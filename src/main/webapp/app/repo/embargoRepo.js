vireo.repo("EmbargoRepo", function EmbargoRepo($q, WsApi) {

    var embargoRepo = this;

    this.activate = function(model) {
        model.updateRequested = true;
        model.dirty(true);

        angular.extend(this.mapping.activate, {'method': 'activate/' + model.id});

        var promise = WsApi.fetch(this.mapping.activate);
        promise.then(function(res) {
            var message = angular.fromJson(res.body);
            if (message.meta.status === "INVALID") {
                angular.extend(embargoRepo, message.payload);
            }
        });

        return promise;
    };

    this.deactivate = function(model) {
        model.updateRequested = true;
        model.dirty(true);

        angular.extend(this.mapping.deactivate, {'method': 'deactivate/' + model.id});

        var promise = WsApi.fetch(this.mapping.deactivate);
        promise.then(function(res) {
            var message = angular.fromJson(res.body);
            if (message.meta.status === "INVALID") {
                angular.extend(embargoRepo, message.payload);
            }
        });

        return promise;
    };

    this.sort = function(guarantor, facet) {
        embargoRepo.clearValidationResults();
        angular.extend(this.mapping.sort, {'method': 'sort/' + guarantor +'/'+ facet});

        var promise = WsApi.fetch(this.mapping.sort);
        promise.then(function(res) {
            if(angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(embargoRepo, angular.fromJson(res.body).payload);
            }
        });

        return promise;
    };

    this.reorder = function(guarantor, src, dest) {
        embargoRepo.clearValidationResults();
        angular.extend(this.mapping.reorder, {'method': 'reorder/' + guarantor +'/'+ src + '/' + dest});

        var promise = WsApi.fetch(this.mapping.reorder);
        promise.then(function(res) {
            if(angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(embargoRepo, angular.fromJson(res.body).payload);
            }
        });

        return promise;
    };

    return this;

});
