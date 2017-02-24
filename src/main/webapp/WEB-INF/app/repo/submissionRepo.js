vireo.repo("SubmissionRepo", function SubmissionRepo($q, WsApi, Submission) {

    var submissionRepo = this;

    // additional repo methods and variables

    submissionRepo.findSubmissionById = function(id) {

        var submission = submissionRepo.findById(id);

        var defer = $q.defer();

        if (!submission) {
            submissionRepo.clearValidationResults();
            angular.extend(submissionRepo.mapping.one, {
                'method': 'get-one/' + id
            });
            var fetchPromise = WsApi.fetch(submissionRepo.mapping.one);
            fetchPromise.then(function(res) {

                if (angular.fromJson(res.body).meta.type !== "ERROR") {
                    // angular.extend(submissionRepo.list, angular.fromJson(res.body).payload);
                    submission = new Submission(angular.fromJson(res.body).payload.Submission);
                    submissionRepo.add(submission);
                    defer.resolve(submission);
                }
            });
        } else {
            defer.resolve(submission);
        }

        return defer.promise;
    };

    submissionRepo.query = function(columns, page, size) {
        angular.extend(submissionRepo.mapping.query, {
            'method': 'query/' + page + '/' + size,
            'data': columns
        });
        var promise = WsApi.fetch(submissionRepo.mapping.query);
        promise.then(function(res) {
            if (angular.fromJson(res.body).meta.type !== "ERROR") {
                angular.extend(submissionRepo, angular.fromJson(res.body).payload);
            }
        });
        return promise;
    };

    submissionRepo.batchUpdateStatus = function(submissionState) {

        console.log(submissionState);

        angular.extend(submissionRepo.mapping.batchUpdateSubmissionState, {'data': submissionState});
        var promise = WsApi.fetch(submissionRepo.mapping.batchUpdateSubmissionState);

        return promise;

    };

    submissionRepo.batchAssignTo = function(assignee) {

        console.log(assignee);

        angular.extend(submissionRepo.mapping.batchAssignTo, {'data': assignee});
        var promise = WsApi.fetch(submissionRepo.mapping.batchAssignTo);

        return promise;

    };

    return submissionRepo;

});
