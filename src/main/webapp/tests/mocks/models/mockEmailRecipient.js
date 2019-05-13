var dataEmailRecipient1 = {
    id: 1
};

var dataEmailRecipient2 = {
    id: 2
};

var dataEmailRecipient3 = {
    id: 3
};

var dataEmailRecipient4 = {
    id: 4
};

var dataEmailRecipient5 = {
    id: 5
};

var dataEmailRecipient6 = {
    id: 6
};

var mockEmailRecipient = function($q) {
    var model = mockModel("EmailRecipient", $q, dataEmailRecipient1);

    return model;
};

angular.module('mock.emailRecipient', []).service('EmailRecipient', mockEmailRecipient);

