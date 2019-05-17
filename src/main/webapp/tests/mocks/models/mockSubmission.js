var dataSubmission1 = {
    id: 1,
    organization: {
        name: "organization 1"
    },
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

var dataSubmission2 = {
    id: 2,
    organization: {
        name: "organization 2"
    },
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

var dataSubmission3 = {
    id: 3,
    organization: {
        name: "organization 3"
    },
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

var dataSubmission4 = {
    id: 4,
    organization: {
        id: 1,
        name: "organization 4"
    },
    submissionStatus: {
        submissionState: "IN_PROGRESS"
    },
    submissionWorkflowSteps: [
    ]
};

var dataSubmission5 = {
    id: 5,
    organization: {
        id: 1,
        name: "organization 5"
    },
    submissionStatus: {
        submissionState: "SUBMITTED"
    },
    submissionWorkflowSteps: [
    ]
};

var dataSubmission6 = {
    id: 6,
    organization: {
        id: 2,
        name: "organization 6"
    },
    submissionStatus: {
        submissionState: "WITHDRAWN"
    },
    submissionWorkflowSteps: [
    ]
};

var mockSubmission = function($q) {
    var model = mockModel("Submission", $q, dataSubmission1);

    model.mockWorkflowSteps = null;

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

    model.changeStatus = function (submissionStatusName) {
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

        if (model.mockWorkflowSteps) {
            for (var i in model.mockWorkflowSteps.aggregateFieldProfiles) {
                var currentFieldProfile = model.mockWorkflowSteps.aggregateFieldProfiles[i];
                if (currentFieldProfile.fieldPredicate.id === predicate.id) {
                    fieldProfile = currentFieldProfile;
                    break;
                }
            }
        }

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
        };
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

angular.module('mock.submission', []).service('Submission', mockSubmission);

