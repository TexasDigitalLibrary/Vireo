var dataNote1 = {
    id: 1
};

var dataNote2 = {
    id: 2
};

var dataNote3 = {
    id: 3
};

var mockNote = function($q) {
    var model = mockModel($q, dataNote1);

    return model;
};

angular.module('mock.note', []).service('Note', mockNote);

