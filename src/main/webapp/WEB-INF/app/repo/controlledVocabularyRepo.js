vireo.repo("ControlledVocabularyRepo", function ControlledVocabularyRepo(FileService, RestApi, WsApi) {

    var controlledVocabularyRepo = this;

    // additional repo methods and variables

    this.downloadCSV = function (controlledVocabulary) {
        controlledVocabularyRepo.clearValidationResults();
        angular.extend(this.mapping.downloadCSV, {
            'method': 'export/' + controlledVocabulary
        });
        var promise = WsApi.fetch(this.mapping.downloadCSV);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(controlledVocabularyRepo, angular.fromJson(res.body).payload);
                console.log(controlledVocabularyRepo);
            }
        });
        return promise;
    };

    this.uploadCSV = function (controlledVocabulary) {
        controlledVocabularyRepo.clearValidationResults();
        angular.extend(this.mapping.uploadCSV, {
            'method': 'import/' + controlledVocabulary
        });
        var promise = WsApi.fetch(this.mapping.uploadCSV);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(controlledVocabularyRepo, angular.fromJson(res.body).payload);
                console.log(controlledVocabularyRepo);
            }
        });
        return promise;
    };

    this.confirmCSV = function (file, controlledVocabulary) {
        controlledVocabularyRepo.clearValidationResults();
        angular.extend(this.mapping.confirmCSV, {
            method: 'compare/' + controlledVocabulary,
            file: file
        });
        var promise = FileService.upload(this.mapping.confirmCSV);
        promise.then(function (res) {
            if (res.data.meta.status === "INVALID") {
                angular.extend(controlledVocabularyRepo, res.data.payload);
            }
        });
        return promise;
    };

    this.cancel = function (controlledVocabulary) {
        controlledVocabularyRepo.clearValidationResults();
        angular.extend(this.mapping.cancel, {
            'method': 'cancel/' + controlledVocabulary
        });
        var promise = WsApi.fetch(this.mapping.cancel);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(controlledVocabularyRepo, angular.fromJson(res.body).payload);
            }
        });
        return promise;
    };

    this.addVocabularyWord = function (cv, vw) {
        angular.extend(this.mapping.addVocabularyWord, {
            'method': 'add-vocabulary-word/' + cv.id,
            'data': vw
        });
        var promise = WsApi.fetch(this.mapping.addVocabularyWord);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(controlledVocabularyRepo, angular.fromJson(res.body).payload);
            }
        });
        return promise;
    };

    this.removeVocabularyWord = function (cv, vw) {
        angular.extend(this.mapping.removeVocabularyWord, {
            'method': 'remove-vocabulary-word/' + cv.id + "/" + vw.id
        });
        var promise = WsApi.fetch(this.mapping.removeVocabularyWord);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(controlledVocabularyRepo, angular.fromJson(res.body).payload);
            }
        });
        return promise;
    };

    this.updateVocabularyWord = function (cv, vw) {
        angular.extend(this.mapping.updateVocabularyWord, {
            'method': 'update-vocabulary-word/' + cv.id,
            'data': vw
        });
        var promise = WsApi.fetch(this.mapping.updateVocabularyWord);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(controlledVocabularyRepo, angular.fromJson(res.body).payload);
            }
        });
        return promise;
    };

    this.status = function (controlledVocabulary) {
        controlledVocabularyRepo.clearValidationResults();
        angular.extend(this.mapping.status, {
            'method': 'status/' + controlledVocabulary
        });
        var promise = WsApi.fetch(this.mapping.status);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(controlledVocabularyRepo, angular.fromJson(res.body).payload);
            }
        });
        return promise;
    };

    return this;

});
