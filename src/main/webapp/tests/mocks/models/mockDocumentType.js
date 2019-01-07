var mockDocumentType1 = {
    id: 1
};

var mockDocumentType2 = {
    id: 2
};

var mockDocumentType3 = {
    id: 3
};

var mockDocumentType = function($q) {
    var model = mockModel($q, mockDocumentType1);

    return model;
};

angular.module('mock.documentType', []).service('DocumentType', mockDocumentType);

