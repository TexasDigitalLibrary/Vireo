var dataEmailTemplate1 = {
    id: 1
};

var dataEmailTemplate2 = {
    id: 2
};

var dataEmailTemplate3 = {
    id: 3
};

var mockEmailTemplate = function($q) {
    var model = mockModel($q, dataEmailTemplate1);

    return model;
};

angular.module('mock.emailTemplate', []).service('EmailTemplate', mockEmailTemplate);

