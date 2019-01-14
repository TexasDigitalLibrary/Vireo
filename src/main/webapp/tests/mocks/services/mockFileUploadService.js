angular.module('mock.fileUploadService', []).service('FileUploadService', function($q) {
    var service = mockService($q);

    service.archiveFile = function (submission, fieldValue, removeFieldValue) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    service.download = function (submission, fieldValue) {
        var download = {};
        // TODO
        return download;
    };

    service.getFileType = function (fieldPredicate) {
        var fileType = "";
        if (fieldPredicate.value) {
            fileType = fieldPredicate.value;
        }
        return fileType;
    };

    service.isPrimaryDocument = function (fieldPredicate) {
        return fieldPredicate.value && fieldPredicate.value == "PRIMARY";
    };

    service.removeFile = function (submission, fieldValue) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    service.uploadFile = function (submission, fieldValue) {
        var response = {};
        // TODO
        return dataPromise($q.defer(), response);
    };

    return service;
});
