vireo.controller("DepositLocationRepoController", function ($controller, $scope, $q, DepositLocationRepo) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.ready = $q.all([DepositLocationRepo.ready()]);

	$scope.depositLocations = DepositLocationRepo.get();

	$scope.depositLocation = {};

	$scope.dragging = false;

	$scope.trashCan = 'deposit-location-trash';

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
					$scope.removeDepositLocation(index);
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
		    		$scope.reorderDepositLocation(src, dest);
		    	}
		    }
		};

	});	

});