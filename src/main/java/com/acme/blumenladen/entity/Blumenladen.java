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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.Nullable;
import static com.acme.blumenladen.entity.Blumenladen.LEITER_BLUMEN_GRAPH;
import static com.acme.blumenladen.entity.Blumenladen.LEITER_GRAPH;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static org.hibernate.annotations.UuidGenerator.Style.VERSION_7;

/// Daten eines Blumenladens. In DDD ist Blumenladen ein Aggregate Root.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
@Entity
@NamedEntityGraph(name = LEITER_GRAPH, attributeNodes = @NamedAttributeNode("leiter"))
@NamedEntityGraph(name = LEITER_BLUMEN_GRAPH, attributeNodes = {
    @NamedAttributeNode("leiter"), @NamedAttributeNode("blumen")
})
@SuppressWarnings({
    "ClassFanOutComplexity",
    "RequireEmptyLineBeforeBlockTagGroup",
    "DeclarationOrder",
    "JavadocDeclaration",
    "MissingSummary",
    "RedundantSuppression",
    "ParameterHidesMemberVariable",
    "ParameterNumber",
    "com.intellij.jpb.LombokEqualsAndHashCodeInspection"
})
public class Blumenladen {
    /// NamedEntityGraph für das Attribut "leiter".
    public static final String LEITER_GRAPH = "Blumenladen.leiter";

    /// NamedEntityGraph für die Attribute "leiter" und "blumen".
    public static final String LEITER_BLUMEN_GRAPH = "Blumenladen.leiterBlumen";

    /// Die ID des Blumenladens.
    @Id
    @GeneratedValue
    @UuidGenerator(style = VERSION_7)
    @Nullable
    private UUID id;

    /// Versionsnummer für optimistische Synchronisation.
    @Version
    private int version;

    /// Der Name des Blumenladens.
    private String name;

    /// Die Adresse des Blumenladens.
    private String adresse;

    /// Die Telefonnummer des Blumenladens.
    private String telefonnummer;

    /// Ob der Blumenladen geöffnet ist.
    private boolean open;

    /// Die Öffnungszeit des Blumenladens.
    private LocalTime oeffnungszeit;

    /// Die Schließzeit des Blumenladens.
    private LocalTime schliesszeit;

    /// 1:1-Beziehung zum Leiter des Blumenladens.
    @OneToOne(optional = false, cascade = {PERSIST, REMOVE}, fetch = LAZY, orphanRemoval = true)
    private Leiter leiter;

