var dataLanguage1 = {
    id: 1,
    position: null,
    name: "English"
};

var dataLanguage2 = {
    id: 2,
    position: null,
    name: "Spanish"
};

var dataLanguage3 = {
    id: 3,
    position: null,
    name: "French"
};

var dataLanguage4 = {
    id: 4,
    position: null,
    name: "Chinese"
};

var dataLanguage5 = {
    id: 5,
    position: null,
    name: "Deutsch"
};

var dataLanguage6 = {
    id: 6,
    position: null,
    name: "Arabic"
};

var mockLanguage = function($q) {
    var model = mockModel("Language", $q, dataLanguage1);

    return model;
};

angular.module("mock.language", []).service("Language", mockLanguage);

