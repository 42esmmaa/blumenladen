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

import com.jayway.jsonpath.JsonPath;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;
import static com.acme.blumenladen.config.DevConfig.DEV;
import static com.acme.blumenladen.controller.Constants.API_PATH;
import static com.acme.blumenladen.controller.TestConstants.API_VERSION_INSERTER;
import static com.acme.blumenladen.controller.TestConstants.HOST;
import static com.acme.blumenladen.controller.TestConstants.REQUEST_FACTORY;
import static com.acme.blumenladen.controller.TestConstants.SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.condition.JRE.JAVA_26;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Tag("integration")
@Tag("rest")
@Tag("rest-get")
@DisplayName("REST-Schnittstelle fuer GET-Requests testen")
@ExtendWith(SoftAssertionsExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(DEV)
@EnabledForJreRange(min = JAVA_26, max = JAVA_26)
@SuppressWarnings({"WriteTag", "ClassFanOutComplexity", "MissingJavadoc", "MissingJavadocType",
    "JavadocVariable", "PMD.LinguisticNaming"})
class BlumenladenControllerTest {
    private static final String ID_VORHANDEN = "10000001-0000-7000-8000-000000000000";
    private static final String NAME_VORHANDEN = "Rosen";
    private static final String NAME_PARAM = "name";

    private final BlumenladenRepository blumenladenRepo;

    @InjectSoftAssertions
    @SuppressWarnings("NullAway.Init")
    private SoftAssertions softly;

    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    BlumenladenControllerTest(@LocalServerPort final int port, final ApplicationContext ctx) {
        assertThat(ctx).isNotNull();
        final var controller = ctx.getBean(BlumenladenController.class);
        assertThat(controller).isNotNull();

        final var baseUrl = UriComponentsBuilder.newInstance()
            .scheme(SCHEMA).host(HOST).port(port).path(API_PATH)
            .build().toUriString();

        final var restClient = RestClient.builder()
            .requestFactory(REQUEST_FACTORY)
            .apiVersionInserter(API_VERSION_INSERTER)
            .baseUrl(baseUrl)
            .build();
        final var proxyFactory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient)).build();
        blumenladenRepo = proxyFactory.createClient(BlumenladenRepository.class);
    }

    @ParameterizedTest(name = "[{index}] Suche mit vorhandener ID: id={0}")
    @ValueSource(strings = ID_VORHANDEN)
    @DisplayName("Suche mit vorhandener ID")
    void getById(final String id) {
        // when
        final var response = blumenladenRepo.getByIdAsString(id);

        // then
        final var body = response.getBody();
        assertThat(body).isNotNull().isNotBlank();
        final String idResult = JsonPath.read(body, "$.id");
        softly.assertThat(idResult).isEqualTo(id);
        final String name = JsonPath.read(body, "$.name");
        softly.assertThat(name).isNotNull().isNotBlank();
        final String adresse = JsonPath.read(body, "$.adresse");
        softly.assertThat(adresse).isNotNull().isNotBlank();
        final Object leiter = JsonPath.read(body, "$.leiter");
        softly.assertThat(leiter).isNotNull();
    }

    @ParameterizedTest(name = "[{index}] Suche mit vorhandenem Namen: name={0}")
    @ValueSource(strings = NAME_VORHANDEN)
    @DisplayName("Suche mit vorhandenem Namen")
    void getByName(final String name) {
        // given
        final var suchparameter = MultiValueMap.fromSingleValue(Map.of(NAME_PARAM, name));

        // when
        final var responseBody = blumenladenRepo.getAllAsString(suchparameter);

        // then
        assertThat(responseBody).isNotNull().isNotBlank();
        final List<String> namen = JsonPath.read(responseBody, "$[*].name");
        softly.assertThat(namen).isNotEmpty();
        namen.forEach(nameTmp -> softly.assertThat(nameTmp).contains(name));
    }
}