    /// 1:N-Beziehung zu den Blumen des Blumenladens.
    // Default: fetch=LAZY
    @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "blumenladen_id")
    @OrderColumn(name = "idx", nullable = false)
    @Nullable
    private List<Blume> blumen;

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
    public Blumenladen() {
    }

    /// Konstruktor für eine Builder-Klasse.
    ///
    /// @param id Die ID des Blumenladens.
    /// @param version Die Version.
    /// @param name Der Name des Blumenladens.
    /// @param adresse Die Adresse des Blumenladens.
    /// @param telefonnummer Die Telefonnummer des Blumenladens.
    /// @param isOpen Ob der Blumenladen geöffnet ist.
    /// @param oeffnungszeit Die Öffnungszeit des Blumenladens.
    /// @param schliesszeit Die Schließzeit des Blumenladens.
    /// @param leiter Der Leiter des Blumenladens.
    /// @param blumen Die Blumen des Blumenladens.
    /// @param username Der Benutzername.
    /// @param erzeugt Erzeugungszeitstempel.
    /// @param aktualisiert Aktualisierungszeitstempel.
    public Blumenladen(
        @Nullable final UUID id,
        final int version,
        final String name,
        final String adresse,
        final String telefonnummer,
        final boolean isOpen,
        final LocalTime oeffnungszeit,
        final LocalTime schliesszeit,
        final Leiter leiter,
        @Nullable final List<Blume> blumen,
        final String username,
        @Nullable final LocalDateTime erzeugt,
        @Nullable final LocalDateTime aktualisiert
    ) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.adresse = adresse;
        this.telefonnummer = telefonnummer;
        open = isOpen;
        this.oeffnungszeit = oeffnungszeit;
        this.schliesszeit = schliesszeit;
        this.leiter = leiter;
        this.blumen = blumen;
        this.username = username;
        this.erzeugt = erzeugt;
        this.aktualisiert = aktualisiert;
    }

    /// Blumenladendaten überschreiben (ohne Beziehungen, ID, Version, Zeitstempel).
    ///
    /// @param blumenladen Neue Blumenladendaten.
    public void set(final Blumenladen blumenladen) {
        name = blumenladen.name;
        adresse = blumenladen.adresse;
        telefonnummer = blumenladen.telefonnummer;
        open = blumenladen.open;
        oeffnungszeit = blumenladen.oeffnungszeit;
        schliesszeit = blumenladen.schliesszeit;
    }

    /// Die ID des Blumenladens ermitteln.
    /// @return Die ID des Blumenladens.
    @Nullable
    public UUID getId() {
        return id;
    }

    /// Die ID des Blumenladens setzen.
    /// @param id Die ID des Blumenladens.
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

    /// Den Namen des Blumenladens ermitteln.
    /// @return Der Name des Blumenladens.
    public String getName() {
        return name;
    }

    /// Den Namen des Blumenladens setzen.
    /// @param name Der Name des Blumenladens.
    public void setName(final String name) {
        this.name = name;
    }

    /// Die Adresse des Blumenladens ermitteln.
    /// @return Die Adresse des Blumenladens.
    public String getAdresse() {
        return adresse;
    }

    /// Die Adresse des Blumenladens setzen.
    /// @param adresse Die Adresse des Blumenladens.
    public void setAdresse(final String adresse) {
        this.adresse = adresse;
    }

    /// Die Telefonnummer des Blumenladens ermitteln.
    /// @return Die Telefonnummer des Blumenladens.
    public String getTelefonnummer() {
        return telefonnummer;
    }

    /// Die Telefonnummer des Blumenladens setzen.
    /// @param telefonnummer Die Telefonnummer des Blumenladens.
    public void setTelefonnummer(final String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    /// Ermitteln, ob der Blumenladen geöffnet ist.
    /// @return true, falls der Blumenladen geöffnet ist.
    public boolean isOpen() {
        return open;
    }

    /// Den Öffnungsstatus des Blumenladens setzen.
    /// @param open Der Öffnungsstatus des Blumenladens.
    public void setOpen(final boolean open) {
        this.open = open;
    }

    /// Die Öffnungszeit des Blumenladens ermitteln.
    /// @return Die Öffnungszeit des Blumenladens.
    public LocalTime getOeffnungszeit() {
        return oeffnungszeit;
    }

    /// Die Öffnungszeit des Blumenladens setzen.
    /// @param oeffnungszeit Die Öffnungszeit des Blumenladens.
    public void setOeffnungszeit(final LocalTime oeffnungszeit) {
        this.oeffnungszeit = oeffnungszeit;
    }

    /// Die Schließzeit des Blumenladens ermitteln.
    /// @return Die Schließzeit des Blumenladens.
    public LocalTime getSchliesszeit() {
        return schliesszeit;
    }

    /// Die Schließzeit des Blumenladens setzen.
    /// @param schliesszeit Die Schließzeit des Blumenladens.
    public void setSchliesszeit(final LocalTime schliesszeit) {
        this.schliesszeit = schliesszeit;
    }

    /// Den Leiter des Blumenladens ermitteln.
    /// @return Der Leiter des Blumenladens.
    public Leiter getLeiter() {
        return leiter;
    }

    /// Den Leiter des Blumenladens setzen.
    /// @param leiter Der Leiter des Blumenladens.
    public void setLeiter(final Leiter leiter) {
        this.leiter = leiter;
    }

    /// Die Blumen des Blumenladens ermitteln.
    /// @return Die Blumen des Blumenladens.
    @Nullable
    public List<Blume> getBlumen() {
        return blumen;
    }

    /// Die Blumen des Blumenladens setzen.
    /// @param blumen Die Blumen des Blumenladens.
    public void setBlumen(final List<Blume> blumen) {
        this.blumen = blumen;
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
        return other instanceof final Blumenladen blumenladen && Objects.equals(id, blumenladen.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // Ohne `leiter` und `blumen`, damit _Lazy Fetching_ nicht stattfindet.
    @Override
    public String toString() {
        return "Blumenladen{" +
            "id=" + id +
            ", version=" + version +
            ", name='" + name + '\'' +
            ", adresse='" + adresse + '\'' +
            ", telefonnummer='" + telefonnummer + '\'' +
            ", open=" + open +
            ", oeffnungszeit=" + oeffnungszeit +
            ", schliesszeit=" + schliesszeit +
            ", username='" + username + '\'' +
            ", erzeugt=" + erzeugt +
            ", aktualisiert=" + aktualisiert +
            '}';
    }
}
