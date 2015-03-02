create table actionlog (
    id int8 not null,
    actionDate timestamp,
    entry varchar(32768) not null,
    privateFlag boolean not null,
    submissionState varchar(255) not null,
    attachment_id int8,
    person_id int8,
    submission_id int8 not null,
    primary key (id)
);

create table administrative_groups (
    id int8 not null,
    displayOrder int4 not null,
    emails bytea,
    name varchar(255) not null,
    primary key (id)
);

create table attachment (
    id int8 not null,
    data varchar(255),
    date timestamp not null,
    name varchar(255) not null,
    type int4 not null,
    person_id int8,
    submission_id int8 not null,
    primary key (id)
);

create table college (
    id int8 not null,
    displayOrder int4 not null,
    emails bytea,
    name varchar(255) not null,
    primary key (id)
);

create table committee_member (
    id int8 not null,
    displayOrder int4 not null,
    firstName varchar(255),
    lastName varchar(255),
    middleName varchar(255),
    submission_id int8 not null,
    primary key (id)
);

create table committee_member_role_type (
    id int8 not null,
    displayOrder int4 not null,
    level int4 not null,
    name varchar(255) not null,
    primary key (id)
);

create table committee_member_roles (
    JpaCommitteeMemberImpl_id int8 not null,
    roles varchar(255),
    roles_ORDER int4 not null,
    primary key (JpaCommitteeMemberImpl_id , roles_ORDER)
);

create table configuration (
    id int8 not null,
    name varchar(255) not null,
    value varchar(32768),
    primary key (id)
);

create table custom_action_definition (
    id int8 not null,
    displayOrder int4 not null,
    isStudentVisible BOOLEAN DEFAULT false not null,
    label varchar(255) not null,
    primary key (id)
);

create table custom_action_value (
    id int8 not null,
    value boolean not null,
    definition_id int8 not null,
    submission_id int8 not null,
    primary key (id)
);

create table degree (
    id int8 not null,
    displayOrder int4 not null,
    level int4 not null,
    name varchar(255) not null,
    primary key (id)
);

create table department (
    id int8 not null,
    displayOrder int4 not null,
    emails bytea,
    name varchar(255) not null,
    primary key (id)
);

create table deposit_location (
    id int8 not null,
    collection varchar(1024),
    depositor varchar(255),
    displayOrder int4 not null,
    name varchar(255) not null,
    onBehalfOf varchar(255),
    packager varchar(255),
    password varchar(255),
    repository varchar(1024),
    timeout INTEGER DEFAULT '60',
    username varchar(255),
    primary key (id)
);

create table document_type (
    id int8 not null,
    displayOrder int4 not null,
    level int4 not null,
    name varchar(255) not null,
    primary key (id)
);

create table email_template (
    id int8 not null,
    displayOrder int4 not null,
    message varchar(32768) not null,
    name varchar(255) not null,
    subject varchar(32768) not null,
    systemRequired BOOLEAN DEFAULT false not null,
    primary key (id)
);

create table email_workflow_rule_conditions (
    id int8 not null,
    conditionId int8,
    conditionType int4,
    displayOrder int4 not null,
    primary key (id)
);

create table email_workflow_rules (
    id int8 not null,
    associatedState varchar(255),
    displayOrder int4 not null,
    isDisabled boolean,
    isSystem boolean,
    recipientType int4,
    adminGroupRecipientId int8,
    conditionID int8,
    emailTemplateId int8,
    primary key (id)
);

create table embargo_type (
    id int8 not null,
    active boolean not null,
    description varchar(32768) not null,
    displayOrder int4 not null,
    duration int4,
    guarantor INTEGER DEFAULT '0' not null,
    name varchar(255) not null,
    systemRequired BOOLEAN DEFAULT false not null,
    primary key (id)
);

create table graduation_month (
    id int8 not null,
    displayOrder int4 not null,
    month int4 not null,
    primary key (id)
);

create table language (
    id int8 not null,
    displayOrder int4 not null,
    name varchar(255) not null,
    primary key (id)
);

create table major (
    id int8 not null,
    displayOrder int4 not null,
    name varchar(255) not null,
    primary key (id)
);

