vireo.controller("OrganizationCategoriesController", function ($controller, $scope, $q, OrganizationCategoryRepoModel, DragAndDropListenerFactory) {
  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  $scope.organizationCategories = OrganizationCategoryRepoModel.get();

  $scope.ready = $q.all([OrganizationCategoryRepoModel.ready()]);

  $scope.dragging = false;

  $scope.trashCanId = 'organization-category-trash';

  $scope.ready.then(function() {

    console.log($scope.organizationCategories)

    $scope.resetOrganizationCategories = function() {
      $scope.modalData = {'name':''};
    }

    $scope.resetOrganizationCategories();
    
    $scope.selectOrganizationCategory = function(index) {
      $scope.resetOrganizationCategories();
      $scope.modalData = $scope.organizationCategories.list[index];
    };

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

    $scope.removeOrganizationCategory = function(index) {
      OrganizationCategoryRepoModel.remove($scope.modalData).then(function(){
        $scope.resetOrganizationCategories();
        console.info($scope.organizationCategories);
      });
    };

    $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
      trashId: $scope.trashCanId,
      dragging: $scope.dragging,
      select: $scope.selectOrganizationCategory,     
      model: $scope.organizationCategories,
      confirm: '#organizationCategoryConfirmRemoveModal',
      reorder: null,
      container: '#organization-categories'
    });

    console.log($scope.dragControlListeners);

    var listener = $scope.dragControlListeners.getListener();

   $scope.dragControlListeners.accept = function (sourceItemHandleScope, destSortableScope) {
      var currentElement = destSortableScope.element;
      if(listener.dragging && currentElement[0].id == listener.trash.id) {
        listener.trash.hover = true;
        listener.trash.element = currentElement;
        listener.trash.element.addClass('dragging');
      }
      else {
        listener.trash.hover = false;
      }
      return false;
    };

    $scope.dragControlListeners.orderChanged = function (event) {};

  });
});
