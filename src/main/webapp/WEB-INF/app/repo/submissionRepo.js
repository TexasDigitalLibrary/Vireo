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
                    submissionRepo.add(angular.fromJson(res.body).payload.Submission);
                    defer.resolve(submissionRepo.findById(id));
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

        angular.extend(submissionRepo.mapping.batchUpdateSubmissionState, {'data': submissionState});
        var promise = WsApi.fetch(submissionRepo.mapping.batchUpdateSubmissionState);

        return promise;

    };

    submissionRepo.batchPublish = function(depositLocationName) {

        angular.extend(submissionRepo.mapping.batchPublish, {
            method: "batch-publish/" + depositLocationName
        });

        var promise = WsApi.fetch(submissionRepo.mapping.batchPublish);

        return promise;
    };

    submissionRepo.batchAssignTo = function(assignee) {

        angular.extend(submissionRepo.mapping.batchAssignTo, {'data': assignee});
        var promise = WsApi.fetch(submissionRepo.mapping.batchAssignTo);

        return promise;

    };

    return submissionRepo;

});