create table person (
    id int8 not null,
    birthYear int4,
    currentCollege varchar(255),
    currentDegree varchar(255),
    currentDepartment varchar(255),
    currentEmailAddress varchar(255),
    currentGraduationMonth int4,
    currentGraduationYear int4,
    currentMajor varchar(255),
    currentPhoneNumber varchar(255),
    currentPostalAddress varchar(255),
    currentProgram varchar(255),
    displayName varchar(255),
    email varchar(255) not null,
    firstName varchar(255),
    institutionalIdentifier varchar(255),
    lastName varchar(255),
    middleName varchar(255),
    netid varchar(255),
    orcid varchar(255),
    passwordHash varchar(255),
    permanentEmailAddress varchar(255),
    permanentPhoneNumber varchar(255),
    permanentPostalAddress varchar(255),
    role int4 not null,
    primary key (id)
);

create table person_affiliations (
    JpaPersonImpl_id int8 not null,
    affiliations varchar(255)
);

create table preference (
    id int8 not null,
    name varchar(255) not null,
    value varchar(32768),
    person_id int8 not null,
    primary key (id)
);

create table program (
    id int8 not null,
    displayOrder int4 not null,
    emails bytea,
    name varchar(255) not null,
    primary key (id)
);

create table search_filter (
    id int8 not null,
    name varchar(255) not null,
    publicFlag boolean not null,
    rangeEnd date,
    rangeStart date,
    umiRelease boolean,
    creator_id int8 not null,
    primary key (id)
);

create table search_filter_assignees (
    search_filter_id int8 not null,
    assigneeIds int8
);

create table search_filter_colleges (
    search_filter_id int8 not null,
    colleges varchar(255)
);

create table search_filter_columns (
    search_filter_id int8 not null,
    columns int4
);

create table search_filter_customactions (
    search_filter_id int8 not null,
    customActionIds int8
);

create table search_filter_degrees (
    search_filter_id int8 not null,
    degrees varchar(255)
);

create table search_filter_departments (
    search_filter_id int8 not null,
    departments varchar(255)
);

create table search_filter_documenttypes (
    search_filter_id int8 not null,
    documentTypes varchar(255)
);

create table search_filter_embargos (
    search_filter_id int8 not null,
    embargoIds int8
);

create table search_filter_excluded_actionlogs (
    search_filter_id int8 not null,
    excludedActionLogIds int8
);

create table search_filter_excluded_submissions (
    search_filter_id int8 not null,
    excludedSubmissionIds int8
);

create table search_filter_included_actionlogs (
    search_filter_id int8 not null,
    includedActionLogIds int8
);

create table search_filter_included_submissions (
    search_filter_id int8 not null,
    includedSubmissionIds int8
);

create table search_filter_majors (
    search_filter_id int8 not null,
    majors varchar(255)
);

create table search_filter_programs (
    search_filter_id int8 not null,
    programs varchar(255)
);

create table search_filter_semesters (
    search_filter_id int8 not null,
    semesters varchar(255)
);

create table search_filter_states (
    search_filter_id int8 not null,
    states varchar(255)
);

create table search_filter_text (
    search_filter_id int8 not null,
    searchText varchar(255)
);

create table submission (
    id int8 not null,
    UMIRelease boolean,
    approvalDate timestamp,
    college varchar(255),
    collegeId int8,
    committeeApprovalDate timestamp,
    committeeContactEmail varchar(255),
    committeeEmailHash varchar(255),
    committeeEmbargoApprovalDate timestamp,
    defenseDate timestamp,
    degree varchar(255),
    degreeLevel int4,
    department varchar(255),
    departmentId int8,
    depositDate timestamp,
    depositId varchar(1024),
    documentAbstract varchar(326768),
    documentKeywords varchar(326768),
    documentLanguage varchar(255),
    documentTitle varchar(326768),
    documentType varchar(255),
    graduationMonth int4,
    graduationYear int4,
    lastActionLogDate timestamp,
    lastActionLogEntry varchar(326768),
    licenseAgreementDate timestamp,
    major varchar(255),
    orcid varchar(255),
    program varchar(255),
    programId int8,
    publishedMaterial varchar(326768),
    reviewerNotes varchar(326768),
    stateName varchar(255),
    studentBirthYear int4,
    studentFirstName varchar(255),
    studentLastName varchar(255),
    studentMiddleName varchar(255),
    submissionDate timestamp,
    assignee_id int8,
    submitter_id int8 not null,
    primary key (id)
);

