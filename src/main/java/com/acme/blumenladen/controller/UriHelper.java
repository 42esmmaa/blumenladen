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

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/// Hilfsklasse um URIs für den Location-Header zu bauen.
@Component
class UriHelper {
    UriHelper() {
    }
    /// Basis-URI aus dem aktuellen Request ermitteln.
    ///
    /// @param request Das aktuelle Request-Objekt.
    /// @return Die Basis-URI.
    URI getBaseUri(final HttpServletRequest request) {
        return ServletUriComponentsBuilder
            .fromRequestUri(request)
            .build()
            .toUri();
    }
}
