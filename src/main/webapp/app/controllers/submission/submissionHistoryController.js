vireo.controller('SubmissionHistoryController', function ($controller, $location, $scope, $timeout, NgTableParams, StudentSubmissionRepo, SubmissionStatuses, ManagedConfigurationRepo) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    console.log('SubmissionHistoryController');

    $scope.SubmissionStatuses = SubmissionStatuses;

    $scope.submissionToDelete = {};

    $scope.studentsSubmissions = StudentSubmissionRepo.getAll();

    $scope.configuration = ManagedConfigurationRepo.getAll();

    var buildTable = function () {
        return new NgTableParams({}, {
            counts: [],
            filterDelay: 0,
            dataset: $scope.studentsSubmissions
        });
    };

    var resetTable = function() {
        $scope.tableParams = buildTable();
        $scope.tableParams.reload();
    };

    var manuscriptFetchLock = {};

    var handleResponse = function (res) {
        if (!!res && !!res.body) {
            var apiRes = angular.fromJson(res.body);
            if (apiRes.meta.status === "SUCCESS") {
                resetTable();
            }
        }
    };

    StudentSubmissionRepo.ready().then(function () {
        angular.forEach($scope.studentsSubmissions, function (submission) {
            if (!!submission) {
                submission.enableListeners(true);

                if (!!submission.fieldValuesListenPromise) {
                    submission.fieldValuesListenPromise.then(null, null, handleResponse);
                }

                if (!!submission.fieldValuesRemovedListenPromise) {
                    submission.fieldValuesRemovedListenPromise.then(null, null, handleResponse);
                }
            }
        });

        resetTable();
    });

    StudentSubmissionRepo.listenForChanges().then(null, null, function(res) {
        StudentSubmissionRepo.reset().then(handleResponse);
    });

    $scope.getDocumentTitle = function (row) {
        var title = null;
        for (var i in row.fieldValues) {
            var fv = row.fieldValues[i];
            if (fv.fieldPredicate.value === 'dc.title') {
                title = fv.value;
                break;
            }
        }
        return title;
    };

    $scope.getManuscriptFileName = function (row, defaultString) {
        for (var i in row.fieldValues) {
            var fv = row.fieldValues[i];
            if (fv.fieldPredicate.value === '_doctype_primary') {
                if (!fv.fileInfo) {
                    if (!!manuscriptFetchLock[fv.id]) {
                        return defaultString;
                    }

                    manuscriptFetchLock[fv.id] = true;

                    row.fileInfo(fv).then(function (response) {
                        fv.fileInfo = angular.fromJson(response.body).payload.ObjectNode;

                        manuscriptFetchLock[fv.id] = undefined;
                    }).catch (function (response) {
                        if (!!response) console.error(response);

                        manuscriptFetchLock[fv.id] = undefined;
                    });

                    break;
                }

                return fv.fileInfo.name;
            }
        }

        return defaultString;
    };

    $scope.startNewSubmission = function (path) {
        $scope.closeModal();
        $timeout(function () {
            $location.path(path);
        }, 250);
    };

    $scope.confirmDelete = function (submission) {
        $scope.openModal('#confirmDeleteSubmission');
        $scope.submissionToDelete = submission;
    };

    $scope.deleteSubmission = function () {
        $scope.deleting = true;
        $scope.submissionToDelete.delete().then(function () {
            $scope.closeModal();
            $scope.deleting = false;
            StudentSubmissionRepo.remove($scope.submissionToDelete);
            $scope.submissionToDelete = {};
            $scope.tableParams.reload();
        });
    };

});
