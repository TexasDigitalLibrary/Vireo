var dataDocumentType1 = {
    id: 1
};

var dataDocumentType2 = {
    id: 2
};

var dataDocumentType3 = {
    id: 3
};

var mockDocumentType = function($q) {
    var model = mockModel($q, dataDocumentType1);

    return model;
};

angular.module('mock.documentType', []).service('DocumentType', mockDocumentType);

