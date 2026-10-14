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

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/// ValueObject für eine Blume eines Blumenladens.
///
/// @param name Der Name der Blume.
/// @param farbe Die Farbe der Blume.
/// @param preis Der Preis der Blume.
/// @param lagerbestand Der Lagerbestand der Blume.
record BlumeDTO(
    @NotBlank
    String name,

    @NotBlank
    String farbe,

    @NotNull
    @DecimalMin("0.01")
    BigDecimal preis,

    @Min(0)
    int lagerbestand
) {
}
/// controller und methoden müssen package private sein
/// lazyconstants verwenden
/// kronstruktor servuce klassen package private
/// javadoc in markdown
