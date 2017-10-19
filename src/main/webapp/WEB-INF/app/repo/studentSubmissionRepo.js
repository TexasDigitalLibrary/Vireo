vireo.repo("StudentSubmissionRepo", function StudentSubmissionRepo($q, WsApi) {

    var studentSubmissionRepo = this;

    studentSubmissionRepo.findSubmissionById = function (id) {
        var submission = studentSubmissionRepo.findById(id);
        var defer = $q.defer();
        if (!submission) {
            studentSubmissionRepo.clearValidationResults();
            angular.extend(studentSubmissionRepo.mapping.one, {
                'method': 'get-one/' + id
            });
            var fetchPromise = WsApi.fetch(studentSubmissionRepo.mapping.one);
            fetchPromise.then(function(res) {
                var resObj = angular.fromJson(res.body);
                if (resObj.meta.status !== "ERROR") {
                    studentSubmissionRepo.add(resObj.payload.Submission);
                    defer.resolve(studentSubmissionRepo.findById(id));
                }
            });
        } else {
            defer.resolve(submission);
        }
        return defer.promise;
    };

    return studentSubmissionRepo;

});
