angular.module("mock.fileService", []).service("FileService", function ($q) {
    var service = mockService($q);

    service.download = function (req) {
        var download = {};
        return download;
    };

    service.anonymousDownload = function (req) {
        var download = {};
        return download;
    };

    return service;
});
