var dataVocabularyWord1 = {
    id: 1,
    identifier: "vw1",
    adding: false,
    beginAdd: false,
    clickedCell: false,
    contacts: "a,b",
    definition: "",
    editing: false,
    moving: false,
    name: "DEFAULT"
};

var dataVocabularyWord2 = {
    id: 2,
    identifier: "vw2",
    adding: false,
    beginAdd: false,
    clickedCell: false,
    contacts: ["a", "c"],
    definition: "",
    editing: false,
    moving: false,
    name: "PROQUEST"
};

var dataVocabularyWord3 = {
    id: 3,
    identifier: "vw3",
    adding: false,
    beginAdd: false,
    clickedCell: false,
    contacts: [],
    definition: "",
    editing: false,
    moving: false,
    name: "PROQUEST"
};

var dataVocabularyWord4 = {
    id: 4,
    identifier: "vw4",
    adding: false,
    beginAdd: false,
    clickedCell: false,
    contacts: "a,b",
    definition: "",
    editing: false,
    moving: false,
    name: "DEFAULT"
};

var dataVocabularyWord5 = {
    id: 5,
    identifier: "vw5",
    adding: false,
    beginAdd: false,
    clickedCell: false,
    contacts: ["a", "c"],
    definition: "",
    editing: false,
    moving: false,
    name: "PROQUEST"
};

var dataVocabularyWord6 = {
    id: 6,
    identifier: "vw6",
    adding: false,
    beginAdd: false,
    clickedCell: false,
    contacts: [],
    definition: "",
    editing: false,
    moving: false,
    name: "PROQUEST"
};


var mockVocabularyWord = function($q) {
    var model = mockModel($q, dataVocabularyWord1);

    return model;
};

angular.module('mock.vocabularyWord', []).service('VocabularyWord', mockVocabularyWord);
