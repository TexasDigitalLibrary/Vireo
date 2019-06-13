var dataEmailRecipient1 = {
    id: 1,
    data: {
        id: 1
    },
    name: "emailRecipient1",
    type: "ASSIGNEE"
};

var dataEmailRecipient2 = {
    id: 2,
    data: {
        id: 2
    },
    name: "emailRecipient2",
    type: "CONTACT"
};

var dataEmailRecipient3 = {
    id: 3,
    data: {
        id: 1
    },
    name: "emailRecipient3",
    type: "ORGANIZATION"
};

var dataEmailRecipient4 = {
    id: 4,
    data: {
        id: 2
    },
    name: "emailRecipient4",
    type: "PLAIN_ADDRESS"
};

var dataEmailRecipient5 = {
    id: 5,
    data: {
        id: 3
    },
    name: "emailRecipient5",
    type: "SUBMITTER"
};

var dataEmailRecipient6 = {
    id: 6,
    data: {
        id: 3
    },
    name: "emailRecipient6",
    type: "ASSIGNEE"
};

var mockEmailRecipient = function($q) {
    var model = mockModel("EmailRecipient", $q, dataEmailRecipient1);

    return model;
};

angular.module("mock.emailRecipient", []).service("EmailRecipient", mockEmailRecipient);

