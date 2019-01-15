var dataStudentSubmission1 = {
    id: 1,
    submissionStatus: {
        submissionState: "IN_PROGRESS"
    },
    submissionWorkflowSteps: [
    ],
    submitter: {
        uin: "123456789",
        lastName: "Daniels",
        firstName: "Jack",
        name: "jack",
        role: "ROLE_ADMIN"
    }
};

var dataStudentSubmission2 = {
    id: 2,
    submissionStatus: {
        submissionState: "IN_PROGRESS"
    },
    submissionWorkflowSteps: [
    ],
    submitter: {
        uin: "123456789",
        lastName: "Daniels",
        firstName: "Jack",
        name: "jack",
        role: "ROLE_ADMIN"
    }
};

var dataStudentSubmission3 = {
    id: 3,
    submissionStatus: {
        submissionState: "IN_PROGRESS"
    },
    submissionWorkflowSteps: [
    ],
    submitter: {
        uin: "123456789",
        lastName: "Daniels",
        firstName: "Jack",
        name: "jack",
        role: "ROLE_ADMIN"
    }
};

var dataStudentSubmission4 = {
    id: 4,
    organization: {
        name: "organization 4"
    },
    submissionStatus: {
        submissionState: "UNDER_REVIEW"
    },
    submissionWorkflowSteps: [
    ],
    submitter: {
        uin: "123456789",
        lastName: "Daniels",
        firstName: "Jack",
        name: "jack",
        role: "ROLE_ADMIN"
    }
};

var dataStudentSubmission5 = {
    id: 5,
    organization: {
        name: "organization 5"
    },
    submissionStatus: {
        submissionState: "ON_HOLD"
    },
    submissionWorkflowSteps: [
    ],
    submitter: {
        uin: "123456789",
        lastName: "Daniels",
        firstName: "Jack",
        name: "jack",
        role: "ROLE_ADMIN"
    }
};

var dataStudentSubmission6 = {
    id: 6,
    organization: {
        name: "organization 6"
    },
    submissionStatus: {
        submissionState: "CANCELLED"
    },
    submissionWorkflowSteps: [
    ],
    submitter: {
        uin: "123456789",
        lastName: "Daniels",
        firstName: "Jack",
        name: "jack",
        role: "ROLE_ADMIN"
    }
};

var mockStudentSubmission = function($q) {
    var model = mockModel($q, dataStudentSubmission1);

    model.actionLogListenPromise = null;

    model.addComment = function (data) {
        return payloadPromise($q.defer());
    };

    model.addFieldValue = function (fieldPredicate) {
        var fieldValue = {
            value: "",
            fieldPredicate: fieldPredicate
        };
        return fieldValue;
    };

    model.addMessage = function (message) {
        return payloadPromise($q.defer());
    };

    model.archiveFile = function (fieldValue) {
        return payloadPromise($q.defer());
    };

    model.assign = function (assignee) {
        return payloadPromise($q.defer());
    };

    model.changeStatus = function (studentSubmissionStatusName) {
        return payloadPromise($q.defer());
    };

    model.fetchDocumentTypeFileInfo = function () {
    };

    model.file = function (uri) {
        return payloadPromise($q.defer());
    };

    model.fileInfo = function (fieldValue) {
        return payloadPromise($q.defer());
    };

    model.findFieldValueById = function (id) {
        var fieldValue = {
            value: "",
            fieldPredicate: {}
        };
        return fieldValue;
    };

    model.getContactEmails = function () {
        return payloadPromise($q.defer());
    };

    model.getFieldProfileByPredicate = function (predicate) {
        var fieldProfile = null;
        return fieldProfile;
    };

    model.getFieldProfileByPredicateName = function (predicateValue) {
        var fieldProfile = null;
        return fieldProfile;
    };

    model.getFieldValuesByFieldPredicate = function (fieldPredicate) {
        var fieldValues = [];
        return fieldValues;
    };

    model.getFieldValuesByInputType = function (inputType) {
        var fieldValues = [];
        return fieldValues;
    };

    model.getFileType = function (fieldPredicate) {
        return payloadPromise($q.defer());
    };

    model.getFlaggedFieldProfiles = function () {
        return payloadPromise($q.defer());
    };

    model.getPrimaryDocumentFieldProfile = function () {
        var fieldProfile = new mockFieldProfile($q);
        return fieldProfile;
    };

    model.publish = function (depositLocation) {
        return payloadPromise($q.defer());
    };

    model.needsCorrection = function () {
        return payloadPromise($q.defer());
    };

    model.removeAllUnsavedFieldValuesByPredicate = function (fieldPredicate) {
    };

    model.removeFieldValue = function (fieldValue) {
        return payloadPromise($q.defer());
    };

    model.removeFile = function (fieldValue) {
        return payloadPromise($q.defer());
    };

    model.removeUnsavedFieldValue = function (fieldValue) {
    };

    model.renameFile = function (fieldValue) {
        return payloadPromise($q.defer());
    };

    model.saveFieldValue = function (fieldValue, fieldProfile) {
        return payloadPromise($q.defer());
    };

    model.saveReviewerNotes = function (reviewerNotes) {
        return payloadPromise($q.defer());
    };

    model.sendEmail = function (data) {
        return payloadPromise($q.defer());
    };

    model.sendAdvisorEmail = function () {
        return payloadPromise($q.defer());
    };

    model.setSubmissionDate = function (newDate) {
        return payloadPromise($q.defer());
    };

    model.submit = function () {
        return payloadPromise($q.defer());
    };

    model.submitCorrections = function () {
        return payloadPromise($q.defer());
    };

    model.updateAdvisorApproval = function (approval) {
        var payload = {
            Submission: angular.copy(model)
        }
        return payloadPromise($q.defer(), payload);
    };

    model.updateCustomActionValue = function (customActionValue) {
        return payloadPromise($q.defer());
    };

    model.validate = function () {
        model.isValid = true;
    };

    model.validateFieldValue = function (fieldValue, fieldProfile) {
        return payloadPromise($q.defer());
    };

    return model;
};

angular.module('mock.studentSubmission', []).service('StudentSubmission', mockStudentSubmission);

