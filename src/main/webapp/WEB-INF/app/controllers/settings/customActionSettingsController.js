vireo.controller("CustomActionSettingsController", function($controller, $scope, $q, CustomActionSettings) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.ready = $q.all([CustomActionSettings.ready()]);

	$scope.customActions = CustomActionSettings.get();

	$scope.customAction = { isStudentVisible: false };
	
	$scope.dragging = false;

	$scope.trashCan = 'custom-action-trash';

	$scope.ready.then(function() {

		$scope.createCustomActionSettings = function() {
			CustomActionSettings.create($scope.customAction);
			$scope.customAction = { isStudentVisible: false };
		};
		
		$scope.reorderCustomActionSettings = function(src, dest) {
			CustomActionSettings.reorder(src, dest);
		};
		
		$scope.removeCustomActionSettings = function(index) {
			CustomActionSettings.remove(index);
		};
		
		$scope.loadEditModal = function(item) {
			$scope.customAction = item;
		};

		$scope.editCustomActionSettings = function() {
			CustomActionSettings.edit($scope.customAction);
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