vireo.repo("AdvisorSubmissionRepo", function AdvisorSubmissionRepo($q, AdvisorSubmission, WsApi) {

    var advisorSubmissionRepo = this;

    advisorSubmissionRepo.findSubmissionByhash = function (hash) {
        var defer = $q.defer();
        advisorSubmissionRepo.clearValidationResults();
        angular.extend(advisorSubmissionRepo.mapping.getByHash, {
            'method': 'advisor-review/' + hash
        });
        var fetchPromise = WsApi.fetch(advisorSubmissionRepo.mapping.getByHash);
        fetchPromise.then(function (res) {
            var resObj = angular.fromJson(res.body);
            if (resObj.meta.status !== "ERROR") {
                var submission = resObj.payload.Submission;
                advisorSubmissionRepo.add(submission);
                defer.resolve(advisorSubmissionRepo.findById(submission.id));
            }
        });
        return defer.promise;
    };

    return advisorSubmissionRepo;

});
