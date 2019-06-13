var dataDocumentType1 = {
    id: 1
};

var dataDocumentType2 = {
    id: 2
};

var dataDocumentType3 = {
    id: 3
};

var dataDocumentType4 = {
    id: 4
};

var dataDocumentType5 = {
    id: 5
};

var dataDocumentType6 = {
    id: 6
};

var mockDocumentType = function($q) {
    var model = mockModel("DocumentType", $q, dataDocumentType1);

    return model;
};

angular.module("mock.documentType", []).service("DocumentType", mockDocumentType);

