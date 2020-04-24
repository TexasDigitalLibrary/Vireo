vireo.repo("SubmissionRepo", function SubmissionRepo($q, FileService, Submission, WsApi) {

    var submissionRepo = this;

    // additional repo methods and variables

    submissionRepo.fetchSubmissionById = function (id) {
        return $q(function(resolve, reject) {
            angular.extend(submissionRepo.mapping.one, {
                'method': 'get-one/' + id
            });
            var fetchPromise = WsApi.fetch(submissionRepo.mapping.one);
            fetchPromise.then(function (res) {
                var apiRes = angular.fromJson(res.body);
                if (apiRes.meta.status === "SUCCESS") {
                    resolve(new Submission(apiRes.payload.Submission));
                } else {
                    reject("A submission with the ID " + id + " does not exist.");
                }
            });
        });
    };

    submissionRepo.query = function (columns, page, size) {
        angular.extend(submissionRepo.mapping.query, {
            'method': 'query/' + page + '/' + size,
            'data': columns
        });
        var promise = WsApi.fetch(submissionRepo.mapping.query);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "SUCCESS") {
                angular.extend(submissionRepo, angular.fromJson(res.body).payload);
            }
        });
        return promise;
    };

    submissionRepo.batchExport = function (packager, filterId) {
        angular.extend(submissionRepo.mapping.batchExport, {
            'method': 'batch-export/' + packager.name + (filterId ? '/' + filterId : '')
        });
        var promise = FileService.download(submissionRepo.mapping.batchExport);
        return promise;
    };

    submissionRepo.batchUpdateStatus = function (submissionStatus) {
        angular.extend(submissionRepo.mapping.batchUpdateSubmissionStatus, {
            method: "batch-update-status/" + submissionStatus.name
        });
        var promise = WsApi.fetch(submissionRepo.mapping.batchUpdateSubmissionStatus);
        return promise;
    };

    submissionRepo.batchPublish = function (depositLocation) {
        angular.extend(submissionRepo.mapping.batchPublish, {
            method: "batch-publish/" + depositLocation.id
        });
        var promise = WsApi.fetch(submissionRepo.mapping.batchPublish);
        return promise;
    };

    submissionRepo.batchAssignTo = function (assignee) {
        angular.extend(submissionRepo.mapping.batchAssignTo, {
            'data': assignee
        });
        var promise = WsApi.fetch(submissionRepo.mapping.batchAssignTo);
        return promise;
    };

    return submissionRepo;

});
