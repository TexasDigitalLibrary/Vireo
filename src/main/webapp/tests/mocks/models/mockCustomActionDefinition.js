var mockCustomActionDefinition1 = {
    'id': 1
};

var mockCustomActionDefinition2 = {
    'id': 2
};

var mockCustomActionDefinition3 = {
    'id': 3
};

var mockCustomActionDefinition = function($q) {
    var model = mockModel($q, mockCustomActionDefinition1);

    return model;
};

angular.module('mock.customActionDefinition', []).service('CustomActionDefinition', mockCustomActionDefinition);

