--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: -
--

CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: actionlog; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE actionlog (
    id bigint NOT NULL,
    actiondate timestamp without time zone,
    entry character varying(32768) NOT NULL,
    privateflag boolean NOT NULL,
    submissionstate character varying(255) NOT NULL,
    attachment_id bigint,
    person_id bigint,
    submission_id bigint NOT NULL
);


--
-- Name: attachment; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE attachment (
    id bigint NOT NULL,
    data character varying(255),
    date timestamp without time zone NOT NULL,
    name character varying(255) NOT NULL,
    type integer NOT NULL,
    person_id bigint,
    submission_id bigint NOT NULL
);


--
-- Name: college; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE college (
    id bigint NOT NULL,
    displayorder integer NOT NULL,
    name character varying(255) NOT NULL
);


--
-- Name: committee_member; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE committee_member (
    id bigint NOT NULL,
    chair boolean NOT NULL,
    displayorder integer NOT NULL,
    firstname character varying(255),
    lastname character varying(255),
    middlename character varying(255),
    submission_id bigint NOT NULL
);


--
-- Name: configuration; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE configuration (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    value character varying(32768)
);


--
-- Name: custom_action_definition; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE custom_action_definition (
    id bigint NOT NULL,
    displayorder integer NOT NULL,
    label character varying(255) NOT NULL
);


--
-- Name: custom_action_value; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE custom_action_value (
    id bigint NOT NULL,
    value boolean NOT NULL,
    definition_id bigint NOT NULL,
    submission_id bigint NOT NULL
);


--
-- Name: degree; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE degree (
    id bigint NOT NULL,
    displayorder integer NOT NULL,
    level integer NOT NULL,
    name character varying(255) NOT NULL
);


--
-- Name: department; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE department (
    id bigint NOT NULL,
    displayorder integer NOT NULL,
    name character varying(255) NOT NULL
);


--
-- Name: deposit_location; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE deposit_location (
    id bigint NOT NULL,
    collection character varying(1024),
    depositor character varying(255),
    displayorder integer NOT NULL,
    name character varying(255) NOT NULL,
    onbehalfof character varying(255),
    packager character varying(255),
    password character varying(255),
    repository character varying(1024),
    username character varying(255)
);


--
-- Name: document_type; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE document_type (
    id bigint NOT NULL,
    displayorder integer NOT NULL,
    level integer NOT NULL,
    name character varying(255) NOT NULL
);


--
-- Name: email_template; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE email_template (
    id bigint NOT NULL,
    displayorder integer NOT NULL,
    message character varying(32768) NOT NULL,
    name character varying(255) NOT NULL,
    subject character varying(32768) NOT NULL,
    systemrequired boolean NOT NULL
);


--
-- Name: embargo_type; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE embargo_type (
    id bigint NOT NULL,
    active boolean NOT NULL,
    description character varying(32768) NOT NULL,
    displayorder integer NOT NULL,
    duration integer,
    name character varying(255) NOT NULL
);


--
-- Name: graduation_month; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE graduation_month (
    id bigint NOT NULL,
    displayorder integer NOT NULL,
    month integer NOT NULL
);


--
-- Name: major; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE major (
    id bigint NOT NULL,
    displayorder integer NOT NULL,
    name character varying(255) NOT NULL
);


--
-- Name: person; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE person (
    id bigint NOT NULL,
    birthyear integer,
    currentcollege character varying(255),
    currentdegree character varying(255),
    currentdepartment character varying(255),
    currentemailaddress character varying(255),
    currentgraduationmonth integer,
    currentgraduationyear integer,
    currentmajor character varying(255),
    currentphonenumber character varying(255),
    currentpostaladdress character varying(255),
    displayname character varying(255),
    email character varying(255) NOT NULL,
    firstname character varying(255),
    institutionalidentifier character varying(255),
    lastname character varying(255),
    middlename character varying(255),
    netid character varying(255),
    passwordhash character varying(255),
    permanentemailaddress character varying(255),
    permanentphonenumber character varying(255),
    permanentpostaladdress character varying(255),
    role integer NOT NULL
);


--
-- Name: person_affiliations; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE person_affiliations (
    jpapersonimpl_id bigint NOT NULL,
    affiliations character varying(255)
);


--
-- Name: preference; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE preference (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    value character varying(32768),
    person_id bigint NOT NULL
);


--
-- Name: search_filter; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE search_filter (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    publicflag boolean NOT NULL,
    rangeend date,
    rangestart date,
    umirelease boolean,
    unassigned boolean,
    creator_id bigint NOT NULL
);


--
-- Name: search_filter_colleges; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE search_filter_colleges (
    jpanamedsearchfilterimpl_id bigint NOT NULL,
    colleges character varying(255)
);


--
-- Name: search_filter_degrees; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE search_filter_degrees (
    jpanamedsearchfilterimpl_id bigint NOT NULL,
    degrees character varying(255)
);


