vireo.controller("OrganizationCategoriesController", function ($controller, $scope, $q, OrganizationCategoryRepoModel) {
  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  $scope.organizationCategories = OrganizationCategoryRepoModel.get();

  $scope.ready = $q.all([OrganizationCategoryRepoModel.ready()]);

  $scope.ready.then(function() {

    $scope.resetOrganizationCategories = function() {
      $scope.modalData = {'name':''};
    }

    $scope.resetOrganizationCategories();
    console.info($scope);

    $scope.createOrganizationCategory = function() {
      OrganizationCategoryRepoModel.add($scope.modalData).then(function(){
      });
        $scope.resetOrganizationCategories();
    };

    $scope.updateOrganizationCategory = function() {
        OrganizationCategoryRepoModel.update($scope.modalData);
        $scope.resetOrganizationCategories();
    };

    $scope.launchEditModal = function(organizationCategory) {
        $scope.modalData = organizationCategory;
        angular.element('#organizationCategoryEditModal').modal('show');
    };

    // $scope.removeOrganizationCategory = function(index) {
    //   OrganizationCategoryRepo.remove(index).then(function() {
    //     $scope.resetOrganizationCategories();
    //   });
    // };

  });
});
