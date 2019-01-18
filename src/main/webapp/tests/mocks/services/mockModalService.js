angular.module('mock.modalService', []).service('ModalService', function ($q) {
    var service = mockService($q);

    service.openModal = function (id) {
    };

    service.closeModal = function () {
    };

    return service;
});
