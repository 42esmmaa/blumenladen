-- Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with this program.  If not, see <https://www.gnu.org/licenses/>.

-- docker compose exec postgres bash
-- psql --dbname=blumenladen --username=blumenladen [--file=/sql/V1.0__create_table.sql]

-- https://www.postgresql.org/docs/current/manage-ag-tablespaces.html
SET default_tablespace = blumenladenspace;

-- text statt varchar(n):
-- "There is no performance difference among these three types, apart from a few extra CPU cycles
-- to check the length when storing into a length-constrained column"

-- https://www.postgresql.org/docs/current/sql-createtable.html
-- https://www.postgresql.org/docs/current/datatype.html
-- https://www.postgresql.org/docs/current/functions-uuid.html

-- Leiter: eingebettetes Objekt in Blumenladen (kein eigener Aggregate Root)
-- wird als separate Tabelle mit FK in blumenladen abgebildet (1:1)
CREATE TABLE IF NOT EXISTS leiter (
                                      id            UUID PRIMARY KEY DEFAULT uuidv7(),
    -- https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-INT
    version       INTEGER NOT NULL DEFAULT 0,
    vorname       TEXT NOT NULL,
    nachname      TEXT NOT NULL,
    -- https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-REGEXP
    telefonnummer TEXT NOT NULL CHECK (telefonnummer ~ '^\+?[0-9\s\-]{7,20}$'),
    username      TEXT NOT NULL,
    -- https://www.postgresql.org/docs/current/datatype-datetime.html
    erzeugt       TIMESTAMP NOT NULL,
    aktualisiert  TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS blumenladen (
                                           id            UUID PRIMARY KEY DEFAULT uuidv7(),
    -- https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-INT
    version       INTEGER NOT NULL DEFAULT 0,
    name          TEXT NOT NULL,
    adresse       TEXT NOT NULL,
    -- https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-REGEXP
    telefonnummer TEXT NOT NULL CHECK (telefonnummer ~ '^\+?[0-9\s\-]{7,20}$'),
    -- https://www.postgresql.org/docs/current/datatype-boolean.html
    open          BOOLEAN NOT NULL DEFAULT FALSE,
    -- https://www.postgresql.org/docs/current/datatype-datetime.html
    oeffnungszeit TIME NOT NULL,
    schliesszeit  TIME NOT NULL,
    -- 1:1 Beziehung zum Leiter (UNIQUE = jeder Leiter leitet genau einen Laden)
    -- https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-FK
    leiter_id     UUID NOT NULL UNIQUE REFERENCES leiter,
    username      TEXT NOT NULL,
    erzeugt       TIMESTAMP NOT NULL,
    aktualisiert  TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS blume (
                                     id            UUID PRIMARY KEY DEFAULT uuidv7(),
    -- https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-INT
    version       INTEGER NOT NULL DEFAULT 0,
    name          TEXT NOT NULL,
    farbe         TEXT NOT NULL,
    -- https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-NUMERIC-DECIMAL
    -- https://www.postgresql.org/docs/current/datatype-money.html
    -- 10 Stellen, davon 2 Nachkommastellen
    preis         NUMERIC(10,2) NOT NULL CHECK (preis >= 0),
    lagerbestand  INTEGER NOT NULL DEFAULT 0 CHECK (lagerbestand >= 0),
    -- nicht NOT NULL wegen gerichteter Beziehung und UPDATE durch Hibernate
    blumenladen_id UUID REFERENCES blumenladen,
    idx           INTEGER,
    username      TEXT NOT NULL,
    erzeugt       TIMESTAMP NOT NULL,
    aktualisiert  TIMESTAMP NOT NULL
    );
