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

    advisorSubmissionRepo.findPaginatedActionLogsByHash = function (hash, orderBy, page, count) {
        return $q(function(resolve, reject) {
            angular.extend(advisorSubmissionRepo.mapping.getByHash, {
                'method': 'advisor-review/' + hash + '/action-logs',
                'query': {
                  'orderBy': orderBy,
                  'page': page,
                  'size': count,
                }
            });

            var fetchPromise = WsApi.fetch(advisorSubmissionRepo.mapping.getByHash);

            fetchPromise.then(function (res) {
                if (angular.isDefined(res) && angular.isDefined(res.body)) {
                    var apiRes = angular.fromJson(res.body);

                    if (angular.isDefined(apiRes) && angular.isDefined(apiRes.meta)) {
                        if (apiRes.meta.status === "SUCCESS" && angular.isDefined(apiRes.payload.PageImpl)) {
                            resolve(apiRes.payload.PageImpl);
                            return;
                        }
                    }
                }

                reject();
            });
        });
    };

    return advisorSubmissionRepo;

});
