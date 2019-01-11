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
        // TODO
        return fileType;
    };

    service.isPrimaryDocument = function (fieldPredicate) {
        // TODO
        return true;
    };

    service.removeFile = function (submission, fieldValue) {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    service.uploadFile = function (submission, fieldValue) {
        var response = {
            data: {
                meta: {
                    status: "SUCCESS",
                },
                payload: {},
                status: 200
            }
        };
        // TODO
        return valuePromise($q.defer(), response);
    };

    return service;
});
