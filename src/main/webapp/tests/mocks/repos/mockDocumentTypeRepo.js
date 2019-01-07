var mockDocumentTypeRepo1 = [
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

var mockDocumentTypeRepo2 = [
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

var mockDocumentTypeRepo3 = [
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

angular.module('mock.documentTypeRepo', []).service('DocumentTypeRepo', function($q) {
    var repo = mockRepo('DocumentTypeRepo', $q, mockDocumentType, mockDocumentTypeRepo1);

    return repo;
});
