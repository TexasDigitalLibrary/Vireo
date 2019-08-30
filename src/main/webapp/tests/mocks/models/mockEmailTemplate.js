var dataEmailTemplate1 = {
    id: 1,
    name: "name 1",
    message: "message 1",
    subject: "subject 1",
    systemRequired: true

};

var dataEmailTemplate2 = {
    id: 2,
    name: "name 2",
    message: "message 2",
    subject: "subject 2",
    systemRequired: false
};

var dataEmailTemplate3 = {
    id: 3,
    name: "name 3",
    message: "message 3",
    subject: "subject 1",
    systemRequired: false
};

var dataEmailTemplate4 = {
    id: 4,
    name: "name 4",
    message: "message 2",
    subject: "subject 2",
    systemRequired: true
};

var dataEmailTemplate5 = {
    id: 5,
    name: "name 5",
    message: "message 3",
    subject: "subject 5",
    systemRequired: false
};

var dataEmailTemplate6 = {
    id: 6,
    name: "name 6",
    message: "message 6",
    subject: "subject 6",
    systemRequired: false
};

var mockEmailTemplate = function($q) {
    var model = mockModel("EmailTemplate", $q, dataEmailTemplate1);

    return model;
};

angular.module("mock.emailTemplate", []).service("EmailTemplate", mockEmailTemplate);

