var dataValidation1 = {
    id: 1,
    message: "name validation pattern",
    pattern: ".*"
};

var dataValidation2 = {
    id: 2,
    message: "email validation pattern",
    pattern: "^([_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})|)$"
};

var dataValidation3 = {
    id: 3,
    message: "mock validation pattern",
    pattern: "^mock$"
};

var dataValidation4 = {
    id: 4,
    message: "mock4 validation pattern",
    pattern: "^mock4$"
};

var dataValidation5 = {
    id: 5,
    message: "mock digit validation pattern",
    pattern: "^mock[0-9]+$"
};

var dataValidation6 = {
    id: 6,
    message: "mock optional digit validation pattern",
    pattern: "^mock[0-9]*$"
};

var mockValidation = function($q) {
    var model = mockModel("Validation", $q, dataValidation1);

    return model;
};

angular.module("mock.validation", []).service("Validation", mockValidation);

