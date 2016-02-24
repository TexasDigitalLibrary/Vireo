vireo.controller("CustomActionSettingsController", function($controller, $scope, CustomActionSettings) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));


	$scope.customAction = CustomActionSettings.get();

	$scope.modalData = {};

	$scope.ready = CustomActionSettings.ready();
	
	$scope.dragging = false;

	$scope.trashCan = 'custom-action-trash';

	$scope.ready.then(function() {

		$scope.createCustomActionSettings = function(customAction) {
			CustomActionSettings.create(customAction);
		};

		$scope.loadCreateModal = function() {
			$scope.modalData.newCustomAction = {
				isStudentVisible: false
			};
		};
		
		$scope.loadEditModal = function(customAction) {
			$scope.modalData.editCustomAction = customAction;
		};

		$scope.editCustomActionSettings = function(customAction) {
			CustomActionSettings.edit(customAction);
		};
		
		$scope.removeCustomActionSettings = function(index) {
			CustomActionSettings.remove(index);
		};
		
		$scope.reorderCustomActionSettings = function(src, dest) {
			CustomActionSettings.reorder(src, dest);
		};		
		
		var overTrash = false;

		$scope.dragControlListeners = {
			dragStart: function(event) {
				$scope.dragging = true;
			},
			dragEnd: function(event) {
				$scope.dragging = false;
				if(overTrash) {
					console.log('trash it!');
					var index = event.source.index + 1;
					$scope.removeCustomActionSettings(index);
				}
			},
		    accept: function (sourceItemHandleScope, destSortableScope) {
		    	var id = destSortableScope.element[0].id;
	     		if(id == $scope.trashCan) {
	     			overTrash = true;
	     		}
	     		else {
	     			overTrash = false;
	     		}
		     	return sourceItemHandleScope.itemScope.sortableScope.$id === destSortableScope.$id;
		    },
		    orderChanged: function(event) {
		    	if(!overTrash) {
		    		var src = event.source.index + 1;
		    		var dest = event.dest.index + 1;
		    		$scope.reorderCustomActionSettings(src, dest);
		    	}
		    }
		};

	});
});