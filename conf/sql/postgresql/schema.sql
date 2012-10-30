create table actionlog (
    id int8 not null,
    actionDate timestamp,
    entry varchar(32768) not null,
    privateFlag bool not null,
    submissionState varchar(255) not null,
    attachment_id int8,
    person_id int8,
    submission_id int8 not null,
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
    primary key (id),
    unique (submission_id, name)
);

create table college (
    id int8 not null,
    displayOrder int4 not null,
    name varchar(255) not null unique,
    primary key (id)
);

create table committee_member (
    id int8 not null,
    chair bool not null,
    displayOrder int4 not null,
    firstName varchar(255),
    lastName varchar(255),
    middleName varchar(255),
    submission_id int8 not null,
    primary key (id)
);

create table configuration (
    id int8 not null,
    name varchar(255) not null unique,
    value varchar(32768),
    primary key (id)
);

create table custom_action_definition (
    id int8 not null,
    displayOrder int4 not null,
    label varchar(255) not null unique,
    primary key (id)
);

create table custom_action_value (
    id int8 not null,
    value bool not null,
    definition_id int8 not null,
    submission_id int8 not null,
    primary key (id),
    unique (submission_id, definition_id)
);

create table degree (
    id int8 not null,
    displayOrder int4 not null,
    level int4 not null,
    name varchar(255) not null,
    primary key (id),
    unique (name, level)
);

create table department (
    id int8 not null,
    displayOrder int4 not null,
    name varchar(255) not null unique,
    primary key (id)
);

create table deposit_location (
    id int8 not null,
    collection varchar(1024),
    depositor varchar(255),
    displayOrder int4 not null,
    name varchar(255) not null unique,
    onBehalfOf varchar(255),
    packager varchar(255),
    password varchar(255),
    repository varchar(1024),
    username varchar(255),
    primary key (id)
);

create table document_type (
    id int8 not null,
    displayOrder int4 not null,
    level int4 not null,
    name varchar(255) not null,
    primary key (id),
    unique (name, level)
);

create table email_template (
    id int8 not null,
    displayOrder int4 not null,
    message varchar(32768) not null,
    name varchar(255) not null unique,
    subject varchar(32768) not null,
    systemRequired bool not null,
    primary key (id)
);

create table embargo_type (
    id int8 not null,
    active bool not null,
    description varchar(32768) not null,
    displayOrder int4 not null,
    duration int4,
    name varchar(255) not null unique,
    primary key (id)
);

create table graduation_month (
    id int8 not null,
    displayOrder int4 not null,
    month int4 not null unique,
    primary key (id)
);

create table major (
    id int8 not null,
    displayOrder int4 not null,
    name varchar(255) not null unique,
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
    displayName varchar(255),
    email varchar(255) not null unique,
    firstName varchar(255),
    institutionalIdentifier varchar(255),
    lastName varchar(255),
    middleName varchar(255),
    netid varchar(255) unique,
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
    primary key (id),
    unique (person_id, name)
);

create table search_filter (
    id int8 not null,
    name varchar(255) not null,
    publicFlag bool not null,
    rangeEnd date,
    rangeStart date,
    umiRelease bool,
    unassigned bool,
    creator_id int8 not null,
    primary key (id),
    unique (creator_id, name)
);

create table search_filter_colleges (
    JpaNamedSearchFilterImpl_id int8 not null,
    colleges varchar(255)
);

create table search_filter_degrees (
    JpaNamedSearchFilterImpl_id int8 not null,
    degrees varchar(255)
);

create table search_filter_departments (
    JpaNamedSearchFilterImpl_id int8 not null,
    departments varchar(255)
);

create table search_filter_documenttypes (
    JpaNamedSearchFilterImpl_id int8 not null,
    documentTypes varchar(255)
);

create table search_filter_embargo_type (
    search_filter_id int8 not null,
    embargos_id int8 not null,
    unique (embargos_id)
);

create table search_filter_excluded_actionlogs (
    search_filter_id int8 not null,
    excludedActionLogs_id int8 not null,
    unique (excludedActionLogs_id)
);

create table search_filter_excluded_submissions (
    search_filter_id int8 not null,
    excludedSubmisisons_id int8 not null,
    unique (excludedSubmisisons_id)
);

create table search_filter_included_actionlogs (
    search_filter_id int8 not null,
    includedActionLogs_id int8 not null,
    unique (includedActionLogs_id)
);

create table search_filter_included_submissions (
    search_filter_id int8 not null,
    includedSubmisisons_id int8 not null,
    unique (includedSubmisisons_id)
);

create table search_filter_majors (
    JpaNamedSearchFilterImpl_id int8 not null,
    majors varchar(255)
);

create table search_filter_person (
    search_filter_id int8 not null,
    assignees_id int8 not null,
    unique (assignees_id)
);

create table search_filter_semesters (
    JpaNamedSearchFilterImpl_id int8 not null,
    semesters varchar(255)
);

create table search_filter_states (
    JpaNamedSearchFilterImpl_id int8 not null,
    states varchar(255)
);

create table search_filter_text (
    JpaNamedSearchFilterImpl_id int8 not null,
    searchText varchar(255)
);