create table submission_embargotypes (
    submission_id int8 not null,
    embargoTypeIds int8
);

create table submission_subjects (
    JpaSubmissionImpl_id int8 not null,
    documentSubjects varchar(255),
    documentSubjects_ORDER int4 not null,
    primary key (JpaSubmissionImpl_id , documentSubjects_ORDER)
);

alter table administrative_groups 
    add constraint UK_g0arithcbc2y7mqw06l9f53yv unique (name);

alter table attachment 
    add constraint UK_ruw2i1tqm58ytj13n6o8n49r6 unique (submission_id, name);

alter table college 
    add constraint UK_nyc2rxbj71rdhcw055436agb5 unique (name);

alter table committee_member_role_type 
    add constraint UK_f8fg81yj2rftvrw9drj6t32c5 unique (name, level);

alter table configuration 
    add constraint UK_c3pprgpekt3nw1vmv98sqjqce unique (name);

alter table custom_action_definition 
    add constraint UK_vws65okmwb4j72n41daph650 unique (label);

alter table custom_action_value 
    add constraint UK_sfhufnuq27bp4rvau3p418gej unique (submission_id, definition_id);

alter table degree 
    add constraint UK_gbfqltywj1fafp78jeloxu0ef unique (name, level);

alter table department 
    add constraint UK_1t68827l97cwyxo9r1u6t4p7d unique (name);

alter table deposit_location 
    add constraint UK_dovfybuq9gk43yqlfept9xc42 unique (name);

alter table document_type 
    add constraint UK_gu9uufujcobcnd1g0auqi6sxo unique (name, level);

alter table email_template 
    add constraint UK_2qlliow884c9ci671eliwiydu unique (name, systemRequired);

alter table embargo_type 
    add constraint UK_rrrhycsns6ohf9uvxrs5i61jk unique (name, guarantor, systemRequired);

alter table graduation_month 
    add constraint UK_dv0ctus1ai651v35jvpdyhuyx unique (month);

alter table language 
    add constraint UK_g8hr207ijpxlwu10pewyo65gv unique (name);

alter table major 
    add constraint UK_oi0ctjbjvktdcfxws9w2exiwb unique (name);

alter table person 
    add constraint UK_fwmwi44u55bo4rvwsv0cln012 unique (email);

alter table person 
    add constraint UK_7lave14pgltnfmvs342s9qco8 unique (netid);

alter table preference 
    add constraint UK_kpjn6eigbd3yxtc9dbr4ytpyp unique (person_id, name);

alter table program 
    add constraint UK_ha1ojetw3fv9tfdrrvfy99yuf unique (name);

alter table search_filter 
    add constraint UK_hcac87nf5end9s93gfb1lygyp unique (creator_id, name);

alter table submission 
    add constraint UK_k7sos9qjyyos23e4hw2c5gqgh unique (committeeEmailHash);

alter table actionlog 
    add constraint FK_9ud0hhk9umt2gcffwi4ipxhrb 
    foreign key (attachment_id) 
    references attachment;

alter table actionlog 
    add constraint FK_4norsqa704bntket913ub5bpx 
    foreign key (person_id) 
    references person;

alter table actionlog 
    add constraint FK_ahsjgamx6y6m5g7cgr8xnkjq6 
    foreign key (submission_id) 
    references submission;

alter table attachment 
    add constraint FK_8se0vn1c9y2lviiun4a877q7h 
    foreign key (person_id) 
    references person;

alter table attachment 
    add constraint FK_xrwsik7ddccorroc9m86mtmy 
    foreign key (submission_id) 
    references submission;

alter table committee_member 
    add constraint FK_4eqjy8xt9agdgnkygu1jhib6b 
    foreign key (submission_id) 
    references submission;

alter table committee_member_roles 
    add constraint FK_lhstcjubx3ok7jsd4sur6dpmu 
    foreign key (JpaCommitteeMemberImpl_id) 
    references committee_member;

alter table custom_action_value 
    add constraint FK_3brcld4o5axxxtpqvtx5jlhty 
    foreign key (definition_id) 
    references custom_action_definition;

