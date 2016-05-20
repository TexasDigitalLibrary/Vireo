vireo.controller("WorkflowStepsController", function ($controller, $scope, $q, OrganizationRepo) {

    angular.extend(this, $controller("AbstractController", {$scope: $scope}));
  

  //$scope.workflowSteps = managedOrganization.workflowSteps;


  // var reorder = function(originIdx, destinationIdx){
  //   console.info('entered reorder block with ', originIdx, destinationIdx);
  //   if (!(originIdx == 0 && destinationIdx == -1) && !(originIdx == $scope.selectedOrganization.workflowStepOrder.length-1 && destinationIdx == $scope.selectedOrganization.workflowStepOrder.length)) {
  //     var tmp = $scope.selectedOrganization.workflowStepOrder[originIdx];
  //     $scope.selectedOrganization.workflowStepOrder[originIdx] = $scope.selectedOrganization.workflowStepOrder[destinationIdx];
  //     $scope.selectedOrganization.workflowStepOrder[destinationIdx] = tmp;
  //   }
  //   OrganizationRepo.update($scope.selectedOrganization);
  // };

  // var stepForId = function(id){
  //   for(var i = 0; i < $scope.steps.length; i++) {
  //     if ($scope.steps[i].id == id) {
  //       return $scope.steps[i];
  //     }
  //   }
  // }

  // $scope.cacheSelectedOrganizationSteps = function() {

  //     if ($scope.selectedOrganization === null) return null;

  //     $scope.selectedOrganization.workflowSteps.forEach(function(element, idx, array){
  //       console.info('this is an id: '+element);
  //       OrganizationRepo.getStepForId(element).then(function(workflowStep){
  //         // console.info(workflowStep);
  //         //todo order the steps visually
  //         $scope.selectedOrganizationSteps.push(workflowStep);
  //       });
  //     });
  //   };

  // $scope.ready.then(function() {

  //   $scope.cacheSelectedOrganizationSteps();

  //   $scope.selectedOrganizationSteps = [];

  //   $scope.reorderUp = function(originIdx) {
  //     reorder(originIdx, originIdx-1);
  //   };

  //   $scope.reorderDown = function(originIdx) {
  //     reorder(originIdx, originIdx+1);
  //   };

  //   $scope.resetOrganizationSteps = function() {
  //     $scope.modalData = {'name':''};
  //   };

  //   $scope.resetOrganizationSteps();

  //   $scope.createOrganizationSteps = function() {
  //     OrganizationStepsRepo.add($scope.modalData).then(function(){
  //     });
  //       $scope.resetOrganizationSteps();
  //   };

  //   $scope.updateOrganizationSteps = function() {
  //       OrganizationStepsRepo.update($scope.modalData);
  //       $scope.resetOrganizationSteps();
  //   };

  //   $scope.launchEditModal = function(organizationSteps) {
  //       $scope.modalData = organizationSteps;
  //       angular.element('#organizationStepsEditModal').modal('show');
  //   };

  //   $scope.removeOrganizationSteps = function(index) {
  //     OrganizationStepsRepo.remove($scope.modalData).then(function(){
  //       $scope.resetOrganizationSteps();
  //     });
  //   };

  // });
});
