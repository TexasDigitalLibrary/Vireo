// provide common helper methods to be used by mocks.

var messagePromise = function (defer, message, messageStatus, httpStatus) {
     defer.resolve({
        body: angular.toJson({
            meta: {
                status: messageStatus ? messageStatus : "SUCCESS",
                message: message
            },
            status: httpStatus ? httpStatus : 200
        })
    });
    return defer.promise;
};

var valuePromise = function (defer, model, type, timeout) {
    if (type === "reject") {
        defer.reject(model);
    } else if (type === "notify") {
        timeout(function () {
            defer.notify(model);
        }, 0);
    } else {
        defer.resolve(model);
    }

    return defer.promise;
};

var payloadPromise = function (defer, payload, messageStatus, httpStatus) {
    defer.resolve({
        body: angular.toJson({
            meta: {
                status: messageStatus ? messageStatus : "SUCCESS",
            },
            payload: payload,
            status: httpStatus ? httpStatus : 200
        })
    });
    return defer.promise;
};

var dataPromise = function (defer, payload, messageStatus, httpStatus) {
    defer.resolve({
        data: {
            meta: {
                status: messageStatus ? messageStatus : "SUCCESS",
            },
            payload: payload,
            status: httpStatus ? httpStatus : 200
        }
    });
    return defer.promise;
};

var rejectPromise = function (defer, payload, messageStatus, httpStatus) {
    defer.reject({
        body: angular.toJson({
            meta: {
                status: messageStatus ? messageStatus : "INVALID",
            },
            payload: payload,
            status: httpStatus ? httpStatus : 200
        })
    });
    return defer.promise;
};

var failurePromise = function (defer, payload, messageStatus, httpStatus) {
    defer.reject({
        data: {
            meta: {
                status: messageStatus ? messageStatus : "INVALID",
            },
            payload: payload,
            status: httpStatus ? httpStatus : 500
        }
    });
    return defer.promise;
};

var notifyPromise = function (timeout, defer, payload, messageStatus, httpStatus) {
    timeout(function () {
        defer.notify({
            body: angular.toJson({
                meta: {
                    status: messageStatus ? messageStatus : "SUCCESS",
                },
                payload: payload,
                status: httpStatus ? httpStatus : 200
            })
        });
    }, 0);
    return defer.promise;
};

var mockParameterModel = function($q, mockModel) {
    return function(toMock) {
        var model = new mockModel($q);
        model.mock(toMock);
        return model;
    };
};

var mockParameterConstructor = function(mockConstructor) {
    return function() { return mockConstructor; };
};

var mockWindow = function() {
    return {
        location: {
            href: "",
            absUrl: "",
            replace: function() {}
        },
        open: () => {}
    };
};

var mockForms = function() {
    return {
        $pristine: true,
        $untouched: true,
        $setPristine: function (value) { this.$pristine = value; },
        $setUntouched: function (value) { this.$untouched = value; }
    };
};
