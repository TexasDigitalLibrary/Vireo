angular.module('mock.fileService', []).service('FileService', function ($q) {
    var service = mockService($q);

    return service;
});
