vireo.controller("OrganizationStepsController", function ($controller, $scope, $q, OrganizationRepo) {

  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  var reorder = function(originIdx, destinationIdx){
    console.info($scope.selectedOrganization.workflowStepOrder);
    if (!(originIdx == 0 && destinationIdx == -1) && !(originIdx == $scope.selectedOrganization.workflowStepOrder.length-1 && destinationIdx == $scope.selectedOrganization.workflowStepOrder.length)) {
      var tmp = $scope.selectedOrganization.workflowStepOrder[originIdx];
      $scope.selectedOrganization.workflowStepOrder[originIdx] = $scope.selectedOrganization.workflowStepOrder[destinationIdx];
      $scope.selectedOrganization.workflowStepOrder[destinationIdx] = tmp;
    }
    console.info($scope.selectedOrganization.workflowStepOrder);
    OrganizationRepo.update($scope.selectedOrganization);
  };

  var stepForId = function(id){
    for(var i = 0; i < $scope.steps.length; i++) {
      if ($scope.steps[i].id == id) {
        return $scope.steps[i];
      }
    }
  }

  // var tempRefreshStepOrder = function(){
  //   var tmp = ['a', 'b', 'c'];
  //   for(var i=0; i<$scope.steps.length; ++i) {
  //     tmp[i] = stepForId($scope.stepOrder[i]);
  //   }
  //   $scope.steps = tmp;
  //   //TODO would save the organization (and thus the ordering) here; refresh from the broadcast.
  // };

  $scope.ready.then(function() {


    $scope.reorderUp = function(originIdx) {
      reorder(originIdx, originIdx-1);
    };

    $scope.reorderDown = function(originIdx) {
      reorder(originIdx, originIdx+1);
    };

    $scope.resetOrganizationSteps = function() {
      $scope.modalData = {'name':''};
    };

    $scope.resetOrganizationSteps();
    
    $scope.selectOrganizationSteps = function(index) {
      $scope.resetOrganizationSteps();
      $scope.modalData = $scope.organizationSteps.list[index];
    };

    $scope.createOrganizationSteps = function() {
      OrganizationStepsRepo.add($scope.modalData).then(function(){
      });
        $scope.resetOrganizationSteps();
    };

    $scope.updateOrganizationSteps = function() {
        OrganizationStepsRepo.update($scope.modalData);
        $scope.resetOrganizationSteps();
    };

    $scope.launchEditModal = function(organizationSteps) {
        $scope.modalData = organizationSteps;
        angular.element('#organizationStepsEditModal').modal('show');
    };

    $scope.removeOrganizationSteps = function(index) {
      OrganizationStepsRepo.remove($scope.modalData).then(function(){
        $scope.resetOrganizationSteps();
        console.info($scope.organizationSteps);
      });
    };

    $scope.printState = function() {
      console.info('parent org VVV');
      console.info($scope.selectedOrganization);
    };

  });
});
