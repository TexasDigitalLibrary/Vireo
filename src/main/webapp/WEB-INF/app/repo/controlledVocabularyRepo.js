vireo.repo("ControlledVocabularyRepo", function ControlledVocabularyRepo(RestApi, WsApi) {

    var controlledVocabularyRepo = this;

    // additional repo methods and variables

    this.change = WsApi.listen(this.mapping.change);

    this.downloadCSV = function(controlledVocabulary) {
        controlledVocabularyRepo.clearValidationResults();
        angular.extend(this.mapping.downloadCSV, {
            'method': 'export/' + controlledVocabulary
        });
        var promise = WsApi.fetch(this.mapping.downloadCSV);
        promise.then(function(res) {
            if (angular.fromJson(res.body).meta.type == "INVALID") {
                angular.extend(controlledVocabularyRepo, angular.fromJson(res.body).payload);
                console.log(controlledVocabularyRepo);
            }
        });
        return promise;
    };

    this.uploadCSV = function(controlledVocabulary) {
        controlledVocabularyRepo.clearValidationResults();
        angular.extend(this.mapping.uploadCSV, {
            'method': 'import/' + controlledVocabulary
        });
        var promise = WsApi.fetch(this.mapping.uploadCSV);
        promise.then(function(res) {
            if (angular.fromJson(res.body).meta.type == "INVALID") {
                angular.extend(controlledVocabularyRepo, angular.fromJson(res.body).payload);
                console.log(controlledVocabularyRepo);
            }
        });
        return promise;
    };

    this.confirmCSV = function(file, controlledVocabulary) {
        controlledVocabularyRepo.clearValidationResults();
        angular.extend(this.mapping.confirmCSV, {
            'method': 'compare/' + controlledVocabulary,
            'file': file
        });
        var promise = RestApi.post(this.mapping.confirmCSV);
        promise.then(function(res) {
            if (res.meta.type == "INVALID") {
                angular.extend(controlledVocabularyRepo, angular.fromJson(res.body).payload);
                console.log(controlledVocabularyRepo);
            }
        });
        return promise;
    };

    this.cancel = function(controlledVocabulary) {
        controlledVocabularyRepo.clearValidationResults();
        angular.extend(this.mapping.cancel, {
            'method': 'cancel/' + controlledVocabulary
        });
        var promise = WsApi.fetch(this.mapping.cancel);
        promise.then(function(res) {
            if (angular.fromJson(res.body).meta.type == "INVALID") {
                angular.extend(controlledVocabularyRepo, angular.fromJson(res.body).payload);
                console.log(controlledVocabularyRepo);
            }
        });
        return promise;
    };

    this.status = function(controlledVocabulary) {
        controlledVocabularyRepo.clearValidationResults();
        angular.extend(this.mapping.status, {
            'method': 'status/' + controlledVocabulary
        });
        var promise = WsApi.fetch(this.mapping.status);
        promise.then(function(res) {
            if (angular.fromJson(res.body).meta.type == "INVALID") {
                angular.extend(controlledVocabularyRepo, angular.fromJson(res.body).payload);
                console.log(controlledVocabularyRepo);
            }
        });
        return promise;
    };

    return this;

});
