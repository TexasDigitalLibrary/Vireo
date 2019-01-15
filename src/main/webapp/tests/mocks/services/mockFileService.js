angular.module('mock.fileService', []).service('FileService', function ($q) {
    var service = mockService($q);

    service.download = function (submission, fieldValue) {
        var download = {};
        // TODO
        return download;
    };

    return service;
});
