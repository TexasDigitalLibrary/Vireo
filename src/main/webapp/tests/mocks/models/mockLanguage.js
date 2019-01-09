var dataLanguage1 = {
    id: 1
};

var dataLanguage2 = {
    id: 2
};

var dataLanguage3 = {
    id: 3
};

var mockLanguage = function($q) {
    var model = mockModel($q, dataLanguage1);

    return model;
};

angular.module('mock.language', []).service('Language', mockLanguage);

