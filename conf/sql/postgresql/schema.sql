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
    primary key (id),
    unique (name, level)
);

create table committee_member_roles (
    JpaCommitteeMemberImpl_id int8 not null,
    roles varchar(255),
    roles_ORDER int4 not null,
    primary key (JpaCommitteeMemberImpl_id, roles_ORDER)
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

create table language (
    id int8 not null,
    displayOrder int4 not null,
    name varchar(255) not null unique,
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
    currentProgram varchar(255),
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

create table program (
    id int8 not null,
    displayOrder int4 not null,
    name varchar(255) not null unique,
    primary key (id)
);

create table search_filter (
    id int8 not null,
    name varchar(255) not null,
    publicFlag bool not null,
    rangeEnd date,
    rangeStart date,
    umiRelease bool,
    creator_id int8 not null,
    primary key (id),
    unique (creator_id, name)
);

create table search_filter_assignees (
    search_filter_id int8 not null,
    assigneeIds int8
);

create table search_filter_colleges (
    search_filter_id int8 not null,
    colleges varchar(255)
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
    UMIRelease bool,
    approvalDate timestamp,
    college varchar(255),
    committeeApprovalDate timestamp,
    committeeContactEmail varchar(255),
    committeeEmailHash varchar(255) unique,
    committeeEmbargoApprovalDate timestamp,
    defenseDate timestamp,
    degree varchar(255),
    degreeLevel int4,
    department varchar(255),
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
    program varchar(255),
    publishedMaterial varchar(326768),
    reviewerNotes varchar(326768),
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

create table submission_subjects (
    JpaSubmissionImpl_id int8 not null,
    documentSubjects varchar(255),
    documentSubjects_ORDER int4 not null,
    primary key (JpaSubmissionImpl_id, documentSubjects_ORDER)
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

alter table committee_member_roles 
    add constraint FK205EE65A9A0CF993 
    foreign key (JpaCommitteeMemberImpl_id) 
    references committee_member;

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

alter table search_filter_assignees 
    add constraint FK110B7E348E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_colleges 
    add constraint FK32FE0A0C8E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_degrees 
    add constraint FK884FE8D78E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_departments 
    add constraint FKB38A4D118E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_documenttypes 
    add constraint FK208979EE8E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_embargos 
    add constraint FK874E5E908E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_excluded_actionlogs 
    add constraint FKD187842A8E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_excluded_submissions 
    add constraint FK2DF4C028E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_included_actionlogs 
    add constraint FK5A7E35F88E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_included_submissions 
    add constraint FK98BED3F48E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_majors 
    add constraint FKFAC40E2A8E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_programs 
    add constraint FK1CFDEABF8E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_semesters 
    add constraint FK8BFDE7EB8E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_states 
    add constraint FK608DA528E0B1A22 
    foreign key (search_filter_id) 
    references search_filter;

alter table search_filter_text 
    add constraint FK8068257D8E0B1A22 
    foreign key (search_filter_id) 
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

alter table submission_subjects 
    add constraint FKD56F3C5A8B58B7C1 
    foreign key (JpaSubmissionImpl_id) 
    references submission;

create sequence seq_actionlog;

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

create sequence seq_embargo_type;

create sequence seq_graduation_month;

create sequence seq_language;

create sequence seq_major;

create sequence seq_person;

create sequence seq_preference;

create sequence seq_program;

create sequence seq_search_filter;

create sequence seq_submission;