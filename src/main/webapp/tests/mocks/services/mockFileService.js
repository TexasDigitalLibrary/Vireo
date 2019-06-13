angular.module("mock.fileService", []).service("FileService", function ($q) {
    var service = mockService($q);

    service.download = function (submission, fieldValue) {
        var download = {};
        return download;
    };

    return service;
});
