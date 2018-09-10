vireo.repo("AdvisorSubmissionRepo", function AdvisorSubmissionRepo($q, AdvisorSubmission, WsApi) {

    var advisorSubmissionRepo = this;

    advisorSubmissionRepo.fetchSubmissionByHash = function (hash) {
        return $q(function(resolve, reject) {
            angular.extend(advisorSubmissionRepo.mapping.getByHash, {
                'method': 'advisor-review/' + hash
            });
            var fetchPromise = WsApi.fetch(advisorSubmissionRepo.mapping.getByHash);
            fetchPromise.then(function (res) {
                var apiRes = angular.fromJson(res.body);
                if (apiRes.meta.status === "SUCCESS") {
                    resolve(new AdvisorSubmission(apiRes.payload.Submission));
                } else {
                    reject();
                }
            });
        });
    };

    return advisorSubmissionRepo;

});
