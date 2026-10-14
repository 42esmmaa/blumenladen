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
package com.acme.blumenladen.repository;

import com.acme.blumenladen.entity.Blumenladen;
import java.util.Collection;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import static com.acme.blumenladen.entity.Blumenladen.LEITER_BLUMEN_GRAPH;
import static com.acme.blumenladen.entity.Blumenladen.LEITER_GRAPH;

/// Repository für den DB-Zugriff bei Blumenläden.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
public interface BlumenladenRepository extends JpaRepository<Blumenladen, UUID> {
    /// Alle Blumenläden einschließlich Leiter ermitteln.
    ///
    /// @return Alle Blumenläden.
    @EntityGraph(LEITER_GRAPH)
    @Override
    @SuppressWarnings("NullableProblems")
    java.util.List<Blumenladen> findAll();

    /// Blumenladen einschließlich Leiter anhand der ID suchen.
    ///
    /// @param id Blumenladen-ID.
    /// @return Gefundener Blumenladen oder null.
    @Query("""
        SELECT bl
        FROM   #{#entityName} bl
        WHERE  bl.id = :id
        """)
    @EntityGraph(LEITER_GRAPH)
    @Nullable
    Blumenladen findByIdFetchLeiter(UUID id);

    /// Blumenladen einschließlich Leiter und Blumen anhand der ID suchen.
    ///
    /// @param id Blumenladen-ID.
    /// @return Gefundener Blumenladen oder null.
    @Query("""
        SELECT bl
        FROM   #{#entityName} bl
        WHERE  bl.id = :id
        """)
    @EntityGraph(LEITER_BLUMEN_GRAPH)
    @Nullable
    Blumenladen findByIdFetchLeiterUndBlumen(UUID id);

    /// Blumenläden anhand des Namens suchen.
    ///
    /// @param name Der (Teil-) Name der gesuchten Blumenläden.
    /// @return Die gefundenen Blumenläden oder eine leere Collection.
    @Query("""
        SELECT   bl
        FROM     #{#entityName} bl
        WHERE    lower(bl.name) LIKE '%' || lower(:name) || '%'
        ORDER BY bl.name
        """)
    @EntityGraph(LEITER_GRAPH)
    Collection<Blumenladen> findByName(CharSequence name);
}
