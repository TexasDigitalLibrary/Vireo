vireo.factory("AbstractAppModel", function AbstractAppModel($q, $timeout) {

    return function AbstractAppModel() {

        // additional app level model methods and variables

        this.enableBeforeMethods = function() {

            var model = this;
            var originalReadyPromise = model.ready();

            model.beforeMethodsBuffer = [];

            model.before = function(beforeMethod) {
                model.beforeMethodsBuffer.push(beforeMethod);
            };

            model.defer = $q.defer();

            model.ready = function() {
                return model.defer.promise;
            };

            originalReadyPromise.then(function() {

                angular.forEach(model.beforeMethodsBuffer, function(beforeMethod) {
                    beforeMethod();
                });

                model.defer.resolve();
            });
        };

        return this;
    }

});
