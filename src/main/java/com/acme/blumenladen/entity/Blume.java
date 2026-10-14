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
package com.acme.blumenladen.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.Nullable;
import static org.hibernate.annotations.UuidGenerator.Style.VERSION_7;

/// Daten einer Blume.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
@Entity
@SuppressWarnings({"RequireEmptyLineBeforeBlockTagGroup", "MissingSummary", "ParameterHidesMemberVariable"})
public class Blume {
    /// Die ID der Blume.
    @Id
    @GeneratedValue
    @UuidGenerator(style = VERSION_7)
    @Nullable
    private UUID id;

    /// Versionsnummer für optimistische Synchronisation.
    @Version
    private int version;

    /// Der Name der Blume.
    private String name;

    /// Die Farbe der Blume.
    private String farbe;

    /// Der Preis der Blume.
    private BigDecimal preis;

    /// Der Lagerbestand der Blume.
    private int lagerbestand;

    /// Der Benutzername.
    private String username;

    /// Zeitstempel beim initialen Einfügen in die DB.
    @CreationTimestamp
    @Nullable
    private LocalDateTime erzeugt;

    /// Zeitstempel vom letzten `UPDATE` in der DB.
    @UpdateTimestamp
    @Nullable
    private LocalDateTime aktualisiert;

    /// Standardkonstruktor für _Jakarta Persistence_.
    @SuppressWarnings("NullAway.Init")
    public Blume() {
    }

    /// Konstruktor für eine Builder-Klasse.
    ///
    /// @param id Die ID der Blume.
    /// @param name Der Name der Blume.
    /// @param farbe Die Farbe der Blume.
    /// @param preis Der Preis der Blume.
    /// @param lagerbestand Der Lagerbestand der Blume.
    public Blume(
        @Nullable final UUID id,
        final String name,
        final String farbe,
        final BigDecimal preis,
        final int lagerbestand
    ) {
        this.id = id;
        this.name = name;
        this.farbe = farbe;
        this.preis = preis;
        this.lagerbestand = lagerbestand;
    }

    /// Die ID der Blume ermitteln.
    /// @return Die ID der Blume.
    @Nullable
    public UUID getId() {
        return id;
    }

    /// Die ID der Blume setzen.
    /// @param id Die ID der Blume.
    public void setId(final UUID id) {
        this.id = id;
    }

    /// Version ermitteln.
    /// @return Die Version.
    public int getVersion() {
        return version;
    }

    /// Version setzen.
    /// @param version Die Version.
    public void setVersion(final int version) {
        this.version = version;
    }

    /// Den Namen der Blume ermitteln.
    /// @return Der Name der Blume.
    public String getName() {
        return name;
    }

    /// Den Namen der Blume setzen.
    /// @param name Der Name der Blume.
    public void setName(final String name) {
        this.name = name;
    }

    /// Die Farbe der Blume ermitteln.
    /// @return Die Farbe der Blume.
    public String getFarbe() {
        return farbe;
    }

    /// Die Farbe der Blume setzen.
    /// @param farbe Die Farbe der Blume.
    public void setFarbe(final String farbe) {
        this.farbe = farbe;
    }

    /// Den Preis der Blume ermitteln.
    /// @return Der Preis der Blume.
    public BigDecimal getPreis() {
        return preis;
    }

    /// Den Preis der Blume setzen.
    /// @param preis Der Preis der Blume.
    public void setPreis(final BigDecimal preis) {
        this.preis = preis;
    }

    /// Den Lagerbestand der Blume ermitteln.
    /// @return Der Lagerbestand der Blume.
    public int getLagerbestand() {
        return lagerbestand;
    }

    /// Den Lagerbestand der Blume setzen.
    /// @param lagerbestand Der Lagerbestand der Blume.
    public void setLagerbestand(final int lagerbestand) {
        this.lagerbestand = lagerbestand;
    }

    /// Benutzername ermitteln.
    /// @return Der Benutzername.
    public String getUsername() {
        return username;
    }

    /// Benutzername setzen.
    /// @param username Der Benutzername.
    public void setUsername(final String username) {
        this.username = username;
    }

    /// Das Erstellungsdatum ermitteln.
    /// @return Das Erstellungsdatum.
    @Nullable
    public LocalDateTime getErzeugt() {
        return erzeugt;
    }

    /// Das Erstellungsdatum setzen.
    /// @param erzeugt Das Erstellungsdatum.
    public void setErzeugt(final LocalDateTime erzeugt) {
        this.erzeugt = erzeugt;
    }

    /// Das Datum der letzten Änderung ermitteln.
    /// @return Das Datum der letzten Änderung.
    @Nullable
    public LocalDateTime getAktualisiert() {
        return aktualisiert;
    }

    /// Das Datum der letzten Änderung setzen.
    /// @param aktualisiert Das Datum der letzten Änderung.
    public void setAktualisiert(final LocalDateTime aktualisiert) {
        this.aktualisiert = aktualisiert;
    }

    @Override
    public final boolean equals(final Object other) {
        return other instanceof final Blume blume && Objects.equals(id, blume.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Blume{" +
            "id=" + id +
            ", version=" + version +
            ", name='" + name + '\'' +
            ", farbe='" + farbe + '\'' +
            ", preis=" + preis +
            ", lagerbestand=" + lagerbestand +
            ", username='" + username + '\'' +
            ", erzeugt=" + erzeugt +
            ", aktualisiert=" + aktualisiert +
            '}';
    }
}
