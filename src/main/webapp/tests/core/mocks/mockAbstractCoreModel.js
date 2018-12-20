var mockModel = function ($q, mockDataObj) {
    var model = {};

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

    model.clearValidationResults = function () {

    };

    model.delete = function() {
        return payloadPromise($q.defer(), true);
    };

    model.dirty = function(boolean) {
        model.isDirty = boolean;
    };

    model.reload = function() {

    };

    model.save = function() {
        return payloadPromise($q.defer(), true);
    };

    return model;
};
