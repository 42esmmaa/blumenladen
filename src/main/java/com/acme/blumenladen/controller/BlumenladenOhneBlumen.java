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

import com.acme.blumenladen.entity.Blumenladen;
import com.acme.blumenladen.entity.Leiter;
import java.time.LocalTime;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/// ValueObject für einen gefundenen Blumenladen ohne die nicht-mitgeladenen Blumen, d.h. ohne ein
/// nicht-serialisierbares Proxy-Objekt. Dadurch wirft Jackson keinen Statuscode 500 wegen _Lazy Fetching_.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
/// @param id Die ID des Blumenladens.
/// @param name Der Name des Blumenladens.
/// @param adresse Die Adresse des Blumenladens.
/// @param telefonnummer Die Telefonnummer des Blumenladens.
/// @param open Ob der Blumenladen geöffnet ist.
/// @param oeffnungszeit Die Öffnungszeit des Blumenladens.
/// @param schliesszeit Die Schließzeit des Blumenladens.
/// @param leiter Der Leiter des Blumenladens.
@SuppressWarnings("RecordComponentNumber")
record BlumenladenOhneBlumen(
    @Nullable UUID id,
    String name,
    String adresse,
    String telefonnummer,
    boolean open,
    LocalTime oeffnungszeit,
    LocalTime schliesszeit,
    Leiter leiter
) {
    /// DTO-Objekt ohne Blumen aus einem Blumenladen-Objekt erstellen.
    ///
    /// @param blumenladen Blumenladen-Objekt als Ausgangspunkt.
    /// @return DTO-Objekt ohne Blumen.
    static BlumenladenOhneBlumen of(final Blumenladen blumenladen) {
        return new BlumenladenOhneBlumen(
            blumenladen.getId(),
            blumenladen.getName(),
            blumenladen.getAdresse(),
            blumenladen.getTelefonnummer(),
            blumenladen.isOpen(),
            blumenladen.getOeffnungszeit(),
            blumenladen.getSchliesszeit(),
            blumenladen.getLeiter()
        );
    }
}
