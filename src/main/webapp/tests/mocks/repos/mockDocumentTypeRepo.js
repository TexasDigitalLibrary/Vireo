var dataDocumentTypeRepo1 = [
    dataDocumentType1,
    dataDocumentType2,
    dataDocumentType3
];

var dataDocumentTypeRepo2 = [
    dataDocumentType3,
    dataDocumentType2,
    dataDocumentType1
];

var dataDocumentTypeRepo3 = [
    dataDocumentType4,
    dataDocumentType5,
    dataDocumentType6
];

angular.module("mock.documentTypeRepo", []).service("DocumentTypeRepo", function($q) {
    var repo = mockRepo("DocumentTypeRepo", $q, mockDocumentType, dataDocumentTypeRepo1);

    return repo;
});
