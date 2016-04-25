vireo.controller("WorkflowController", function ($controller, $scope, $q) { // TODO inject model

  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  //TODO get model data
  // $scope.workflow = WorkflowRepo.get();
  // $scope.ready = $q.all([WorkflowRepo.ready()]);

  $scope.ready.then(function() {

    $scope.resetWorkflow = function() {
      $scope.modalData = {'name':''};
    }

    $scope.resetWorkflow();
    
    $scope.selectWorkflow = function(index) {
      $scope.resetWorkflow();
      $scope.modalData = $scope.workflow.list[index];
    };

    $scope.createWorkflow = function() {
      WorkflowRepo.add($scope.modalData).then(function(){
      });
        $scope.resetWorkflow();
    };

    $scope.updateWorkflow = function() {
        WorkflowRepo.update($scope.modalData);
        $scope.resetWorkflow();
    };

    $scope.launchEditModal = function(workflow) {
        $scope.modalData = workflow;
        angular.element('#workflowEditModal').modal('show');
    };

    $scope.removeWorkflow = function(index) {
      WorkflowRepo.remove($scope.modalData).then(function(){
        $scope.resetWorkflow();
        console.info($scope.workflow);
      });
    };

  });
});
