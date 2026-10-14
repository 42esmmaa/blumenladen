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
package com.acme.blumenladen.service;

import com.acme.blumenladen.entity.Blumenladen;
import com.acme.blumenladen.repository.BlumenladenRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/// Geschäftslogik für Blumenläden für die schreibenden Operationen.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
@Service
@Transactional(readOnly = true)
public class BlumenladenWriteService {
    private final BlumenladenRepository repo;
    private final LazyConstant<Logger> logger =
        LazyConstant.of(() -> LoggerFactory.getLogger(BlumenladenWriteService.class));

    /// Konstruktor mit _package private_ für _Constructor Injection_ bei _Spring_.
    ///
    /// @param repo Injiziertes Repository-Objekt für _Spring Data_.
    BlumenladenWriteService(final BlumenladenRepository repo) {
        this.repo = repo;
    }

    /// Einen neuen Blumenladen anlegen.
    ///
    /// @param blumenladen Das Objekt des neu anzulegenden Blumenladens.
    /// @return Der neu angelegte Blumenladen mit generierter ID.
    @Transactional
    public Blumenladen create(final Blumenladen blumenladen) {
        logger.get().debug("create: blumenladen={}, leiter={}, blumen={}",
            blumenladen, blumenladen.getLeiter(), blumenladen.getBlumen());

        blumenladen.setUsername("user");
        if (blumenladen.getLeiter() != null) {
            blumenladen.getLeiter().setUsername("user");
        }
        final var blumen = blumenladen.getBlumen();
        if (blumen != null) {
            blumen.forEach(blume -> blume.setUsername("user"));
        }

        final var blumenladenDb = repo.save(blumenladen);
        logger.get().debug("create: blumenladenDb={}", blumenladenDb);
        return blumenladenDb;
    }

    /// Einen vorhandenen Blumenladen aktualisieren.
    ///
    /// @param blumenladen Das Objekt mit den neuen Daten (ohne ID).
    /// @param id ID des zu aktualisierenden Blumenladens.
    /// @param version Die erforderliche Version.
    /// @return Aktualisierter Blumenladen mit erhöhter Versionsnummer.
    /// @throws NotFoundException Kein Blumenladen zur ID vorhanden.
    /// @throws VersionOutdatedException Die Versionsnummer ist veraltet und nicht aktuell.
    @Transactional
    public Blumenladen update(final Blumenladen blumenladen, final UUID id, final int version) {
        logger.get().debug("update: blumenladen={}, id={}, version={}", blumenladen, id, version);

        var blumenladenDb = repo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(id));
        logger.get().trace("update: version={}, blumenladenDb={}", version, blumenladenDb);
        if (version != blumenladenDb.getVersion()) {
            throw new VersionOutdatedException(version);
        }

        // Zu ueberschreibende Werte uebernehmen
        blumenladenDb.set(blumenladen);
        blumenladenDb = repo.save(blumenladenDb);

        logger.get().debug("update: {}", blumenladenDb);
        return blumenladenDb;
    }

    /// Einen Blumenladen löschen.
    ///
    /// @param id Die ID des zu löschenden Blumenladens.
    @Transactional
    public void deleteById(final UUID id) {
        logger.get().debug("deleteById: id={}", id);
        repo.findById(id).ifPresent(repo::delete);
    }
}
