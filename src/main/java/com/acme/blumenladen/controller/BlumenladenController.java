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

import com.acme.blumenladen.service.BlumenladenService;
import com.acme.blumenladen.service.NotFoundException;
import java.util.Collection;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import static com.acme.blumenladen.controller.Constants.API_PATH;
import static com.acme.blumenladen.controller.Constants.ID_PATTERN;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

/// REST-Schnittstelle für lesende Zugriffe auf Blumenläden.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
@RestController
@RequestMapping(API_PATH)
@SuppressWarnings({"java:S1075", "ParameterHidesMemberVariable"})
class BlumenladenController {
    private static final String DEFAULT_BLUMEN = "false";

    private final BlumenladenService service;
    private final LazyConstant<Logger> logger =
        LazyConstant.of(() -> LoggerFactory.getLogger(BlumenladenController.class));

    /// Konstruktor mit _package private_ für _Constructor Injection_ bei _Spring_.
    ///
    /// @param service Injiziertes Service-Objekt.
    BlumenladenController(final BlumenladenService service) {
        this.service = service;
    }

    /// Suche anhand der Blumenladen-ID als Pfad-Parameter mit bedingtem GET-Request.
    ///
    /// @param id ID des zu suchenden Blumenladens.
    /// @param blumen Flag, ob die Blumen mitgeladen werden (default: false).
    /// @param ifNoneMatch Versionsnummer aus dem Header `If-None-Match`.
    /// @return Response mit Statuscode 200 und gefundenem Blumenladen, 304 falls unverändert, oder 404.
    @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Object> getById(
        @PathVariable final UUID id,
        @RequestParam(defaultValue = DEFAULT_BLUMEN) final boolean blumen,
        @RequestHeader("If-None-Match") @Nullable final String ifNoneMatch
    ) {
        logger.get().debug("getById: id={}, blumen={}, ifNoneMatch={}", id, blumen, ifNoneMatch);
        if (blumen) {
            return getByIdMitLeiterUndBlumen(id, ifNoneMatch);
        }
        return getByIdMitLeiter(id, ifNoneMatch);
    }

    private ResponseEntity<Object> getByIdMitLeiter(final UUID id, @Nullable final String ifNoneMatch) {
        final var blumenladen = service.findByIdMitLeiter(id);
        final var versionStr = "\"" + blumenladen.getVersion() + '"';
        if (versionStr.equals(ifNoneMatch)) {
            logger.get().trace("getByIdMitLeiter: NOT_MODIFIED, version={}", ifNoneMatch);
            return status(NOT_MODIFIED).build();
        }
        logger.get().trace("getByIdMitLeiter: blumenladen={}, leiter={}", blumenladen, blumenladen.getLeiter());
        // DTO-Objekt _ohne_ Blumen, damit Jackson kein nicht-geladenes Proxy-Objekt serialisiert (Statuscode 500)
        return ok().eTag(versionStr).body(BlumenladenOhneBlumen.of(blumenladen));
    }

    private ResponseEntity<Object> getByIdMitLeiterUndBlumen(final UUID id, @Nullable final String ifNoneMatch) {
        final var blumenladen = service.findByIdMitLeiterUndBlumen(id);
        final var versionStr = "\"" + blumenladen.getVersion() + '"';
        if (versionStr.equals(ifNoneMatch)) {
            logger.get().trace("getByIdMitLeiterUndBlumen: NOT_MODIFIED, version={}", ifNoneMatch);
            return status(NOT_MODIFIED).build();
        }
        logger.get().trace("getByIdMitLeiterUndBlumen: blumenladen={}, blumen={}",
            blumenladen, blumenladen.getBlumen());
        return ok().eTag(versionStr).body(blumenladen);
    }

    /// Suche mit diversen Query-Parametern.
    ///
    /// @param queryparam Query-Parameter als Map.
    /// @return Gefundene Blumenläden ohne Blumen als [Collection].
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    Collection<BlumenladenOhneBlumen> get(@RequestParam final MultiValueMap<String, String> queryparam) {
        logger.get().debug("get: queryparam={}", queryparam);
        final var blumenlaeden = service.find(queryparam)
            .stream()
            .map(BlumenladenOhneBlumen::of)
            .toList();
        logger.get().debug("get: blumenlaeden={}", blumenlaeden);
        return blumenlaeden;
    }

    /// Behandlung von [NotFoundException] → HTTP 404.
    ///
    /// @param ex Die ausgelöste Exception.
    /// @return ProblemDetail mit Fehlerbeschreibung.
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ProblemDetail handleNotFoundException(final NotFoundException ex) {
        logger.get().debug("handleNotFoundException: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
