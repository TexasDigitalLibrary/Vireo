vireo.controller("OrganizationStepsController", function ($controller, $scope, $q, OrganizationRepo) {

  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  var reorder = function(originIdx, destinationIdx){
    console.info('entered reorder block with ', originIdx, destinationIdx);
    if (!(originIdx == 0 && destinationIdx == -1) && !(originIdx == $scope.selectedOrganization.workflowStepOrder.length-1 && destinationIdx == $scope.selectedOrganization.workflowStepOrder.length)) {
      var tmp = $scope.selectedOrganization.workflowStepOrder[originIdx];
      $scope.selectedOrganization.workflowStepOrder[originIdx] = $scope.selectedOrganization.workflowStepOrder[destinationIdx];
      $scope.selectedOrganization.workflowStepOrder[destinationIdx] = tmp;
    }
    OrganizationRepo.update($scope.selectedOrganization);
  };

  var stepForId = function(id){
    for(var i = 0; i < $scope.steps.length; i++) {
      if ($scope.steps[i].id == id) {
        return $scope.steps[i];
      }
    }
  }
  $scope.foo = [{name:'myname'}];

  $scope.ready.then(function() {
    console.info($scope.selectedOrganization);


    $scope.reorderUp = function(originIdx) {
      reorder(originIdx, originIdx-1);
    };

    $scope.reorderDown = function(originIdx) {
      console.info('downclick');
      reorder(originIdx, originIdx+1);
    };

    $scope.resetOrganizationSteps = function() {
      $scope.modalData = {'name':''};
    };

    $scope.resetOrganizationSteps();
    
    $scope.selectedOrganizationSteps = function() {
      var steps = [];
      $scope.resetOrganizationSteps();
      $scope.selectedOrganization.workflowSteps.forEach(function(element, idx, array){
        OrganizationRepo.getStepForId(element).then(function(workflowStep){
          console.info(workflowStep);
        });
      });
      
      console.info(steps);
      // $scope.modalData = $scope.organizationSteps.list[index];
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
      });
    };

    $scope.printState = function() {
      console.info($scope.selectedOrganization);
      $scope.selectedOrganizationSteps();
    };

  });
});
