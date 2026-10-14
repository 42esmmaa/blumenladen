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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.blumenladen.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;
import static com.acme.blumenladen.config.DevConfig.DEV;
import static com.acme.blumenladen.controller.Constants.API_PATH;
import static com.acme.blumenladen.controller.Constants.ID_PATTERN;
import static com.acme.blumenladen.controller.TestConstants.API_VERSION_INSERTER;
import static com.acme.blumenladen.controller.TestConstants.HOST;
import static com.acme.blumenladen.controller.TestConstants.REQUEST_FACTORY;
import static com.acme.blumenladen.controller.TestConstants.SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.condition.JRE.JAVA_26;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;

@Tag("integration")
@Tag("rest")
@Tag("rest-write")
@DisplayName("REST-Schnittstelle fuer Schreiben testen")
@ExtendWith(SoftAssertionsExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(DEV)
@DirtiesContext
@EnabledForJreRange(min = JAVA_26, max = JAVA_26)
@SuppressWarnings({"WriteTag", "PMD.AtLeastOneConstructor"})
class BlumenladenWriteControllerTest {
    private static final String NEUER_NAME = "Blumenladen-Neuanlegen-Rest";
    private static final String NEUE_ADRESSE = "Teststrasse42";
    private static final String NEUE_TELEFONNUMMER = "0721123456";
    private static final String NEUE_OEFFNUNGSZEIT = "08:00";
    private static final String NEUE_SCHLIESSZEIT = "18:00";
    private static final String LEITER_VORNAME = "Test";
    private static final String LEITER_NACHNAME = "Leiter";
    private static final String LEITER_TELEFONNUMMER = "0721654321";
    private static final String NEUER_NAME_INVALID = "?!$";

    private final BlumenladenRepository blumenladenRepo;

    @InjectSoftAssertions
    @SuppressWarnings("NullAway.Init")
    private SoftAssertions softly;

    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    BlumenladenWriteControllerTest(@LocalServerPort final int port, final ApplicationContext ctx) {
        assertThat(ctx).isNotNull();
        final var writeController = ctx.getBean(BlumenladenWriteController.class);
        assertThat(writeController).isNotNull();

        final var uriComponents = UriComponentsBuilder.newInstance()
            .scheme(SCHEMA)
            .host(HOST)
            .port(port)
            .path(API_PATH)
            .build();
        final var baseUrl = uriComponents.toUriString();
        final var restClient = RestClient
            .builder()
            .baseUrl(baseUrl)
            .requestFactory(REQUEST_FACTORY)
            .apiVersionInserter(API_VERSION_INSERTER)
            .build();
        final var clientAdapter = RestClientAdapter.create(restClient);
        final var proxyFactory = HttpServiceProxyFactory.builderFor(clientAdapter).build();
        blumenladenRepo = proxyFactory.createClient(BlumenladenRepository.class);
    }

    @Nested
    @DisplayName("Erzeugen")
    class Erzeugen {
        @ParameterizedTest(name = "[{index}] Neuanlegen eines neuen Blumenladens: name={0}, adresse={1}")
        @CsvSource(
            NEUER_NAME + "," + NEUE_ADRESSE + "," + NEUE_TELEFONNUMMER + "," +
                NEUE_OEFFNUNGSZEIT + "," + NEUE_SCHLIESSZEIT + "," +
                LEITER_VORNAME + "," + LEITER_NACHNAME + "," + LEITER_TELEFONNUMMER
        )
        @DisplayName("Neuanlegen eines neuen Blumenladens")
        @SuppressWarnings({"BooleanExpressionComplexity", "CyclomaticComplexity", "PMD.CyclomaticComplexity"})
        void post(final ArgumentsAccessor args) {
            // given
            final var name = args.getString(0);
            final var adresse = args.getString(1);
            final var telefonnummer = args.getString(2);
            final var oeffnungszeitStr = args.getString(3);
            final var schliesszeittStr = args.getString(4);
            final var leiterVorname = args.getString(5);
            final var leiterNachname = args.getString(6);
            final var leiterTelefonnummer = args.getString(7);
            if (name == null || adresse == null || telefonnummer == null || oeffnungszeitStr == null ||
                schliesszeittStr == null || leiterVorname == null || leiterNachname == null ||
                leiterTelefonnummer == null) {
                throw new IllegalStateException("Testdaten sind null");
            }
            final var oeffnungszeit = LocalTime.parse(oeffnungszeitStr);
            final var schliesszeit = LocalTime.parse(schliesszeittStr);

            final var leiterDTO = new LeiterDTO(leiterVorname, leiterNachname, leiterTelefonnummer);
            final var blumeDTO = new BlumeDTO(
                "Testrose",
                "Rot",
                new BigDecimal("2.99"),
                10
            );
            final var blumenladenDTO = new BlumenladenDTO(
                name,
                adresse,
                telefonnummer,
                true,
                oeffnungszeit,
                schliesszeit,
                leiterDTO,
                List.of(blumeDTO)
            );

            // when
            final var response = blumenladenRepo.post(blumenladenDTO);

            // then
            assertThat(response).isNotNull();
            softly.assertThat(response.getStatusCode()).isEqualTo(CREATED);
            final var location = response.getHeaders().getLocation();
            assertThat(location)
                .isNotNull()
                .isInstanceOf(URI.class);
            assertThat(location.toString()).matches(".*/api/" + ID_PATTERN + '$');
        }

    }
}
