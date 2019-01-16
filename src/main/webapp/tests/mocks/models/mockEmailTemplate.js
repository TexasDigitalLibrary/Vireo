var dataEmailTemplate1 = {
    id: 1
};

var dataEmailTemplate2 = {
    id: 2
};

var dataEmailTemplate3 = {
    id: 3
};

var dataEmailTemplate4 = {
    id: 4
};

var dataEmailTemplate5 = {
    id: 5
};

var dataEmailTemplate6 = {
    id: 6
};

var mockEmailTemplate = function($q) {
    var model = mockModel("EmailTemplate", $q, dataEmailTemplate1);

    return model;
};

angular.module('mock.emailTemplate', []).service('EmailTemplate', mockEmailTemplate);

