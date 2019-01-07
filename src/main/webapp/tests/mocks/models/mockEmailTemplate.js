var mockEmailTemplate1 = {
    id: 1
};

var mockEmailTemplate2 = {
    id: 2
};

var mockEmailTemplate3 = {
    id: 3
};

var mockEmailTemplate = function($q) {
    var model = mockModel($q, mockEmailTemplate1);

    return model;
};

angular.module('mock.emailTemplate', []).service('EmailTemplate', mockEmailTemplate);

