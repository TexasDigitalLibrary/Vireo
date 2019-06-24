vireo.repo("DegreeRepo", function DegreeRepo(WsApi) {

    var degreeRepo = this;

    // additional repo methods and variables

    degreeRepo.getProquestDegreeCodes = function () {
        degreeRepo.clearValidationResults();
        var promise = WsApi.fetch(degreeRepo.mapping.proquest);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(degreeRepo, angular.fromJson(res.body).payload);
                console.log(degreeRepo);
            }
        });
        return promise;
    };

    degreeRepo.removeAll = function () {
        var promise = WsApi.fetch(degreeRepo.mapping.removeAll);
        promise.then(function (res) {
            console.log("Removed all degrees");
        });
        return promise;
    };

    return degreeRepo;

});