create table submission (
    id int8 not null,
    UMIRelease bool,
    approvalDate timestamp,
    college varchar(255),
    committeeApprovalDate timestamp,
    committeeContactEmail varchar(255),
    committeeEmailHash varchar(255) unique,
    committeeEmbargoApprovalDate timestamp,
    degree varchar(255),
    degreeLevel int4,
    department varchar(255),
    depositId varchar(255),
    documentAbstract varchar(326768),
    documentKeywords varchar(326768),
    documentTitle varchar(326768),
    documentType varchar(255),
    graduationMonth int4,
    graduationYear int4,
    lastActionLogDate timestamp,
    lastActionLogEntry varchar(326768),
    licenseAgreementDate timestamp,
    major varchar(255),
    stateName varchar(255),
    studentBirthYear int4,
    studentFirstName varchar(255),
    studentLastName varchar(255),
    studentMiddleName varchar(255),
    submissionDate timestamp,
    assignee_id int8,
    embargoType_id int8,
    submitter_id int8 not null,
    primary key (id)
);

alter table actionlog 
    add constraint FKBD1F14E936E2B7C 
    foreign key (attachment_id) 
    references attachment;

alter table actionlog 
    add constraint FKBD1F14EF967E3C 
    foreign key (person_id) 
    references person;

alter table actionlog 
    add constraint FKBD1F14E2D7E525C 
    foreign key (submission_id) 
    references submission;

alter table attachment 
    add constraint FK8AF75923F967E3C 
    foreign key (person_id) 
    references person;

alter table attachment 
    add constraint FK8AF759232D7E525C 
    foreign key (submission_id) 
    references submission;

alter table committee_member 
    add constraint FK1792999C2D7E525C 
    foreign key (submission_id) 
    references submission;

alter table custom_action_value 
    add constraint FKE49B30366B22F363 
    foreign key (definition_id) 
    references custom_action_definition;

alter table custom_action_value 
    add constraint FKE49B30362D7E525C 
    foreign key (submission_id) 
    references submission;

alter table person_affiliations 
    add constraint FKE29E7C2DF4A0EAA1 
    foreign key (JpaPersonImpl_id) 
    references person;

alter table preference 
    add constraint FKA8FCBCDBF967E3C 
    foreign key (person_id) 
    references person;

alter table search_filter 
    add constraint FK40B835EF2EF2D605 
    foreign key (creator_id) 
    references person;

alter table search_filter_colleges 
    add constraint FK32FE0A0C2E6A9C33 
    foreign key (JpaNamedSearchFilterImpl_id) 
    references search_filter;

alter table search_filter_degrees 
    add constraint FK884FE8D72E6A9C33 
    foreign key (JpaNamedSearchFilterImpl_id) 
    references search_filter;

alter table search_filter_departments 
    add constraint FKB38A4D112E6A9C33 
    foreign key (JpaNamedSearchFilterImpl_id) 
    references search_filter;

alter table search_filter_documenttypes 
    add constraint FK208979EE2E6A9C33 
    foreign key (JpaNamedSearchFilterImpl_id) 
    references search_filter;

alter table search_filter_embargo_type 
    add constraint FK63F94FB68E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_embargo_type 
    add constraint FK63F94FB69C9E0205 
    foreign key (embargos_id) 
    references embargo_type;

alter table search_filter_excluded_actionlogs 
    add constraint FKD187842A8E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_excluded_actionlogs 
    add constraint FKD187842AF3D6CD77 
    foreign key (excludedActionLogs_id) 
    references actionlog;

alter table search_filter_excluded_submissions 
    add constraint FK2DF4C02CEC613F 
    foreign key (excludedSubmisisons_id) 
    references submission;

alter table search_filter_excluded_submissions 
    add constraint FK2DF4C028E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_included_actionlogs 
    add constraint FK5A7E35F88E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_included_actionlogs 
    add constraint FK5A7E35F819E843C5 
    foreign key (includedActionLogs_id) 
    references actionlog;

alter table search_filter_included_submissions 
    add constraint FK98BED3F4A909B4B1 
    foreign key (includedSubmisisons_id) 
    references submission;

alter table search_filter_included_submissions 
    add constraint FK98BED3F48E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_majors 
    add constraint FKFAC40E2A2E6A9C33 
    foreign key (JpaNamedSearchFilterImpl_id) 
    references search_filter;

alter table search_filter_person 
    add constraint FK1EA7A56F7C2AAD 
    foreign key (assignees_id) 
    references person;

alter table search_filter_person 
    add constraint FK1EA7A58E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_semesters 
    add constraint FK8BFDE7EB2E6A9C33 
    foreign key (JpaNamedSearchFilterImpl_id) 
    references search_filter;

alter table search_filter_states 
    add constraint FK608DA522E6A9C33 
    foreign key (JpaNamedSearchFilterImpl_id) 
    references search_filter;

alter table search_filter_text 
    add constraint FK8068257D2E6A9C33 
    foreign key (JpaNamedSearchFilterImpl_id) 
    references search_filter;

alter table submission 
    add constraint FK84363B4C62B46408 
    foreign key (submitter_id) 
    references person;

alter table submission 
    add constraint FK84363B4C44904182 
    foreign key (assignee_id) 
    references person;

alter table submission 
    add constraint FK84363B4CC6F816D8 
    foreign key (embargoType_id) 
    references embargo_type;

create sequence seq_actionlog;

create sequence seq_attachment;

create sequence seq_college;

create sequence seq_committee_member;

create sequence seq_configuration;

create sequence seq_custom_action_definition;

create sequence seq_custom_action_value;

create sequence seq_degree;

create sequence seq_department;

create sequence seq_deposit_location;

create sequence seq_document_type;

create sequence seq_email_template;

create sequence seq_embargo_type;

create sequence seq_graduation_month;

create sequence seq_major;

create sequence seq_person;

create sequence seq_preference;

create sequence seq_search_filter;

create sequence seq_submission;