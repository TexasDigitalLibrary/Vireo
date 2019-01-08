var mockVocabularyWord1 = {
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

var mockVocabularyWord2 = {
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

var mockVocabularyWord3 = {
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

var mockVocabularyWord = function($q) {
    var model = mockModel($q, mockVocabularyWord1);

    return model;
};

angular.module('mock.vocabularyWord', []).service('VocabularyWord', mockVocabularyWord);
