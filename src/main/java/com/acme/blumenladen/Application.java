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
package com.acme.blumenladen;

import com.acme.blumenladen.config.DevConfig;
import com.acme.blumenladen.config.WebConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import static com.acme.blumenladen.Banner.TEXT;

/// Klasse mit der `main`-Methode für die Anwendung auf Basis von _Spring Boot_.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
@ConfigurationPropertiesScan
@EnableJpaRepositories
@EnableAsync
@SpringBootApplication(proxyBeanMethods = false)
@Import({WebConfig.class, DevConfig.class})
@SuppressWarnings({"ImplicitSubclassInspection", "ClassUnconnectedToPackage"})
public final class Application {
    private Application() {
    }

    /// Hauptprogramm, um den Microservice zu starten.
    ///
    /// @param args Evtl. zusätzliche Argumente für den Start des Microservice
    static void main(final String... args) {
        new SpringApplicationBuilder(Application.class)
            .banner((_, _, out) -> out.println(TEXT))
            .run(args);
    }
}
