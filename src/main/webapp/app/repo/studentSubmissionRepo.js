vireo.repo("StudentSubmissionRepo", function StudentSubmissionRepo($q, ApiResponseActions, StudentSubmission, WsApi) {

    var studentSubmissionRepo = this;

    var defer = $q.defer();

    studentSubmissionRepo.fetchSubmissionById = function (id) {
        return $q(function(resolve, reject) {
            angular.extend(studentSubmissionRepo.mapping.one, {
                'method': 'get-one/' + id
            });
            var fetchPromise = WsApi.fetch(studentSubmissionRepo.mapping.one);
            fetchPromise.then(function (res) {
                var apiRes = angular.fromJson(res.body);
                if (apiRes.meta.status === "SUCCESS") {
                    resolve(new StudentSubmission(apiRes.payload.Submission));
                } else {
                    reject();
                }
            });
        });
    };

    studentSubmissionRepo.findPaginatedActionLogsById = function (id, orderBy, page, count) {
        return $q(function(resolve, reject) {
            angular.extend(studentSubmissionRepo.mapping.one, {
                'method': 'get-one/' + id + '/action-logs',
                'query': {
                  'orderBy': orderBy,
                  'page': page,
                  'size': count,
                }
            });

            var fetchPromise = WsApi.fetch(studentSubmissionRepo.mapping.one);

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

    studentSubmissionRepo.listenForChanges = function() {
        return defer.promise;
    };

    WsApi.listen('/private/queue/submissions').then(null, null, function(msg) {
        var apiRes = angular.fromJson(msg.body);
        if(apiRes.meta.status === 'SUCCESS') {
            if(apiRes.meta.action === ApiResponseActions.CREATE) {
                studentSubmissionRepo.add(apiRes.payload.Submission);
            } else if(apiRes.meta.action === ApiResponseActions.DELETE) {
                studentSubmissionRepo.remove(apiRes.payload.Submission);
            }
            defer.notify();
        }
    });

    return studentSubmissionRepo;

});
