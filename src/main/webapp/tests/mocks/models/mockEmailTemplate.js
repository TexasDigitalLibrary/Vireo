var dataEmailTemplate1 = {
    id: 1,
    name: "name1",
    subject: "subject1",
    message: "message1",
    systemRequired: true

};

var dataEmailTemplate2 = {
    id: 2,
    name: "name2",
    subject: "subject2",
    message: "message2",
    systemRequired: false
};

var dataEmailTemplate3 = {
    id: 3,
    name: "name3",
    subject: "subject1",
    message: "message3",
    systemRequired: false
};

var dataEmailTemplate4 = {
    id: 4,
    name: "name4",
    subject: "subject2",
    message: "message2",
    systemRequired: true
};

var dataEmailTemplate5 = {
    id: 5,
    name: "name5",
    subject: "subject5",
    message: "message3",
    systemRequired: false
};

var dataEmailTemplate6 = {
    id: 6,
    name: "name6",
    subject: "subject6",
    message: "message6",
    systemRequired: false
};

var mockEmailTemplate = function($q) {
    var model = mockModel("EmailTemplate", $q, dataEmailTemplate1);

    return model;
};

angular.module("mock.emailTemplate", []).service("EmailTemplate", mockEmailTemplate);

