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
