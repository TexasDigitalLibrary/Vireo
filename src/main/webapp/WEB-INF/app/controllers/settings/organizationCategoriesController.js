vireo.controller("OrganizationCategoriesController", function ($controller, $scope, $q, OrganizationCategoryRepoModel) {
  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  $scope.organizationCategories = OrganizationCategoryRepoModel.get();

  $scope.ready = $q.all([OrganizationCategoryRepoModel.ready()]);

  console.info('before ready');
  console.info($scope.organizationCategories);
  $scope.ready.then(function() {

    $scope.resetOrganizationCategories = function() {
      $scope.modalData = {'name':''};
    }

    $scope.resetOrganizationCategories();
    console.info($scope);

    // $scope.selectOrganizationCategory = function(index){
    //   $scope.modalData = $scope.organizationCategories.list[index];
    // }

    $scope.updateOrganizationCategory = function() {
      OrganizationCategoryRepoModel.update($scope.modalData).then(function() {
        console.info('about to reset');
      });
        $scope.resetOrganizationCategories();
    };

    $scope.launchEditModal = function(organizationCategory) {
      $scope.modalData = organizationCategory;
      angular.element('#organizationCategoryEditModal').modal('show');
    };

    // $scope.updateOrganizationCategory = function() {
    //   OrganizationCategoryRepo.update($scope.modalData).then(function() {
    //     $scope.resetOrganizationCategories();
    //   });
    // };

    // $scope.removeOrganizationCategory = function(index) {
    //   OrganizationCategoryRepo.remove(index).then(function() {
    //     $scope.resetOrganizationCategories();
    //   });
    // };

  });
});
