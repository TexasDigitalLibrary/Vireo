
vireo.controller("OrganizationCategoriesController", function ($controller, $scope, $q, OrganizationCategoryRepo, DragAndDropListenerFactory) {

  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  $scope.organizationCategories = OrganizationCategoryRepo.getAll();

  $scope.ready = $q.all([OrganizationCategoryRepo.ready()]);

  $scope.dragging = false;
  
  $scope.serverErrors = [];

  $scope.trashCanId = 'organization-category-trash';

  $scope.ready.then(function() {

    $scope.resetOrganizationCategories = function() {
      $scope.modalData = {'name':''};
    }
    
    $scope.closeModal = function(modalId) {
		angular.element('#' + modalId).modal('hide');
		// clear all errors, but not infos or warnings
		if($scope.serverErrors !== undefined) {
			$scope.serverErrors.errors = undefined;
		}
	}

    $scope.resetOrganizationCategories();
    
    $scope.selectOrganizationCategory = function(index) {
      $scope.resetOrganizationCategories();
      $scope.modalData = $scope.organizationCategories[index];
    };

    $scope.createOrganizationCategory = function() {
      OrganizationCategoryRepo.add($scope.modalData).then(function(data){
    	  $scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
    	  if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
    		  $scope.resetOrganizationCategories();
    		  $scope.closeModal("organizationCategoryNewModal");
    	  }
      });
    };

    $scope.updateOrganizationCategory = function() {
        OrganizationCategoryRepo.update($scope.modalData).then(function(data){
        	$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
        	if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
      		  $scope.resetOrganizationCategories();
      		  $scope.closeModal("organizationCategoryEditModal");
      	  }
        });
    };

    $scope.launchEditModal = function(organizationCategory) {
    	$scope.serverErrors = [];
        $scope.modalData = organizationCategory;
        angular.element('#organizationCategoryEditModal').modal('show');
    };

    $scope.removeOrganizationCategory = function(index) {
      OrganizationCategoryRepo.remove($scope.modalData).then(function(data){
    	  $scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
    	  if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
    		  $scope.resetOrganizationCategories();
    		  $scope.closeModal("organizationCategoryConfirmRemoveModal");
    	  }
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
