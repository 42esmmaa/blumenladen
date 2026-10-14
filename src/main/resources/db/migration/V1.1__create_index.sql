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
-- psql --dbname=blumenladen --username=blumenladen [--file=/sql/V1.1__create_index.sql]

-- https://www.postgresql.org/docs/current/manage-ag-tablespaces.html
SET default_tablespace = blumenladenspace;

-- Indexe mit pgAdmin auflisten: "Query Tool" verwenden mit
--  SELECT   tablename, indexname, indexdef, tablespace
--  FROM     pg_indexes
--  WHERE    schemaname = 'blumenladen'
--  ORDER BY tablename, indexname;

-- default: btree
-- https://www.postgresql.org/docs/current/sql-createindex.html

CREATE INDEX IF NOT EXISTS leiter_nachname_idx ON leiter(nachname);

CREATE INDEX IF NOT EXISTS blumenladen_name_idx ON blumenladen(name);

CREATE INDEX IF NOT EXISTS blume_blumenladen_id_idx ON blume(blumenladen_id);
CREATE INDEX IF NOT EXISTS blume_name_idx ON blume(name);
