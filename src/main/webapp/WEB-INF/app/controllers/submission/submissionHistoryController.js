vireo.controller('SubmissionHistoryController', function($controller, $location, $scope, $timeout, NgTableParams, StudentSubmissionRepo, SubmissionStatuses) {

  angular.extend(this, $controller('AbstractController', {
    $scope: $scope
  }));

  $scope.SubmissionStatuses = SubmissionStatuses;
  $scope.submissionToDelete = {};

  $scope.studentsSubmissions = StudentSubmissionRepo.getAll();
  console.log($scope.studentsSubmissions);

  var buildTable = function() {
    return new NgTableParams({}, {
      counts: [],
      filterDelay: 0,
      dataset: $scope.studentsSubmissions
    });
  };

  StudentSubmissionRepo.ready().then(function() {
    $scope.tableParams = buildTable();
    $scope.tableParams.reload();
  });

  StudentSubmissionRepo.listen(function() {
    $scope.tableParams.reload();
  });

  $scope.startNewSubmission = function(path) {
    $scope.closeModal();
    $timeout(function() {
      $location.path(path);
    }, 250);
  };

  $scope.confirmDelete = function(submission) {
    $scope.openModal('#confirmDeleteSubmission');
    $scope.submissionToDelete = submission;
  };

  $scope.deleteSubmission = function() {
    $scope.deleting = true;
    $scope.submissionToDelete.delete().then(function() {
      $scope.closeModal();
      $scope.deleting = false;
      StudentSubmissionRepo.remove($scope.submissionToDelete);
      $scope.submissionToDelete = {};
      $scope.tableParams.reload();
    });
  };

});
