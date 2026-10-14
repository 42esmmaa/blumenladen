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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalTime;
import java.util.List;
import org.jspecify.annotations.Nullable;

/// ValueObject für das Neuanlegen und Ändern eines Blumenladens. Beim Lesen wird die Klasse BlumenladenModel
/// für die Ausgabe verwendet.
///
/// @param name Der Name des Blumenladens.
/// @param adresse Die Adresse des Blumenladens.
/// @param telefonnummer Die Telefonnummer des Blumenladens.
/// @param open Ob der Blumenladen geöffnet ist.
/// @param oeffnungszeit Die Öffnungszeit des Blumenladens.
/// @param schliesszeit Die Schließzeit des Blumenladens.
/// @param leiter Der Leiter des Blumenladens.
/// @param blumen Die Blumen des Blumenladens.
@SuppressWarnings("RecordComponentNumber")
record BlumenladenDTO(
    @NotBlank
    String name,

    @NotBlank
    String adresse,

    @NotNull
    @Pattern(regexp = TELEFONNUMMER_PATTERN)
    String telefonnummer,

    boolean open,

    @NotNull
    LocalTime oeffnungszeit,

    @NotNull
    LocalTime schliesszeit,

    @Valid
    @NotNull(groups = OnCreate.class)
    LeiterDTO leiter,

    @Nullable
    List<@Valid BlumeDTO> blumen
) {
    /// Muster für eine gültige Telefonnummer.
    static final String TELEFONNUMMER_PATTERN = "^\\+?[0-9 ()\\-]{7,20}$";

    /// Marker-Interface für Jakarta Validation: zusätzliche Validierung beim Neuanlegen.
    interface OnCreate { }
}
