var dataInputType1 = {
    id: 1,
    name: "input type 1",
    validationPattern: "name",
    validation: [
        {
            message: "name validation pattern",
            pattern: ".*"
        }
    ]
};

var dataInputType2 = {
    id: 2,
    name: "input type 2",
    validationPattern: "email",
    validation: [
        {
            message: "email validation pattern",
            pattern: "^([_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})|)$"
        }
    ]
};

var dataInputType3 = {
    id: 3,
    name: "input type 3",
    validationPattern: "",
    validation: []
};

var dataInputType4 = {
    id: 4,
    name: "input type 4",
    validationPattern: "",
    validation: []
};

var dataInputType5 = {
    id: 5,
    name: "input type 5",
    validationPattern: "",
    validation: []
};

var dataInputType6 = {
    id: 6,
    name: "input type 6",
    validationPattern: "",
    validation: []
};

var mockInputType = function($q) {
    var model = mockModel("InputType", $q, dataInputType1);

    return model;
};

angular.module("mock.inputType", []).service("InputType", mockInputType);

