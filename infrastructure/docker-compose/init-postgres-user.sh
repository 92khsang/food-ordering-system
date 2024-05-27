#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "root" --dbname "udemy_fos" <<-EOSQL
  CREATE USER udemy WITH PASSWORD 'udemy123!#';
  ALTER USER udemy WITH SUPERUSER;
EOSQL
