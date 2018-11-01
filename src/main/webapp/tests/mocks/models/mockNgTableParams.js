var mockNgTableParams1 = {
};

var mockNgTableParams2 = {
};

var mockNgTableParams3 = {
};

angular.module('mock.ngTableParams', []).service('NgTableParams', function ($q) {
    return function () {
        var model = this;
        var defer;
        var payloadResponse = function (payload) {
            return defer.resolve({
                body: angular.toJson({
                    meta: {
                        status: 'SUCCESS'
                    },
                    payload: payload
                })
            });
        };

        this.isDirty = false;

        this.mock = function(toMock) {
        };

        this.delete = function() {
            defer = $q.defer();
            payloadResponse();
            return defer.promise;
        };

        this.dirty = function(boolean) {
                this.isDirty = boolean;
        };

        this.reload = function() {
        };

        this.save = function() {
            defer = $q.defer();
            payloadResponse();
            return defer.promise;
        };

        this.clearValidationResults = function () {
        };

        return this;
    };
});
