vireo.repo("StudentSubmissionRepo", function StudentSubmissionRepo($q, StudentSubmission, WsApi) {

    var studentSubmissionRepo = this;

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

    return studentSubmissionRepo;

});
