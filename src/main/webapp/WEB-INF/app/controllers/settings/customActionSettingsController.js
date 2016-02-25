vireo.controller("CustomActionSettingsController", function($controller, $scope, $q, CustomActionSettings) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.ready = $q.all([CustomActionSettings.ready()]);
	
	$scope.customActions = CustomActionSettings.get();
	
	$scope.dragging = false;

	$scope.trashCanId = 'custom-action-trash';
	
	$scope.resetCustomAction = function() {
		$scope.modalData = { isStudentVisible: false };
	}

	$scope.resetCustomAction();

	$scope.ready.then(function() {		
		
		var trash = {
			hover: false,
			element: null
		};

		$scope.dragControlListeners = {
			dragStart: function(event) {
				$scope.dragging = true;
			},
			dragMove: function(event) {
				if(trash.hover) {
					trash.hover = false;
					trash.element.removeClass('dragging');
				}
			},
			dragEnd: function(event) {
				$scope.dragging = false;
				if(trash.hover) {
					var index = event.source.index + 1;					
					$scope.modalData = $scope.customActions.list[index - 1];
					angular.element('#confirmRemoveCustomActionModal').modal('show');
					trash.element.removeClass('dragging');
				}
			},
		    accept: function (sourceItemHandleScope, destSortableScope) {
		    	var currentElement = destSortableScope.element;
		    	if(currentElement[0].id == $scope.trashCanId) {
		    		trash.hover = true;
		    		trash.element = currentElement;
	     			trash.element.addClass('dragging');
	     		}
	     		else {	     			
	     			trash.hover = false;
	     		}
		     	return sourceItemHandleScope.itemScope.sortableScope.$id === destSortableScope.$id;
		    },
		    orderChanged: function(event) {
		    	if(!trash.hover) {
		    		var src = event.source.index + 1;
		    		var dest = event.dest.index + 1;
		    		$scope.reorderCustomActionSettings(src, dest);
		    	}
		    }
		};
		
		$scope.createCustomActionSettings = function() {
			CustomActionSettings.create($scope.modalData);
			$scope.resetCustomAction();
		};
		
		$scope.reorderCustomActionSettings = function(src, dest) {
			CustomActionSettings.reorder(src, dest);
		};
		
		$scope.removeCustomActionSettings = function(index) {
			CustomActionSettings.remove(index);
		};
		
		$scope.selectCustomAction = function(index) {
			$scope.modalData = $scope.customActions.list[index - 1];
		};

		$scope.editCustomActionSettings = function() {
			CustomActionSettings.edit($scope.modalData);
		};
	});
});