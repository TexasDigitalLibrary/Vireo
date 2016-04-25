vireo.controller("OrganizationStepsController", function ($controller, $scope, $q) { // TODO inject model

  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  // TODO get real model data; also need to inject
  // $scope.organizationSteps = OrganizationStepsRepo.get();
  // $scope.ready = $q.all([OrganizationStepsRepo.ready()]);
  // Mock for now:
  $scope.steps = ['foo', 'bar', 'qux'];

  $scope.ready.then(function() {

    $scope.resetOrganizationSteps = function() {
      $scope.modalData = {'name':''};
    }

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

  });
});
