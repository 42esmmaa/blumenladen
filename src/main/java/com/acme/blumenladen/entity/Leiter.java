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
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.Nullable;
import static org.hibernate.annotations.UuidGenerator.Style.VERSION_7;

/// Daten eines Leiters eines Blumenladens.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
@Entity
@SuppressWarnings({"RequireEmptyLineBeforeBlockTagGroup", "MissingSummary", "ParameterHidesMemberVariable"})
public class Leiter {
    /// Die ID des Leiters.
    @Id
    @GeneratedValue
    @UuidGenerator(style = VERSION_7)
    @Nullable
    private UUID id;

    /// Versionsnummer für optimistische Synchronisation.
    @Version
    private int version;

    /// Der Vorname des Leiters.
    private String vorname;

    /// Der Nachname des Leiters.
    private String nachname;

    /// Die Telefonnummer des Leiters.
    private String telefonnummer;

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
    public Leiter() {
    }

    /// Konstruktor für eine Builder-Klasse.
    ///
    /// @param id Die ID des Leiters.
    /// @param vorname Der Vorname des Leiters.
    /// @param nachname Der Nachname des Leiters.
    /// @param telefonnummer Die Telefonnummer des Leiters.
    public Leiter(
        @Nullable final UUID id,
        final String vorname,
        final String nachname,
        final String telefonnummer
    ) {
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.telefonnummer = telefonnummer;
    }

    /// Die ID des Leiters ermitteln.
    /// @return Die ID des Leiters.
    @Nullable
    public UUID getId() {
        return id;
    }

    /// Die ID des Leiters setzen.
    /// @param id Die ID des Leiters.
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

    /// Den Vornamen des Leiters ermitteln.
    /// @return Der Vorname des Leiters.
    public String getVorname() {
        return vorname;
    }

    /// Den Vornamen des Leiters setzen.
    /// @param vorname Der Vorname des Leiters.
    public void setVorname(final String vorname) {
        this.vorname = vorname;
    }

    /// Den Nachnamen des Leiters ermitteln.
    /// @return Der Nachname des Leiters.
    public String getNachname() {
        return nachname;
    }

    /// Den Nachnamen des Leiters setzen.
    /// @param nachname Der Nachname des Leiters.
    public void setNachname(final String nachname) {
        this.nachname = nachname;
    }

    /// Die Telefonnummer des Leiters ermitteln.
    /// @return Die Telefonnummer des Leiters.
    public String getTelefonnummer() {
        return telefonnummer;
    }

    /// Die Telefonnummer des Leiters setzen.
    /// @param telefonnummer Die Telefonnummer des Leiters.
    public void setTelefonnummer(final String telefonnummer) {
        this.telefonnummer = telefonnummer;
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
        return other instanceof final Leiter leiter && Objects.equals(id, leiter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Leiter{" +
            "id=" + id +
            ", version=" + version +
            ", vorname='" + vorname + '\'' +
            ", nachname='" + nachname + '\'' +
            ", telefonnummer='" + telefonnummer + '\'' +
            ", username='" + username + '\'' +
            ", erzeugt=" + erzeugt +
            ", aktualisiert=" + aktualisiert +
            '}';
    }
}
