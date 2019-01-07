var mockNote1 = {
    id: 1
};

var mockNote2 = {
    id: 2
};

var mockNote3 = {
    id: 3
};

var mockNote = function($q) {
    var model = mockModel($q, mockNote1);

    return model;
};

angular.module('mock.note', []).service('Note', mockNote);

