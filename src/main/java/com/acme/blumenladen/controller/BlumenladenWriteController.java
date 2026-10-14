/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.acme.blumenladen.controller;

import com.acme.blumenladen.controller.BlumenladenDTO.OnCreate;
import com.acme.blumenladen.service.BlumenladenWriteService;
import com.acme.blumenladen.service.NotFoundException;
import com.acme.blumenladen.service.VersionOutdatedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.groups.Default;
import java.net.URI;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import static com.acme.blumenladen.controller.Constants.API_PATH;
import static com.acme.blumenladen.controller.Constants.ID_PATTERN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;

/// Eine Controller-Klasse bildet die REST-Schnittstelle für schreibende Zugriffe, wobei die HTTP-Methoden, Pfade
/// und MIME-Typen auf die Methoden der Klasse abgebildet werden.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
@Controller
@Validated
@RequestMapping(API_PATH)
@SuppressWarnings({"ClassFanOutComplexity", "MethodCount", "java:S1075", "ParameterHidesMemberVariable"})
class BlumenladenWriteController {
    private static final String VERSIONSNUMMER_FEHLT = "Versionsnummer fehlt";

    private final BlumenladenWriteService service;
    private final BlumenladenMapper mapper;
    private final UriHelper uriHelper;
    private final LazyConstant<Logger> logger =
        LazyConstant.of(() -> LoggerFactory.getLogger(BlumenladenWriteController.class));

    /// Konstruktor mit _package private_ für _Constructor Injection_ bei _Spring_.
    ///
    /// @param service Injiziertes Service-Objekt.
    /// @param mapper Injiziertes Mapper-Objekt für das Mapping von DTO-Objekten auf Entity-Objekte.
    /// @param uriHelper Injiziertes Helper-Objekt, um URIs für den Header `Location` zu bauen.
    BlumenladenWriteController(
        final BlumenladenWriteService service,
        final BlumenladenMapper mapper,
        final UriHelper uriHelper
    ) {
        this.service = service;
        this.mapper = mapper;
        this.uriHelper = uriHelper;
    }

    /// Einen neuen Blumenladen-Datensatz anlegen.
    ///
    /// @param blumenladenDTO Das Blumenladenobjekt aus dem eingegangenen Request-Body.
    /// @param request Das Request-Objekt, um `Location` im Response-Header zu erstellen.
    /// @return Response mit Statuscode 201 einschließlich Location-Header oder Statuscode 422, falls Constraints
    ///      verletzt sind oder Statuscode 400, falls syntaktische Fehler im Request-Body vorliegen.
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<Void> post(
        @RequestBody @Validated({Default.class, OnCreate.class}) final BlumenladenDTO blumenladenDTO,
        final HttpServletRequest request
    ) {
        logger.get().debug("post: {}", blumenladenDTO);

        final var blumenladenInput = mapper.toBlumenladen(blumenladenDTO);
        final var blumenladen = service.create(blumenladenInput);
        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var location = URI.create(baseUri + '/' + blumenladen.getId());
        return created(location).build();
    }

    /// Einen vorhandenen Blumenladen-Datensatz überschreiben.
    ///
    /// @param id ID des zu aktualisierenden Blumenladens.
    /// @param blumenladenDTO Das Blumenladenobjekt aus dem eingegangenen Request-Body.
    /// @param ifMatch Versionsnummer aus dem Header `If-Match`.
    /// @return Response mit Statuscode 204 und neuem ETag, 400 bei syntaktischem Fehler, 404 falls nicht vorhanden,
    ///      412 falls die Versionsnummer nicht ok ist, 428 falls die Versionsnummer fehlt oder 422 bei
    ///      Constraint-Verletzungen.
    @PutMapping(path = "{id:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<Void> put(
        @PathVariable final UUID id,
        @RequestBody @Validated final BlumenladenDTO blumenladenDTO,
        @RequestHeader("If-Match") @Nullable final String ifMatch
    ) {
        logger.get().debug("put: id={}, {}, ifMatch={}", id, blumenladenDTO, ifMatch);
        final int version = getVersion(ifMatch);
        final var blumenladenInput = mapper.toBlumenladen(blumenladenDTO);
        final var blumenladen = service.update(blumenladenInput, id, version);
        logger.get().debug("put: {}", blumenladen);
        return noContent().eTag("\"" + blumenladen.getVersion() + '"').build();
    }

