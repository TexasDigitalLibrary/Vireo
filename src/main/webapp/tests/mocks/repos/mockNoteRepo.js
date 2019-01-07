var mockNoteRepo1 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

var mockNoteRepo2 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

var mockNoteRepo3 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

angular.module('mock.noteRepo', []).service('NoteRepo', function($q) {
    var repo = mockRepo('NoteRepo', $q, mockNote, mockNoteRepo1);

    return repo;
});
