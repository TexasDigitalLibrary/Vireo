vireo.controller("DepositLocationRepoController", function ($controller, $scope, $q, DepositLocationRepo, DragAndDropListenerFactory) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.depositLocations = DepositLocationRepo.get();
	
	$scope.ready = $q.all([DepositLocationRepo.ready()]);

	$scope.dragging = false;

	$scope.trashCanId = 'deposit-location-trash';
	
	// defaults
	$scope.resetDepositLocation = function() {
		$scope.modalData = {
			depositor: 'Sword1Deposit',
			packager: 'VireoExport'
		};
	}

	$scope.resetDepositLocation();
	
	$scope.ready.then(function() {		
		
		$scope.createDepositLocation = function() {
			DepositLocationRepo.add($scope.modalData);
			$scope.resetDepositLocation();
		};
		
		$scope.selectDepositLocation = function(index) {
			$scope.modalData = $scope.depositLocations.list[index];
		};
		
		$scope.editDepositLocation = function(index) {
			$scope.selectDepositLocation(index - 1);
			angular.element('#depositLocationEditModal').modal('show');
		};
		
		$scope.updateDepositLocation = function() {
			DepositLocationRepo.update($scope.modalData);
			$scope.resetDepositLocation();
		};

		$scope.reorderDepositLocation = function(src, dest) {
	    	DepositLocationRepo.reorder(src, dest);
		};

		$scope.removeDepositLocation = function(index) {
	    	DepositLocationRepo.remove(index);
	    	$scope.resetDepositLocation();	    	
		};
		
		$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectDepositLocation,			
			list: $scope.depositLocations.list,
			confirm: '#depositLocationConfirmRemoveModal',
			reorder: $scope.reorderDepositLocation
		});
		
	});	

});