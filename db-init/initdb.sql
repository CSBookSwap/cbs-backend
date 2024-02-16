--------------------------------
-- Create api db
--------------------------------
CREATE DATABASE bookswap_db;
\c bookswap_db
CREATE SCHEMA bookswap;
SET search_path TO bookswap;
CREATE USER bookswap_api WITH ENCRYPTED PASSWORD 'bookswapPassWord';
GRANT ALL PRIVILEGES ON SCHEMA bookswap TO bookswap_api;

--------------------------------
-- Create keycloak db
--------------------------------
CREATE DATABASE keycloak_db;
\c keycloak_db
CREATE SCHEMA keycloak;
SET search_path TO keycloak;
CREATE USER keycloak WITH ENCRYPTED PASSWORD 'password';
GRANT ALL PRIVILEGES ON SCHEMA keycloak TO keycloak;