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

import com.acme.blumenladen.entity.Blume;
import com.acme.blumenladen.entity.Blumenladen;
import com.acme.blumenladen.entity.Leiter;
import java.util.List;
import org.springframework.stereotype.Component;

/// Mapper zwischen DTO-Objekten und Entity-Objekten.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
@Component
class BlumenladenMapper {
    BlumenladenMapper() {
    }

    /// Ein [BlumenladenDTO]-Objekt in ein neues [Blumenladen]-Objekt (ohne ID, Version, Zeitstempel) umwandeln.
    ///
    /// @param dto Das DTO-Objekt.
    /// @return Das Entity-Objekt.
    Blumenladen toBlumenladen(final BlumenladenDTO dto) {
        final var leiterDTO = dto.leiter();
        final var leiter = new Leiter(null, leiterDTO.vorname(), leiterDTO.nachname(), leiterDTO.telefonnummer());

        final var blumenDTO = dto.blumen();
        final var blumen = blumenDTO == null
            ? List.<Blume>of()
            : blumenDTO.stream()
              .map(blume -> new Blume(null, blume.name(), blume.farbe(), blume.preis(), blume.lagerbestand()))
              .toList();

        return new Blumenladen(
            null,
            0,
            dto.name(),
            dto.adresse(),
            dto.telefonnummer(),
            dto.open(),
            dto.oeffnungszeit(),
            dto.schliesszeit(),
            leiter,
            blumen,
            "user",
            null,
            null
        );
    }
}