    @SuppressWarnings({"MagicNumber", "RedundantSuppression"})
    private int getVersion(@Nullable final String versionStr) {
        logger.get().trace("getVersion: {}", versionStr);
        if (versionStr == null) {
            throw new VersionInvalidException(PRECONDITION_REQUIRED, VERSIONSNUMMER_FEHLT);
        }
        if (versionStr.length() < 3 ||
            versionStr.charAt(0) != '"' ||
            versionStr.charAt(versionStr.length() - 1) != '"') {
            throw new VersionInvalidException(PRECONDITION_FAILED, "Ungueltiges ETag " + versionStr);
        }

        final int version;
        try {
            version = Integer.parseInt(versionStr.substring(1, versionStr.length() - 1));
        } catch (final NumberFormatException ex) {
            throw new VersionInvalidException(PRECONDITION_FAILED, "Ungueltiges ETag " + versionStr, ex);
        }

        logger.get().trace("getVersion: version={}", version);
        return version;
    }

    /// Einen vorhandenen Blumenladen anhand seiner ID löschen.
    ///
    /// @param id ID des zu löschenden Blumenladens.
    @DeleteMapping(path = "{id:" + ID_PATTERN + "}")
    @ResponseStatus(NO_CONTENT)
    void deleteById(@PathVariable final UUID id) {
        logger.get().debug("deleteById: id={}", id);
        service.deleteById(id);
    }

    /// [ExceptionHandler] für [MethodArgumentNotValidException].
    ///
    /// @param ex Exception für Fehler im Request-Body bei `POST` oder `PUT` gemäß _Jakarta Validation_.
    /// @return ErrorResponse mit `ProblemDetail` gemäß _RFC 9457_.
    @ExceptionHandler
    ErrorResponse onConstraintViolations(final MethodArgumentNotValidException ex) {
        logger.get().debug("onConstraintViolations: {}", ex.getMessage());

        final var detailMessages = ex.getDetailMessageArguments();
        final var detail = detailMessages == null || detailMessages.length == 0 || detailMessages[1] == null
            ? "Constraint Violation"
            : ((String) detailMessages[1]).replace(", and ", ", ");
        return ErrorResponse.create(ex, UNPROCESSABLE_CONTENT, detail);
    }

    /// [ExceptionHandler] für [HttpMessageNotReadableException].
    ///
    /// @param ex Exception für den syntaktisch falschen Request-Body bei `POST` oder `PUT`.
    /// @return ErrorResponse mit `ProblemDetail` gemäß _RFC 9457_.
    @ExceptionHandler
    ErrorResponse onMessageNotReadable(final HttpMessageNotReadableException ex) {
        final var msg = ex.getMessage() == null ? "N/A" : ex.getMessage();
        logger.get().debug("onMessageNotReadable: {}", msg);
        return ErrorResponse.create(ex, BAD_REQUEST, msg);
    }

    /// [ExceptionHandler], falls bei einem `PUT`-Request die Version fehlt oder syntaktisch falsch ist.
    ///
    /// @param ex Exception vom Typ `VersionInvalidException`.
    /// @return ErrorResponse mit `ProblemDetail` gemäß _RFC 9457_.
    @ExceptionHandler
    ErrorResponse onVersionInvalid(final VersionInvalidException ex) {
        logger.get().debug("onVersionInvalid: {}", ex.getMessage());
        return ErrorResponse.create(ex, ex.getStatus(), ex.getMessage());
    }

    /// [ExceptionHandler], falls bei einem `PUT`-Request die Versionsnummer veraltet ist.
    ///
    /// @param ex Exception vom Typ `VersionOutdatedException`.
    /// @return ErrorResponse mit `ProblemDetail` gemäß _RFC 9457_.
    @ExceptionHandler
    ErrorResponse onVersionOutdated(final VersionOutdatedException ex) {
        logger.get().debug("onVersionOutdated: {}", ex.getMessage());
        return ErrorResponse.create(ex, PRECONDITION_FAILED, ex.getMessage());
    }

    /// [ExceptionHandler] für [NotFoundException] bei einem `PUT`-Request.
    ///
    /// @param ex Die ausgelöste Exception.
    /// @return ProblemDetail mit Fehlerbeschreibung.
    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    ProblemDetail onNotFoundException(final NotFoundException ex) {
        logger.get().debug("onNotFoundException: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
    }
}
