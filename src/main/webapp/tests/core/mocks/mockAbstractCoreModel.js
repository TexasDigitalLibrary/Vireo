var mockModel = function ($q, mockDataObj) {
    var model = {};
    var combinationOperation = "";

    model.isDirty = false;
    model.isValid = false;

    model.mock = function(toMock) {
        if (typeof toMock === "object") {
            var keys = Object.keys(toMock);
            for (var i in keys) {
                model[keys[i]] = toMock[keys[i]];
            }
        }
        else if (toMock === undefined || toMock === null) {
            model = null;
        }
    };

    model.mock(mockDataObj);

    model.acceptPendingUpdate = function () {
    };

    model.acceptPendingDelete = function () {
    };

    model.before = function () {
    };

    model.clearListens = function() {
        var payload = {};
        // TODO
        return payloadPromise($q.defer(), payload);
    };

    model.clearValidationResults = function () {
    };

    model.delete = function() {
        return payloadPromise($q.defer(), true);
    };

    model.dirty = function(boolean) {
        if (boolean !== undefined) {
            model.isDirty = boolean;
        }

        return model.isDirty;
    };

    model.enableMergeCombinationOperation = function () {
        combinationOperation = 'merge';
    };

    model.enableExtendCombinationOperation = function () {
        combinationOperation = 'extend';
    };

    model.fetch = function() {
        return payloadPromise($q.defer(), mockDataObj);
    };

    model.getCombinationOperation = function () {
        return combinationOperation;
    };

    model.getEntityName = function () {
        return "";
    };

    model.getValidations = function () {
        return null;
    };

    model.init = function (data, apiMapping) {
    };

    model.listen = function() {
    };

    model.refresh = function() {
    };

    model.reload = function() {
    };

    model.save = function() {
        return payloadPromise($q.defer(), true);
    };

    model._syncShadow = function() {
    };


    return model;
};
