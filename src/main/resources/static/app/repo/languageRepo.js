vireo.repo("LanguageRepo", function LanguageRepo(WsApi) {

    var languageRepo = this;

    // additional repo methods and variables

    this.getProquestLanguageCodes = function () {
        languageRepo.clearValidationResults();
        var promise = WsApi.fetch(this.mapping.proquest);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(languageRepo, angular.fromJson(res.body).payload);
                console.log(languageRepo);
            }
        });
        return promise;
    };

    return this;

});
