angular.module('mock.fileUploadService', []).service('FileUploadService', function($q) {
    var service = this;
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

    var messageResponse = function (message) {
        return defer.resolve({
            body: angular.toJson({
                meta: {
                    status: 'SUCCESS',
                    message: message
                }
            })
        });
    };

    service.archiveFile = function (submission, fieldValue, removeFieldValue) {
        return $q(function (resolve) {
            // TODO
        });
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
        return $q(function (resolve) {
            // TODO
        });
    };

    service.uploadFile = function (submission, fieldValue) {
        var upload = {};
        // TODO
        return upload;
    };

    return service;
});
