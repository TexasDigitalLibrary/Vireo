vireo.controller("DepositLocationRepoController", function ($controller, $scope, $q, DepositLocationRepo) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.ready = $q.all([DepositLocationRepo.ready()]);

	$scope.depositLocations = DepositLocationRepo.get();

	$scope.depositLocation = {};

	$scope.dragging = false;

	$scope.trashCanId = 'deposit-location-trash';
	
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
				$scope.removeDepositLocation(index);
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
	    		$scope.reorderDepositLocation(src, dest);
	    	}
	    }
	};
	
	$scope.ready.then(function() {

		$scope.createDepositLocation = function() {
			DepositLocationRepo.add($scope.depositLocation);
			$scope.depositLocation = {};
		};

		$scope.reorderDepositLocation = function(src, dest) {
	    	DepositLocationRepo.reorder(src, dest);
		};

		$scope.removeDepositLocation = function(index) {
	    	DepositLocationRepo.remove(index);
		};
		
	});	

});