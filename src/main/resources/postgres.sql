-- Database: grafana-dockerstat

-- DROP DATABASE "grafana-dockerstat";

CREATE DATABASE "grafana-dockerstat"
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Table: public.docker_data

-- DROP TABLE public.docker_data;

CREATE TABLE public.docker_data
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    container character varying(50) COLLATE pg_catalog."default" NOT NULL,
    cpu_perc numeric(6,3) NOT NULL,
    mem_perc numeric(6,3),
    mem_usage_mb numeric(10,3),
    net_i_usage_mb numeric(10,3),
    net_o_usage_mb numeric(10,3),
    block_i_usage_mb numeric(10,3),
    block_o_usage_mb numeric(10,3),
    sampler_time timestamp without time zone,
    CONSTRAINT data_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE public.docker_data
    OWNER to postgres;