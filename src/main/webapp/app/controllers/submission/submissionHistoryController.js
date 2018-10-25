vireo.controller('SubmissionHistoryController', function ($controller, $location, $scope, $timeout, NgTableParams, StudentSubmissionRepo, SubmissionStatuses) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.SubmissionStatuses = SubmissionStatuses;

    $scope.submissionToDelete = {};

    $scope.studentsSubmissions = StudentSubmissionRepo.getAll();

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

    StudentSubmissionRepo.ready().then(function () {
        resetTable();
    });

    StudentSubmissionRepo.listenForChanges().then(null, null, function() {
        StudentSubmissionRepo.reset().then(function() {
            resetTable();
        });
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

    $scope.getManuscriptFileName = function (row) {
        var fileName = null;
        for (var i in row.fieldValues) {
            var fv = row.fieldValues[i];
            if (fv.fieldPredicate.value === '_doctype_primary') {
                fileName = fv.fileInfo ? fv.fileInfo.name : null;
                break;
            }
        }
        return fileName;
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