--
-- Name: search_filter_departments; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE search_filter_departments (
    jpanamedsearchfilterimpl_id bigint NOT NULL,
    departments character varying(255)
);


--
-- Name: search_filter_documenttypes; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE search_filter_documenttypes (
    jpanamedsearchfilterimpl_id bigint NOT NULL,
    documenttypes character varying(255)
);


--
-- Name: search_filter_embargo_type; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE search_filter_embargo_type (
    search_filter_id bigint NOT NULL,
    embargos_id bigint NOT NULL
);


--
-- Name: search_filter_majors; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE search_filter_majors (
    jpanamedsearchfilterimpl_id bigint NOT NULL,
    majors character varying(255)
);


--
-- Name: search_filter_person; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE search_filter_person (
    search_filter_id bigint NOT NULL,
    assignees_id bigint NOT NULL
);


--
-- Name: search_filter_semesters; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE search_filter_semesters (
    jpanamedsearchfilterimpl_id bigint NOT NULL,
    semesters character varying(255)
);


--
-- Name: search_filter_states; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE search_filter_states (
    jpanamedsearchfilterimpl_id bigint NOT NULL,
    states character varying(255)
);


--
-- Name: search_filter_submission; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE search_filter_submission (
    search_filter_id bigint NOT NULL,
    submissions_id bigint NOT NULL
);


--
-- Name: search_filter_text; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE search_filter_text (
    jpanamedsearchfilterimpl_id bigint NOT NULL,
    searchtext character varying(255)
);


--
-- Name: seq_actionlog; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_actionlog
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_attachment; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_attachment
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_college; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_college
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_committee_member; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_committee_member
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_configuration; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_configuration
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_custom_action_definition; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_custom_action_definition
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_custom_action_value; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_custom_action_value
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_degree; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_degree
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_department; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_department
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_deposit_location; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_deposit_location
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_document_type; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_document_type
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_email_template; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_email_template
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_embargo_type; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_embargo_type
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_graduation_month; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_graduation_month
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_major; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_major
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_person; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_person
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_preference; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_preference
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_search_filter; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_search_filter
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: seq_submission; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_submission
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: submission; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE submission (
    id bigint NOT NULL,
    umirelease boolean,
    approvaldate timestamp without time zone,
    college character varying(255),
    committeeapprovaldate timestamp without time zone,
    committeecontactemail character varying(255),
    committeeemailhash character varying(255),
    committeeembargoapprovaldate timestamp without time zone,
    degree character varying(255),
    degreelevel integer,
    department character varying(255),
    depositid character varying(255),
    documentabstract character varying(326768),
    documentkeywords character varying(326768),
    documenttitle character varying(326768),
    documenttype character varying(255),
    graduationmonth integer,
    graduationyear integer,
    lastactionlogdate timestamp without time zone,
    lastactionlogentry character varying(326768),
    licenseagreementdate timestamp without time zone,
    major character varying(255),
    statename character varying(255),
    studentbirthyear integer,
    studentfirstname character varying(255),
    studentlastname character varying(255),
    studentmiddlename character varying(255),
    submissiondate timestamp without time zone,
    assignee_id bigint,
    embargotype_id bigint,
    submitter_id bigint NOT NULL
);


--
-- Name: actionlog_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY actionlog
    ADD CONSTRAINT actionlog_pkey PRIMARY KEY (id);


--
-- Name: attachment_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY attachment
    ADD CONSTRAINT attachment_pkey PRIMARY KEY (id);


--
-- Name: attachment_submission_id_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY attachment
    ADD CONSTRAINT attachment_submission_id_name_key UNIQUE (submission_id, name);


--
-- Name: college_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY college
    ADD CONSTRAINT college_name_key UNIQUE (name);


--
-- Name: college_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY college
    ADD CONSTRAINT college_pkey PRIMARY KEY (id);


--
-- Name: committee_member_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY committee_member
    ADD CONSTRAINT committee_member_pkey PRIMARY KEY (id);


--
-- Name: configuration_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY configuration
    ADD CONSTRAINT configuration_name_key UNIQUE (name);


--
-- Name: configuration_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY configuration
    ADD CONSTRAINT configuration_pkey PRIMARY KEY (id);


--
-- Name: custom_action_definition_label_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY custom_action_definition
    ADD CONSTRAINT custom_action_definition_label_key UNIQUE (label);


--
-- Name: custom_action_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY custom_action_definition
    ADD CONSTRAINT custom_action_definition_pkey PRIMARY KEY (id);


--
-- Name: custom_action_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY custom_action_value
    ADD CONSTRAINT custom_action_value_pkey PRIMARY KEY (id);


--
-- Name: custom_action_value_submission_id_definition_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY custom_action_value
    ADD CONSTRAINT custom_action_value_submission_id_definition_id_key UNIQUE (submission_id, definition_id);


