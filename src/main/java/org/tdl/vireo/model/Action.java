package org.tdl.vireo.model;

/**
 * Enumeration of action logs.
 *
 * ActionLogRepoCustom interface offers the following:
 *
 * create
 * create (without user)
 * createPublicLog
 * createAdvisorPublicLog (without user)
 * createPrivateLog
 *
 * The implementation in ActionLogRepoImpl call the first two create from the subsequent three.
 * All other calls to the first two are from tests.
 *
 * The last three are decomposed below.
 *
 * createPublicLog
 *   SubmissionController
 *     /create......................................................UNDETERMINED
 *     /{submissionId}/add-comment {commentVisibility: 'public'}....UNDETERMINED
 *     /batch-comment {commentVisibility: 'public'}.................UNDETERMINED
 *     /{submissionId}/update-field-value/{fieldProfileId}..........UNDETERMINED
 *     /{submissionId}/update-custom-action-value...................UNDETERMINED
 *     /batch-assign-to.............................................UNDETERMINED
 *     /{submissionId}/submit-date..................................UNDETERMINED
 *     /{submissionId}/assign-to....................................UNDETERMINED
 *     /{submissionId}/needs-correction.............................UNDETERMINED
 *     /{submissionId}/submit-corrections...........................UNDETERMINED
 *     /{submissionId}/add-message..................................STUDENT_MESSAGE
 *     /{submissionId}/{documentType}/upload-file...................UNDETERMINED
 *     /{submissionId}/{documentType}/rename-file...................UNDETERMINED
 *     /{submissionId}/{fieldValueId}/remove-file...................UNDETERMINED
 *     /{submissionId}/{documentType}/archive-file..................UNDETERMINED
 *
 *   SubmissionEmailService
 *     sendAdvisorEmails............................................UNDETERMINED
 *   SubmissionRepoImpl
 *     updateStatus.................................................UNDETERMINED
 *
 * createAdvisorPublicLog (without user)
 *   SubmissionController
 *     /{submissionId}/update-advisor-approval
 *
 *          {  embargo: {
 *               approve: boolean,..................................ADVISOR_APPROVE_SUBMISSION
 *               clearApproval: boolean.............................ADVISOR_CLEAR_APPROVE_SUBMISSION
 *             }, advisor: {
 *               approve: boolean...................................ADVISOR_APPROVE_EMBARGO
 *               clearApproval: boolean.............................ADVISOR_CLEAR_APPROVE_EMBARGO
 *             },
 *             message: string......................................ADVISOR_MESSAGE
 *          }
 *
 * createPrivateLog
 *   SubmissionController
 *     /{submissionId}/add-comment {commentVisibility: !'public'}...UNDETERMINED
 *     /batch-comment {commentVisibility: !'public'}................UNDETERMINED
 *     /{submissionId}/update-reviewer-notes........................UNDETERMINED
 *
 */
public enum Action {
    STUDENT_MESSAGE,
    ADVISOR_MESSAGE,
    ADVISOR_APPROVE_SUBMISSION,
    ADVISOR_CLEAR_APPROVE_SUBMISSION,
    ADVISOR_APPROVE_EMBARGO,
    ADVISOR_CLEAR_APPROVE_EMBARGO,
    // default value for backfilling existing action logs
    // should only be used until a comprehensive enumeration of
    // actions is known in the application which should
    // be conditioned against email workflow rules by action
    UNDETERMINED
}
