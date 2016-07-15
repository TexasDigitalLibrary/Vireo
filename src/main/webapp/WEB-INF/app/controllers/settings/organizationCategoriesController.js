vireo.controller("OrganizationCategoriesController", function ($controller, $scope, $q, OrganizationCategoryRepo, DragAndDropListenerFactory) {

    angular.extend(this, $controller("AbstractController", {$scope: $scope}));

    $scope.organizationCategoryRepo = OrganizationCategoryRepo;

    $scope.organizationCategories = OrganizationCategoryRepo.getAll();

    OrganizationCategoryRepo.listen(function(data) {
        $scope.resetOrganizationCategories();
    });

    $scope.ready = $q.all([OrganizationCategoryRepo.ready()]);

    $scope.dragging = false;

    $scope.trashCanId = 'organization-category-trash';

    $scope.forms = {};

    $scope.ready.then(function() {

        $scope.resetOrganizationCategories = function() {
            $scope.organizationCategoryRepo.clearValidationResults();
            for(var key in $scope.forms) {
                if(!$scope.forms[key].$pristine) {
                    $scope.forms[key].$setPristine();
                }
            }
        	if($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
    			$scope.modalData.refresh();
    		}
            $scope.modalData = {};
            $scope.closeModal();
        };

        $scope.resetOrganizationCategories();

        $scope.selectOrganizationCategory = function(index) {
            $scope.resetOrganizationCategories();
            $scope.modalData = $scope.organizationCategories[index];
        };

        $scope.createOrganizationCategory = function() {
            OrganizationCategoryRepo.create($scope.modalData);
        };

        $scope.updateOrganizationCategory = function() {
            $scope.modalData.save();
        };

        $scope.launchEditModal = function(organizationCategory) {
            $scope.modalData = organizationCategory;
            $scope.openModal('#organizationCategoryEditModal');
        };

        $scope.removeOrganizationCategory = function() {
            $scope.modalData.delete();
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
