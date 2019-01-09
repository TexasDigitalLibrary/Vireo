var dataNoteRepo1 = [
    dataNote1,
    dataNote2,
    dataNote3
];

var dataNoteRepo2 = [
    dataNote3,
    dataNote2,
    dataNote1
];

var dataNoteRepo3 = [
    dataNote4,
    dataNote5,
    dataNote6
];

angular.module('mock.noteRepo', []).service('NoteRepo', function($q) {
    var repo = mockRepo('NoteRepo', $q, mockNote, dataNoteRepo1);

    return repo;
});
