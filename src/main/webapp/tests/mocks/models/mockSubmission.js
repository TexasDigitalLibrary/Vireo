var mockSubmission1 = {
    'id': 1
};

var mockSubmission2 = {
    'id': 2
};

var mockSubmission3 = {
    'id': 3
};

var mockSubmission = function($q) {
    var model = mockModel($q, mockSubmission1);

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
        // TODO
        return fieldProfile;
    };

    model.getFieldProfileByPredicateName = function (predicateValue) {
        var fieldProfile = null;
        // TODO
        return fieldProfile;
    };

    model.getFieldValuesByFieldPredicate = function (fieldPredicate) {
        var fieldValues = [];
        // TODO
        return fieldValues;
    };

    model.getFieldValuesByInputType = function (inputType) {
        var fieldValues = [];
        // TODO
        return fieldValues;
    };

    model.getFileType = function (fieldPredicate) {
        return payloadPromise($q.defer());
    };

    model.getFlaggedFieldProfiles = function () {
        return payloadPromise($q.defer());
    };

    model.getPrimaryDocumentFieldProfile = function () {
        var fieldProfile = null;
        // TODO
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
        return payloadPromise($q.defer());
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

