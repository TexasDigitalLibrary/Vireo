var mockSubmission1 = {
    'id': 1
};

var mockSubmission2 = {
    'id': 2
};

var mockSubmission3 = {
    'id': 3
};

angular.module('mock.submission', []).service('Submission', function($q) {
    var model = this;
    var defer;
    var payloadResponse = function (payload) {
        return defer.resolve({
            body: angular.toJson({
                meta: {
                    status: 'SUCCESS'
                },
                payload: payload
            })
        });
    };

    model.isDirty = false;
    model.isValid = false;
    model.actionLogListenPromise = null;

    model.mock = function(toMock) {
        model.id = toMock.id;
    };

    model.addComment = function (data) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.addFieldValue = function (fieldPredicate) {
        var fieldValue = {
            value: "",
            fieldPredicate: fieldPredicate
        };
        return fieldValue;
    };

    model.addMessage = function (message) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.archiveFile = function (fieldValue) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.assign = function (assignee) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.changeStatus = function (submissionStatusName) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.clearValidationResults = function () {
    };

    model.delete = function() {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.dirty = function(boolean) {
        model.isDirty = boolean;
    };

    model.fetchDocumentTypeFileInfo = function () {
    };

    model.file = function (uri) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.fileInfo = function (fieldValue) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.findFieldValueById = function (id) {
        var fieldValue = {
            value: "",
            fieldPredicate: {}
        };
        return fieldValue;
    };

    model.getContactEmails = function () {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
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
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.getFlaggedFieldProfiles = function () {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.getPrimaryDocumentFieldProfile = function () {
        var fieldProfile = null;
        // TODO
        return fieldProfile;
    };

    model.publish = function (depositLocation) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.needsCorrection = function () {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.removeAllUnsavedFieldValuesByPredicate = function (fieldPredicate) {
    };

    model.removeFieldValue = function (fieldValue) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.removeFile = function (fieldValue) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.removeUnsavedFieldValue = function (fieldValue) {
    };

    model.renameFile = function (fieldValue) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.save = function() {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.saveFieldValue = function (fieldValue, fieldProfile) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.saveReviewerNotes = function (reviewerNotes) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.sendEmail = function (data) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.sendAdvisorEmail = function () {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.setSubmissionDate = function (newDate) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.submit = function () {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.submitCorrections = function () {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.updateAdvisorApproval = function (approval) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.updateCustomActionValue = function (customActionValue) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    model.validate = function () {
        model.isValid = true;
    };

    model.validateFieldValue = function (fieldValue, fieldProfile) {
        defer = $q.defer();
        payloadResponse();
        return defer.promise;
    };

    return model;
});
