var mockLanguage1 = {
    id: 1
};

var mockLanguage2 = {
    id: 2
};

var mockLanguage3 = {
    id: 3
};

var mockLanguage = function($q) {
    var model = mockModel($q, mockLanguage1);

    return model;
};

angular.module('mock.language', []).service('Language', mockLanguage);

