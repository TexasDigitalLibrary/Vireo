var dataEmbargo1 = {
    id: 1,
    description: "Description 1",
    duration: 12,
    guarantor: "DEFAULT",
    isActive: true,
    name: "Embargo 1",
    systemRequired: true
};

var dataEmbargo2 = {
    id: 2,
    description: "Description 2",
    duration: 24,
    guarantor: "DEFAULT",
    isActive: true,
    name: "Embargo 2",
    systemRequired: false
};

var dataEmbargo3 = {
    id: 3,
    description: "Description 3",
    duration: null,
    guarantor: "DEFAULT",
    isActive: false,
    name: "Embargo 3",
    systemRequired: false
};

var dataEmbargo4 = {
    id: 4,
    description: "Description 4",
    duration: 12,
    guarantor: "PROQUEST",
    isActive: true,
    name: "Embargo 4",
    systemRequired: true
};

var dataEmbargo5 = {
    id: 5,
    description: "Description 5",
    duration: 24,
    guarantor: "PROQUEST",
    isActive: true,
    name: "Embargo 5",
    systemRequired: false
};

var dataEmbargo6 = {
    id: 6,
    description: "Description 6",
    duration: null,
    guarantor: "PROQUEST",
    isActive: false,
    name: "Embargo 6",
    systemRequired: false
};

var mockEmbargo = function($q) {
    var model = mockModel("Embargo", $q, dataEmbargo1);

    return model;
};

angular.module("mock.embargo", []).service("Embargo", mockEmbargo);

