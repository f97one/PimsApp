-- User: pims_admin
-- DROP USER pims_admin;

CREATE USER pims_admin WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  VALID UNTIL '2199-12-31 00:00:00+09'

COMMENT ON ROLE pims_admin IS 'PIMS Database Administrator';

CREATE DATABASE pimsdb
    WITH 
    OWNER = pims_admin
    TEMPLATE = template0
    ENCODING = 'UTF8'
    LC_COLLATE = 'C'
    LC_CTYPE = 'C'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE pimsdb
    IS 'PIMS Database';

GRANT ALL ON DATABASE pimsdb TO pims_admin WITH GRANT OPTION;