--
-- Name: degree_name_level_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY degree
    ADD CONSTRAINT degree_name_level_key UNIQUE (name, level);


--
-- Name: degree_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY degree
    ADD CONSTRAINT degree_pkey PRIMARY KEY (id);


--
-- Name: department_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY department
    ADD CONSTRAINT department_name_key UNIQUE (name);


--
-- Name: department_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY department
    ADD CONSTRAINT department_pkey PRIMARY KEY (id);


--
-- Name: deposit_location_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY deposit_location
    ADD CONSTRAINT deposit_location_name_key UNIQUE (name);


--
-- Name: deposit_location_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY deposit_location
    ADD CONSTRAINT deposit_location_pkey PRIMARY KEY (id);


--
-- Name: document_type_name_level_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY document_type
    ADD CONSTRAINT document_type_name_level_key UNIQUE (name, level);


--
-- Name: document_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY document_type
    ADD CONSTRAINT document_type_pkey PRIMARY KEY (id);


--
-- Name: email_template_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY email_template
    ADD CONSTRAINT email_template_name_key UNIQUE (name);


--
-- Name: email_template_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY email_template
    ADD CONSTRAINT email_template_pkey PRIMARY KEY (id);


--
-- Name: embargo_type_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY embargo_type
    ADD CONSTRAINT embargo_type_name_key UNIQUE (name);


--
-- Name: embargo_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY embargo_type
    ADD CONSTRAINT embargo_type_pkey PRIMARY KEY (id);


--
-- Name: graduation_month_month_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY graduation_month
    ADD CONSTRAINT graduation_month_month_key UNIQUE (month);


--
-- Name: graduation_month_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY graduation_month
    ADD CONSTRAINT graduation_month_pkey PRIMARY KEY (id);


--
-- Name: major_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY major
    ADD CONSTRAINT major_name_key UNIQUE (name);


--
-- Name: major_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY major
    ADD CONSTRAINT major_pkey PRIMARY KEY (id);


--
-- Name: person_email_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY person
    ADD CONSTRAINT person_email_key UNIQUE (email);


--
-- Name: person_netid_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY person
    ADD CONSTRAINT person_netid_key UNIQUE (netid);


--
-- Name: person_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY person
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);


--
-- Name: preference_person_id_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY preference
    ADD CONSTRAINT preference_person_id_name_key UNIQUE (person_id, name);


--
-- Name: preference_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY preference
    ADD CONSTRAINT preference_pkey PRIMARY KEY (id);


--
-- Name: search_filter_creator_id_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY search_filter
    ADD CONSTRAINT search_filter_creator_id_name_key UNIQUE (creator_id, name);


--
-- Name: search_filter_embargo_type_embargos_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY search_filter_embargo_type
    ADD CONSTRAINT search_filter_embargo_type_embargos_id_key UNIQUE (embargos_id);


--
-- Name: search_filter_person_assignees_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY search_filter_person
    ADD CONSTRAINT search_filter_person_assignees_id_key UNIQUE (assignees_id);


--
-- Name: search_filter_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY search_filter
    ADD CONSTRAINT search_filter_pkey PRIMARY KEY (id);


--
-- Name: search_filter_submission_submissions_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY search_filter_submission
    ADD CONSTRAINT search_filter_submission_submissions_id_key UNIQUE (submissions_id);


--
-- Name: submission_committeeemailhash_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY submission
    ADD CONSTRAINT submission_committeeemailhash_key UNIQUE (committeeemailhash);


--
-- Name: submission_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY submission
    ADD CONSTRAINT submission_pkey PRIMARY KEY (id);


--
-- Name: fk1792999c2d7e525c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY committee_member
    ADD CONSTRAINT fk1792999c2d7e525c FOREIGN KEY (submission_id) REFERENCES submission(id);


--
-- Name: fk1ea7a56f7c2aad; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_person
    ADD CONSTRAINT fk1ea7a56f7c2aad FOREIGN KEY (assignees_id) REFERENCES person(id);


--
-- Name: fk1ea7a58e0b1a22; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_person
    ADD CONSTRAINT fk1ea7a58e0b1a22 FOREIGN KEY (search_filter_id) REFERENCES search_filter(id);


--
-- Name: fk208979ee2e6a9c33; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_documenttypes
    ADD CONSTRAINT fk208979ee2e6a9c33 FOREIGN KEY (jpanamedsearchfilterimpl_id) REFERENCES search_filter(id);


--
-- Name: fk32fe0a0c2e6a9c33; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_colleges
    ADD CONSTRAINT fk32fe0a0c2e6a9c33 FOREIGN KEY (jpanamedsearchfilterimpl_id) REFERENCES search_filter(id);


