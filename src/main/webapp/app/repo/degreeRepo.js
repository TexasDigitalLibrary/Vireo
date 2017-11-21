vireo.repo("DegreeRepo", function DegreeRepo(WsApi) {

    var degreeRepo = this;

    // additional repo methods and variables

    this.getProquestDegreeCodes = function () {
        degreeRepo.clearValidationResults();
        var promise = WsApi.fetch(this.mapping.proquest);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(degreeRepo, angular.fromJson(res.body).payload);
                console.log(degreeRepo);
            }
        });
        return promise;
    };

    return this;

});
