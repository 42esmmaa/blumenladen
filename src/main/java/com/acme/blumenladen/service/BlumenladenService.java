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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/// Geschäftslogik für Blumenläden. Lese-Operationen werden mit Readonly-Transaktionen durchgeführt.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
@Service
@Transactional(readOnly = true)
public class BlumenladenService {
    private static final int SINGLE_PARAM = 1;

    private final BlumenladenRepository repo;
    private final LazyConstant<Logger> logger =
        LazyConstant.of(() -> LoggerFactory.getLogger(BlumenladenService.class));

    /// Konstruktor mit _package private_ für _Constructor Injection_ bei _Spring_.
    ///
    /// @param repo Injiziertes Repository-Objekt für _Spring Data_.
    BlumenladenService(final BlumenladenRepository repo) {
        this.repo = repo;
    }

    /// Einen Blumenladen mit seinem Leiter anhand seiner ID suchen.
    ///
    /// @param id Die Id des gesuchten Blumenladens.
    /// @return Der gefundene Blumenladen.
    /// @throws NotFoundException Falls kein Blumenladen gefunden wurde.
    public Blumenladen findByIdMitLeiter(final UUID id) {
        logger.get().debug("findByIdMitLeiter: id={}", id);
        final var blumenladen = repo.findByIdFetchLeiter(id);
        if (blumenladen == null) {
            throw new NotFoundException(id);
        }
        logger.get().debug("findByIdMitLeiter: blumenladen={}, leiter={}", blumenladen, blumenladen.getLeiter());
        return blumenladen;
    }

    /// Einen Blumenladen mit Leiter und Blumen anhand seiner ID suchen.
    ///
    /// @param id Die Id des gesuchten Blumenladens.
    /// @return Der gefundene Blumenladen.
    /// @throws NotFoundException Falls kein Blumenladen gefunden wurde.
    public Blumenladen findByIdMitLeiterUndBlumen(final UUID id) {
        logger.get().debug("findByIdMitLeiterUndBlumen: id={}", id);
        final var blumenladen = repo.findByIdFetchLeiterUndBlumen(id);
        if (blumenladen == null) {
            throw new NotFoundException(id);
        }
        logger.get().debug(
            "findByIdMitLeiterUndBlumen: blumenladen={}, leiter={}, blumen={}",
            blumenladen, blumenladen.getLeiter(), blumenladen.getBlumen()
        );
        return blumenladen;
    }

    /// Blumenläden anhand von Suchparametern als Collection suchen.
    ///
    /// @param suchparameter Die Suchparameter.
    /// @return Die gefundenen Blumenläden.
    /// @throws NotFoundException Falls keine Blumenläden gefunden wurden.
    public Collection<Blumenladen> find(final Map<String, List<String>> suchparameter) {
        logger.get().debug("find: suchparameter={}", suchparameter);

        if (suchparameter.isEmpty()) {
            return findAll(suchparameter);
        }

        if (suchparameter.size() == SINGLE_PARAM) {
            final var namen = suchparameter.get("name");
            if (namen != null && namen.size() == SINGLE_PARAM) {
                return findByName(namen.getFirst(), suchparameter);
            }
        }

        throw new NotFoundException(suchparameter);
    }

    private Collection<Blumenladen> findAll(final Map<String, List<String>> suchparameter) {
        final var blumenlaeden = repo.findAll();
        if (blumenlaeden.isEmpty()) {
            throw new NotFoundException(suchparameter);
        }
        return blumenlaeden;
    }

    private Collection<Blumenladen> findByName(final String name, final Map<String, List<String>> suchparameter) {
        final var blumenlaeden = repo.findByName(name);
        if (blumenlaeden.isEmpty()) {
            throw new NotFoundException(suchparameter);
        }
        logger.get().debug("find (name): blumenlaeden={}", blumenlaeden);
        return blumenlaeden;
    }
}
