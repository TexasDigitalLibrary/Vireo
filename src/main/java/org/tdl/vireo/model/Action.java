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
 *     /create......................................................
 *     /{submissionId}/add-comment {commentVisibility: 'public'}....
 *     /batch-comment {commentVisibility: 'public'}.................
 *     /{submissionId}/update-field-value/{fieldProfileId}..........
 *     /{submissionId}/update-custom-action-value...................
 *     /batch-assign-to.............................................
 *     /{submissionId}/submit-date..................................
 *     /{submissionId}/assign-to....................................
 *     /{submissionId}/needs-correction.............................
 *     /{submissionId}/submit-corrections...........................
 *     /{submissionId}/add-message..................................STUDENT_MESSAGE
 *     /{submissionId}/{documentType}/upload-file...................
 *     /{submissionId}/{documentType}/rename-file...................
 *     /{submissionId}/{fieldValueId}/remove-file...................
 *     /{submissionId}/{documentType}/archive-file..................
 *
 *   SubmissionEmailService
 *     sendAdvisorEmails............................................
 *   SubmissionRepoImpl
 *     updateStatus.................................................
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
 *     /{submissionId}/add-comment {commentVisibility: !'public'}
 *     /batch-comment {commentVisibility: !'public'}
 *     /{submissionId}/update-reviewer-notes
 *
 */
public enum Action {
    STUDENT_MESSAGE,
    ADVISOR_MESSAGE,
    ADVISOR_APPROVE_SUBMISSION,
    ADVISOR_CLEAR_APPROVE_SUBMISSION,
    ADVISOR_APPROVE_EMBARGO,
    ADVISOR_CLEAR_APPROVE_EMBARGO
}
