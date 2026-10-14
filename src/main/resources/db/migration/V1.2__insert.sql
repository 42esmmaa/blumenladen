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

--  docker compose exec postgres bash
--  psql --dbname=blumenladen --username=blumenladen [--file=/sql/V1.2__insert.sql]

-- COPY mit CSV-Dateien erfordert den Pfad src/main/resources/...
-- Dieser Pfad existiert aber nicht im Docker-Image
-- https://www.postgresql.org/docs/current/sql-copy.html

INSERT INTO leiter (id, version, vorname, nachname, telefonnummer, username, erzeugt, aktualisiert)
VALUES
    -- admin
    ('00000000-0000-7000-8000-000000000000', 0, 'Hans',  'Mueller',  '+49 721 1111111', 'admin', '2024-01-31 00:00:00', '2024-01-31 00:00:00'),
    -- HTTP GET
    ('00000001-0000-7000-8000-000000000000', 0, 'Anna',  'Schmidt',  '+49 721 2222222', 'user',  '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('00000002-0000-7000-8000-000000000000', 0, 'Klaus', 'Weber',    '+49 721 3333333', 'user',  '2024-01-02 00:00:00', '2024-01-02 00:00:00'),
    -- HTTP PUT
    ('00000003-0000-7000-8000-000000000000', 0, 'Maria', 'Fischer',  '+49 721 4444444', 'user',  '2024-01-03 00:00:00', '2024-01-03 00:00:00'),
    -- HTTP DELETE
    ('00000005-0000-7000-8000-000000000000', 0, 'Peter', 'Bauer',    '+49 721 5555555', 'user',  '2024-01-05 00:00:00', '2024-01-05 00:00:00'),
    -- zur freien Verfuegung
    ('00000006-0000-7000-8000-000000000000', 0, 'Laura', 'Hoffmann', '+49 721 6666666', 'user',  '2024-01-06 00:00:00', '2024-01-06 00:00:00');

INSERT INTO blumenladen (id, version, name, adresse, telefonnummer, open, oeffnungszeit, schliesszeit, leiter_id, username, erzeugt, aktualisiert)
VALUES
    -- admin
    ('10000000-0000-7000-8000-000000000000', 0, 'Blumen Mueller',     'Kaiserstrasse 1, 76133 Karlsruhe',      '+49 721 1111100', true,  '08:00', '18:00', '00000000-0000-7000-8000-000000000000', 'admin', '2024-01-31 00:00:00', '2024-01-31 00:00:00'),
    -- HTTP GET
    ('10000001-0000-7000-8000-000000000000', 0, 'Rosen Schmidt',      'Durlacher Allee 5, 76137 Karlsruhe',    '+49 721 2222200', true,  '09:00', '19:00', '00000001-0000-7000-8000-000000000000', 'user',  '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('10000002-0000-7000-8000-000000000000', 0, 'Tulpen Weber',       'Rheinstrasse 12, 76185 Karlsruhe',      '+49 721 3333300', false, '09:00', '17:00', '00000002-0000-7000-8000-000000000000', 'user',  '2024-01-02 00:00:00', '2024-01-02 00:00:00'),
    -- HTTP PUT
    ('10000003-0000-7000-8000-000000000000', 0, 'Blueten Fischer',    'Pfinztalstr. 8, 76227 Karlsruhe',       '+49 721 4444400', true,  '08:30', '18:30', '00000003-0000-7000-8000-000000000000', 'user',  '2024-01-03 00:00:00', '2024-01-03 00:00:00'),
    -- HTTP DELETE
    ('10000005-0000-7000-8000-000000000000', 0, 'Blumen Bauer',       'Waldstrasse 3, 76133 Karlsruhe',        '+49 721 5555500', true,  '10:00', '20:00', '00000005-0000-7000-8000-000000000000', 'user',  '2024-01-05 00:00:00', '2024-01-05 00:00:00'),
    -- zur freien Verfuegung
    ('10000006-0000-7000-8000-000000000000', 0, 'Floristik Hoffmann', 'Ettlinger Str. 9, 76137 Karlsruhe',     '+49 721 6666600', false, '09:00', '17:00', '00000006-0000-7000-8000-000000000000', 'user',  '2024-01-06 00:00:00', '2024-01-06 00:00:00');

INSERT INTO blume (id, version, name, farbe, preis, lagerbestand, blumenladen_id, idx, username, erzeugt, aktualisiert)
VALUES
    -- Blumen fuer Laden admin (10000000)
    ('20000000-0000-7000-8000-000000000000', 0, 'Rote Rose',       'Rot',    3.99,  50, '10000000-0000-7000-8000-000000000000', 0, 'admin', '2024-01-31 00:00:00', '2024-01-31 00:00:00'),
    ('20000001-0000-7000-8000-000000000000', 0, 'Weisse Lilie',    'Weiss',  5.49,  30, '10000000-0000-7000-8000-000000000000', 1, 'admin', '2024-01-31 00:00:00', '2024-01-31 00:00:00'),
    -- Blumen fuer Laden 1 -- HTTP GET
    ('20000010-0000-7000-8000-000000000000', 0, 'Gelbe Tulpe',     'Gelb',   2.49,  80, '10000001-0000-7000-8000-000000000000', 0, 'user',  '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('20000011-0000-7000-8000-000000000000', 0, 'Rosa Tulpe',      'Rosa',   2.99,  60, '10000001-0000-7000-8000-000000000000', 1, 'user',  '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('20000012-0000-7000-8000-000000000000', 0, 'Weisse Tulpe',    'Weiss',  2.79,  40, '10000001-0000-7000-8000-000000000000', 2, 'user',  '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    -- Blumen fuer Laden 2
    ('20000020-0000-7000-8000-000000000000', 0, 'Pink Orchidee',   'Pink',  12.99,  15, '10000002-0000-7000-8000-000000000000', 0, 'user',  '2024-01-02 00:00:00', '2024-01-02 00:00:00'),
    ('20000021-0000-7000-8000-000000000000', 0, 'Lila Orchidee',   'Lila',  14.99,  10, '10000002-0000-7000-8000-000000000000', 1, 'user',  '2024-01-02 00:00:00', '2024-01-02 00:00:00'),
    -- Blumen fuer Laden 3 -- HTTP PUT
    ('20000030-0000-7000-8000-000000000000', 0, 'Sonnenblume',     'Gelb',   1.99, 100, '10000003-0000-7000-8000-000000000000', 0, 'user',  '2024-01-03 00:00:00', '2024-01-03 00:00:00'),
    ('20000031-0000-7000-8000-000000000000', 0, 'Rote Rose',       'Rot',    4.49,  25, '10000003-0000-7000-8000-000000000000', 1, 'user',  '2024-01-03 00:00:00', '2024-01-03 00:00:00'),
    -- Blumen fuer Laden 5 -- HTTP DELETE
    ('20000050-0000-7000-8000-000000000000', 0, 'Weisse Rose',     'Weiss',  3.49,  20, '10000005-0000-7000-8000-000000000000', 0, 'user',  '2024-01-05 00:00:00', '2024-01-05 00:00:00');
