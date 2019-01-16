var dataNote1 = {
    id: 1
};

var dataNote2 = {
    id: 2
};

var dataNote3 = {
    id: 3
};

var dataNote4 = {
    id: 4
};

var dataNote5 = {
    id: 5
};

var dataNote6 = {
    id: 6
};

var mockNote = function($q) {
    var model = mockModel("Note", $q, dataNote1);

    return model;
};

angular.module('mock.note', []).service('Note', mockNote);

