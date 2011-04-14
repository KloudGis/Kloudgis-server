--
-- PostgreSQL database dump
--

-- Started on 2011-04-12 13:55:15 EDT

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

--
-- TOC entry 1496 (class 1259 OID 276266)
-- Dependencies: 3
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- TOC entry 1785 (class 0 OID 0)
-- Dependencies: 1496
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('hibernate_sequence', 42, true);


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1495 (class 1259 OID 276236)
-- Dependencies: 3
-- Name: user_roles; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE user_roles (
    id bigint NOT NULL,
    role_name character varying(50),
    email character varying(50),
    user_name character varying(50)
);


ALTER TABLE public.user_roles OWNER TO postgres;

--
-- TOC entry 1494 (class 1259 OID 276229)
-- Dependencies: 3
-- Name: users; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE users (
    id bigint NOT NULL,
    email character varying(50),
    expiredate character varying(50),
    fullname character varying(150),
    moreinfo character varying(50),
    password character varying(50),
    group_id bigint,
    compagny character varying(50),
    isactive boolean,
    picture bytea,
    location character varying(50),
    user_name character varying(50)
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 1779 (class 0 OID 276236)
-- Dependencies: 1495
-- Data for Name: user_roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY user_roles (id, role_name, email, user_name) FROM stdin;
37	admin_role	admin@kloudgis.org	\N
38	manager	admin@kloudgis.org	\N
39	user_role	admin@kloudgis.org	\N
\.


--
-- TOC entry 1778 (class 0 OID 276229)
-- Dependencies: 1494
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY users (id, email, expiredate, fullname, moreinfo, password, group_id, compagny, isactive, picture, location, user_name) FROM stdin;
40	admin@kloudgis.org	\N	Adminstrator	\N	kwadmin	\N	\N	t	\N	\N	\N
\.


--
-- TOC entry 1777 (class 2606 OID 276240)
-- Dependencies: 1495 1495
-- Name: user_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (id);


--
-- TOC entry 1775 (class 2606 OID 276233)
-- Dependencies: 1494 1494
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 1784 (class 0 OID 0)
-- Dependencies: 3
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2011-04-12 13:55:15 EDT

--
-- PostgreSQL database dump complete
--

