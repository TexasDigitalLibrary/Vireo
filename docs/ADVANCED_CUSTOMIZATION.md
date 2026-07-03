# Advanced Default Settings Configuration

The default system values can be customized by editing the JSON files found in `/src/main/java/resources/`

*Changes to default values can damage your installation and should be done with caution*

| Resource Type | Files |
|---------------|-------|
| [Controlled Vocabularies](#controlled-vocabularies) | - AdministrativeGroups.json<br/> - Colleges.json<br/> - CommitteeMembers.json<br/> - Departments.json<br/> - Majors.json<br/> - ManuscriptAllowedMimeTypes.json<br/> - Programs.json<br/> - SubmissionTypes.json |
| [Degree Levels](#degree-levels) | - SYSTEM_Degree-Levels.json |
| [Degrees](#degrees) | - SYSTEM_Degrees.json |
| [Document Types](#document-types) | - SYSTEM_Document_Types.json |
| [Emails](#emails) | - SYSTEM_Advisor_Review_Request.email<br/> - SYSTEM_Deposit_Notification.email<br/> - SYSTEM_Email_Test.email<br/> - SYSTEM_Initial_Submission.email<br/> - SYSTEM_Needs_Corrections.email<br/> - SYSTEM_New_User_Registration.email<br/> - SYSTEM_Verify_Email_Address.email |
| [Embargos](#embargos) | - SYSTEM_Embargo_Definitions.json |
| [Filter Columns](#filter-columns) | - default_filter_columns.json |
| [Formats](#formats) | - dspace_mets.xml<br/> - dspace_simple_dublin_core.xml<br/> - dspace_simple_metadata_local.xml<br/> - marc21_xml.xml<br/> - proquest_umi.xml |
| [Graduation Months](#graduation-months) | - SYSTEM_Graduation_Months.json |
| [Input Types](#input-types) | - SYSTEM_Input_Types.json |
| [Languages](#languages) | - SYSTEM_Languages.json |
| [Organization](#organization) | - SYSTEM_Organization_Definition.json |
| [Organization Categories](#organization-categories) | - SYSTEM_Organization_Categories.json |
| [ProQuest](#proquest) | - degree_codes.xls<br/> - language_codes.xls<br/> - umi_subjects.xls |
| [Settings](#settings) | - SYSTEM_Defaults.json |
| [Submission List Columns](#submission-list-columns) | - SYSTEM_Submission_List_Columns_Titles.json<br/> - SYSTEM_Submission_List_Columns.json |
| [Submission Statuses](#submission-statuses) | - SYSTEM_Submission_Statuses.json |

## Controlled Vocabularies

| Key | Type | Description |
|----|----|----|
| name | String | Name of the Controlled Vocabulary |
| isEntityProperty | Boolean | Indicates if the Controlled Vocabulary is the property of a system entity.  |
| dictionary | Array[[VocabularyWord](#vocabulary-word)] | Array of the Vocabulary Words |

### Vocabulary Word

| Key | Type | Description |
|----|----|----|
| name | String | Name of the Vocabulary Word |
| definition | String | Definition of the Vocabulary Word (optional) |
| identifier | String | Identifier of the Vocabulary Word (optional) |
| contacts | Array[String] | A list of contact information in the form of email addresses (optional) |

## Degree Levels

Array of [Degree Levels](#degree-level)

### Degree Level
| Key | Type | Description |
|----|----|----|
| name | String | Name of the Degree Level |

## Degrees

Array of [Degrees](#degree)

### Degree

| Key | Type | Description |
|----|----|----|
| name | String | Name of the Degree |
| level | [DegreeLevel](#degree-level) | Degree Level of the Degree |

## Document Types

Array of [DocumentTypes](#document-type)

### Document Type

| Key | Type | Description |
|----|----|----|
| name | String | Name of the Document Type |
| fieldPredicate | FieldPredicate | The Field Predicate associated with this Document Type |

## Emails

Email Templates can dynamically use any Field Predicate associated with the Submission and the following list of static values.

| Value | Description |
|----|----|
| FULL_NAME | Submitter's full name |
| FIRST_NAME | Submitter's first name |
| LAST_NAME | Submitter's last name |
| STUDENT_URL | URL for viewing a submitted submission from the submitter's view |
| SUBMISSION_URL | URL for viewing an in progress submission from the submitter's view |
| ADVISOR_URL | URL for viewing a submitted submission from the advisor's view |
| DEPOSIT_URI | URI where a complete submission was publised |
| DOCUMENT_TITLE | The title of the submission's primary document |
| SUBMISSION_TYPE | The submission type of the submission |


## Embargos

| Key | Type | Description |
|----|----|----|
| name | String | Name of the Embargo |
| description | String | Description of the Embargo |
| duration | Number | Number of months a submission's publication will be delayed |
| isActive | Boolean | Indicates if the Embargo is active |
| isSystemRequired | Boolean | Indicates if the Embargo is required by the system |
| guarantor | Number | The guarantor of the Embargo (Default or ProQuest) |

## Filter Columns

Array of [Filter Columns](#filter-column)

## Filter Column

| Key | Type | Description |
|----|----|----|
| inputType | [InputType](#input-type) | The Input Type of the column being filtered |
| title | String | Title of the Filter Column |
| predicate | String | Predicate associatied with this Filter Column |

## Formats

Templates for the various exporters. Written using [Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html) templating engine syntax.

## Graduation Months

Array of [Months](#month)

## Month

| Key | Type | Description |
|----|----|----|
| month | Number | Zero-indexed Month of the year |

## Input Types

Array of [InputType](#input-type)

## Input Type

| Key | Type | Description |
|----|----|----|
| name | String | Name of the Input Type |
| validationPattern | String | Regular expression for validating the value of the field, null if not used |
| validationMessage | String | Failure message for validation, null if validationPattern is not used |
| validation | Object | Validations for contact type inputs, optional otherwise. Consists of name and email, each with a pattern and message. |

## Languages

Array of [Languages](#language)

## Language

| Key | Type | Description |
|----|----|----|
| name | String | Name of the Language |


## Organization

| Key | Type | Description |
|----|----|----|
| name | String | Name of the Organization |
| acceptsSubmissions | Boolean | Indicates if the Organization accepts new Submissions |
| category | [OrganizationCategory](#organization-category) | Category of the Organization |
| originalWorkflowSteps | Array[[WorkflowStep](#workflow-step)] | List of Workflow Steps |
| parentOrganization | [Organization](#organization) | The Parent Organization (optional) |
| childrenOrganizations | Array[[Organization](#organization)] | List of child Organizations (optional) |
| emails | Array[String] | List of emails associated with the Organization (optional) |
| emailWorkflowRules | Array[EmailWorkflowRule] | List of Email Workflow Rules |

## Workflow Step

| Key | Type | Description |
|----|----|----|
| name | String | Name of the Workflow Step |
| instructions | String | HTML formatted instructions for the Workflow Step |
| overrideable | Boolean | Indicated if the Workflow Step is overrideable |
| originatingOrganization | [Organization](#organization) | Organization the Workflow Step originates from |
| originalNotes | Array[[Note](#note)] | List of Notes associated with the Workflow Step |
| originalFieldProfiles | Array[[FieldProfile](#field-profile)] | List of Field Profiles associated with the Workflow Step |

## Note

| Key | Type | Description |
|----|----|----|
| note | String | Name of the Note |
| text | String | Text of the Note's content |
| originatingWorkflowStep | [WorkflowStep](#workflow-step) | The Workflow Step the Note originates from |

## Field Profile

| Key | Type | Description |
|----|----|----|
| fieldPredicate | [FieldPredicate](#field-predicate) | Field Predicate for the Field Profile |
| originatingWorkflowStep | [WorkflowStep](#workflow-step) | The Workflow Step the Field Profile originates from |
| inputType | InputType | Input Type for the Field Profile |
| repeatable | Boolean | Indicates if the Field Profile is repeatable |
| overrideable | Boolean | Indicates if the Field Profile is overrideable |
| enabled | Boolean | Indicates if the Field Profile is enabled |
| optional | Boolean | Indicates if the Field Profile is optional |
| flagged | Boolean | Indicates if the Field Profile is flagged |
| logged | Boolean | Indicates if the Field Profile is logged |
| useage | String | Indicated the useage of the Field Profile (optional) |
| help | String | Help text for the Field Profile (optional) |
| gloss | String | The displayed value for the Field Profile |

## Field Predicate

| Key | Type | Description |
|----|----|----|
| value | String | Value of the Field Predicate |
| documentTypePredicate | Boolean | Indicates if the Field Predicate is a Document |

## Organization Categories

Array of [Organization Categories](#organization-category)

## Organization Category

| Key | Type | Description |
|----|----|----|
| name | String | Name of the Organization Category |
| organizations | Array[[Organization](#organization)] | List of Organizations in this Category |


### Proquest

Excel files with the Degree Codes, Language Codes, and UMI Subjects in tabular format.

## Settings

Default configuration settings. These values can be set here, or configured via the application UI.

## Submission List Columns

Contains Submission List Column Titles, an Array[String] for the default column names and Submission List Columns an Array[[SubmissionListColumn](#submission-list-column)].

## Submission List Column

| Key | Type | Description |
|----|----|----|
| title | String | Title of the Column |
| sort | String | Either 'ASC' or 'DESC' for ascending or descending sorting respectively |
| valuePath | Array[String] | The Value Paths for the Column |
| status | String | Status of the Column, typically null |
| inputType | [InputType](#input-type) | The input type associated with the Column |

## Submission Statuses

An Object that defines default system [Submission Statuses](#submission-status) and their suggested transitions.

## Submission Status

| Key | Type | Description |
|----|----|----|
| name | String | Name of the Status |
| isArchived | Boolean | Indicates if the Status is for archived Submissions |
| isPublishable | Boolean | Indicates if the Status is for Submissions ready for publication |
| isDeletable | Boolean | Indicates if the Status is for Submissions that can be deleted |
| isEditableByReviewer | Boolean | Indicates if the Status is for Submissions that can be editied by the Reviewer |
| isEditableByStudent | Boolean | Indicates if the Status is for Submissions that can be edited by the Student |
| isActive | Boolean | Indicated if the Status is for active Submissions |
| submissionState | Number | The order for Submission Statuses, beginning with 1 |
| transitionSubmissionStatuses | Array[[SubmissionStatus](#submission-status)] | A list of suggested Submission States to transition to |
