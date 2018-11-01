angular.module('mock.sidebarService', []).service('service', function($q) {
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

    service.boxes = [];

    service.getBox = function(target) {
        return service.boxes[target];
    };

    service.getBoxes = function() {
        return service.boxes;
    };

    service.addBox = function(box) {
        service.boxes.push(box);
    };

    service.addBoxes = function(newBoxes) {
        angular.extend(service.boxes, newBoxes);
    };

    service.remove = function(box) {
        service.boxes.splice(box, 1);
    };

    service.clear = function() {
        service.boxes.length = 0;
    };

    return service;
});