--
-- Name: fk40b835ef2ef2d605; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter
    ADD CONSTRAINT fk40b835ef2ef2d605 FOREIGN KEY (creator_id) REFERENCES person(id);


--
-- Name: fk57729f9c4783dd21; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_submission
    ADD CONSTRAINT fk57729f9c4783dd21 FOREIGN KEY (submissions_id) REFERENCES submission(id);


--
-- Name: fk57729f9c8e0b1a22; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_submission
    ADD CONSTRAINT fk57729f9c8e0b1a22 FOREIGN KEY (search_filter_id) REFERENCES search_filter(id);


--
-- Name: fk608da522e6a9c33; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_states
    ADD CONSTRAINT fk608da522e6a9c33 FOREIGN KEY (jpanamedsearchfilterimpl_id) REFERENCES search_filter(id);


--
-- Name: fk63f94fb68e0b1a22; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_embargo_type
    ADD CONSTRAINT fk63f94fb68e0b1a22 FOREIGN KEY (search_filter_id) REFERENCES search_filter(id);


--
-- Name: fk63f94fb69c9e0205; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_embargo_type
    ADD CONSTRAINT fk63f94fb69c9e0205 FOREIGN KEY (embargos_id) REFERENCES embargo_type(id);


--
-- Name: fk8068257d2e6a9c33; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_text
    ADD CONSTRAINT fk8068257d2e6a9c33 FOREIGN KEY (jpanamedsearchfilterimpl_id) REFERENCES search_filter(id);


--
-- Name: fk84363b4c44904182; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY submission
    ADD CONSTRAINT fk84363b4c44904182 FOREIGN KEY (assignee_id) REFERENCES person(id);


--
-- Name: fk84363b4c62b46408; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY submission
    ADD CONSTRAINT fk84363b4c62b46408 FOREIGN KEY (submitter_id) REFERENCES person(id);


--
-- Name: fk84363b4cc6f816d8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY submission
    ADD CONSTRAINT fk84363b4cc6f816d8 FOREIGN KEY (embargotype_id) REFERENCES embargo_type(id);


--
-- Name: fk884fe8d72e6a9c33; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_degrees
    ADD CONSTRAINT fk884fe8d72e6a9c33 FOREIGN KEY (jpanamedsearchfilterimpl_id) REFERENCES search_filter(id);


--
-- Name: fk8af759232d7e525c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attachment
    ADD CONSTRAINT fk8af759232d7e525c FOREIGN KEY (submission_id) REFERENCES submission(id);


--
-- Name: fk8af75923f967e3c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY attachment
    ADD CONSTRAINT fk8af75923f967e3c FOREIGN KEY (person_id) REFERENCES person(id);


--
-- Name: fk8bfde7eb2e6a9c33; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_semesters
    ADD CONSTRAINT fk8bfde7eb2e6a9c33 FOREIGN KEY (jpanamedsearchfilterimpl_id) REFERENCES search_filter(id);


--
-- Name: fka8fcbcdbf967e3c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY preference
    ADD CONSTRAINT fka8fcbcdbf967e3c FOREIGN KEY (person_id) REFERENCES person(id);


--
-- Name: fkb38a4d112e6a9c33; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_departments
    ADD CONSTRAINT fkb38a4d112e6a9c33 FOREIGN KEY (jpanamedsearchfilterimpl_id) REFERENCES search_filter(id);


--
-- Name: fkbd1f14e2d7e525c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY actionlog
    ADD CONSTRAINT fkbd1f14e2d7e525c FOREIGN KEY (submission_id) REFERENCES submission(id);


--
-- Name: fkbd1f14e936e2b7c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY actionlog
    ADD CONSTRAINT fkbd1f14e936e2b7c FOREIGN KEY (attachment_id) REFERENCES attachment(id);


--
-- Name: fkbd1f14ef967e3c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY actionlog
    ADD CONSTRAINT fkbd1f14ef967e3c FOREIGN KEY (person_id) REFERENCES person(id);


--
-- Name: fke29e7c2df4a0eaa1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY person_affiliations
    ADD CONSTRAINT fke29e7c2df4a0eaa1 FOREIGN KEY (jpapersonimpl_id) REFERENCES person(id);


--
-- Name: fke49b30362d7e525c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY custom_action_value
    ADD CONSTRAINT fke49b30362d7e525c FOREIGN KEY (submission_id) REFERENCES submission(id);


--
-- Name: fke49b30366b22f363; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY custom_action_value
    ADD CONSTRAINT fke49b30366b22f363 FOREIGN KEY (definition_id) REFERENCES custom_action_definition(id);


--
-- Name: fkfac40e2a2e6a9c33; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY search_filter_majors
    ADD CONSTRAINT fkfac40e2a2e6a9c33 FOREIGN KEY (jpanamedsearchfilterimpl_id) REFERENCES search_filter(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