alter table custom_action_value 
    add constraint FK_784g0grh0a7si8baguih2eu93 
    foreign key (submission_id) 
    references submission;

alter table email_workflow_rules 
    add constraint FK_5it9i77coc0sut7nt4ivl5taj 
    foreign key (adminGroupRecipientId) 
    references administrative_groups;

alter table email_workflow_rules 
    add constraint FK_bs7ru7e7sy0824bei7ldbyhcv 
    foreign key (conditionID) 
    references email_workflow_rule_conditions;

alter table email_workflow_rules 
    add constraint FK_282jvq0q662qfl3c8g09pprim 
    foreign key (emailTemplateId) 
    references email_template;

alter table person_affiliations 
    add constraint FK_2u4d124bh89lkfo4o5gysf0do 
    foreign key (JpaPersonImpl_id) 
    references person;

alter table preference 
    add constraint FK_h918mhr9wiqqvqbjcgxga8sj2 
    foreign key (person_id) 
    references person;

alter table search_filter 
    add constraint FK_3j48541vurjef5am8q0rnipmk 
    foreign key (creator_id) 
    references person;

alter table search_filter_assignees 
    add constraint FK_b1r4dxhoyq31751qlo822cav5 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_colleges 
    add constraint FK_cys8fjpqxkvcyde4beis0n3lx 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_columns 
    add constraint FK_k6e868fvh8225894xuyplaxri 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_customactions 
    add constraint FK_82hut8geqdkp89x0uyl3n6iub 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_degrees 
    add constraint FK_3rkguuxiipf4dcn38yt6nryj4 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_departments 
    add constraint FK_7vinqqsd2j0kidubm1ofpyc87 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_documenttypes 
    add constraint FK_ji793qo35mmcehjs1y8y5luy6 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_embargos 
    add constraint FK_mf5xk637mx8l8scb9m1iu2hod 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_excluded_actionlogs 
    add constraint FK_5o8iokop2rxq2ciq162j0xca0 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_excluded_submissions 
    add constraint FK_7s000trks1u9ir2nn1vsida74 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_included_actionlogs 
    add constraint FK_mg0crds89wdjejct5ly9nub5o 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_included_submissions 
    add constraint FK_15qdawr540viu5in5mem02w62 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_majors 
    add constraint FK_3wwtaloot4o2yv8ue7jg8cyo0 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_programs 
    add constraint FK_x3e9uwkkebwkloegbyfayceh 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_semesters 
    add constraint FK_miqf877rp21ethhtf3mep607r 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_states 
    add constraint FK_85a9ru0277s4n4y19iqt2veo7 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_text 
    add constraint FK_5csq0a20x6lkhhddxl1mxflum 
    foreign key (search_filter_id) 
    references search_filter;

alter table submission 
    add constraint FK_bsnfsllpukyx681rjx6t7kw0q 
    foreign key (assignee_id) 
    references person;

alter table submission 
    add constraint FK_5tcephlarb35bwrijkqdrg0uu 
    foreign key (submitter_id) 
    references person;

alter table submission_embargotypes 
    add constraint FK_b3h9qu258ulj5cu8crm175d5i 
    foreign key (submission_id) 
    references submission;

alter table submission_subjects 
    add constraint FK_rexg1ed7ej7dng9e9aq6whvn3 
    foreign key (JpaSubmissionImpl_id) 
    references submission;

create sequence seq_actionlog;

create sequence seq_administrative_groups;

create sequence seq_attachment;

create sequence seq_college;

create sequence seq_committee_member;

create sequence seq_committee_member_role_type;

create sequence seq_configuration;

create sequence seq_custom_action_definition;

create sequence seq_custom_action_value;

create sequence seq_degree;

create sequence seq_department;

create sequence seq_deposit_location;

create sequence seq_document_type;

create sequence seq_email_template;

create sequence seq_email_workflow_rule_conditions;

create sequence seq_email_workflow_rules;

create sequence seq_embargo_type;

create sequence seq_graduation_month;

create sequence seq_language;

create sequence seq_major;

create sequence seq_person;

create sequence seq_preference;

create sequence seq_program;

create sequence seq_search_filter;

create sequence seq_submission;
