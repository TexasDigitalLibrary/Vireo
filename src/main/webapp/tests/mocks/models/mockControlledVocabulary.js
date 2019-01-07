var mockControlledVocabulary1 = {
    id: 1
};

var mockControlledVocabulary2 = {
    id: 2
};

var mockControlledVocabulary3 = {
    id: 3
};

var mockControlledVocabulary = function($q) {
    var model = mockModel($q, mockControlledVocabulary1);

    model.clearListens = function() {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    model.listen = function() {
        // TODO
    };

    model._syncShadow = function() {
        // TODO
    };

    return model;
};

angular.module('mock.controlledVocabulary', []).service('ControlledVocabulary', mockControlledVocabulary);

