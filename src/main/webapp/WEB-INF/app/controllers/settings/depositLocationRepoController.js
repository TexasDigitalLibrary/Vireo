vireo.controller("DepositLocationRepoController", function ($controller, $scope, $q, DepositLocationRepo, DragAndDropListenerFactory) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.depositLocations = DepositLocationRepo.get();
	
	$scope.ready = $q.all([DepositLocationRepo.ready()]);

	$scope.dragging = false;

	$scope.trashCanId = 'deposit-location-trash';
		
	$scope.ready.then(function() {

		$scope.createDepositLocation = function() {
			DepositLocationRepo.add($scope.modalData).then(function() {
				$scope.resetDepositLocation();
			});
		};
		
		$scope.selectDepositLocation = function(index) {
			$scope.modalData = $scope.depositLocations.list[index];
		};
		
		$scope.editDepositLocation = function(index) {
			$scope.selectDepositLocation(index - 1);
			angular.element('#depositLocationEditModal').modal('show');
		};
		
		$scope.updateDepositLocation = function() {
			DepositLocationRepo.update($scope.modalData).then(function() {
				$scope.resetDepositLocation();
			});
		};

		$scope.reorderDepositLocation = function(src, dest) {
	    	DepositLocationRepo.reorder(src, dest).then(function() {
				$scope.resetDepositLocation();
			});
		};

		$scope.removeDepositLocation = function(index) {
	    	DepositLocationRepo.remove(index).then(function() {
				$scope.resetDepositLocation();
			});
		};

		$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectDepositLocation,			
			model: $scope.depositLocations,
			confirm: '#depositLocationConfirmRemoveModal',
			reorder: $scope.reorderDepositLocation,
			container: '#deposit-location'
		});

		$scope.resetDepositLocation = function() {
			$scope.modalData = {
				depositor: 'Sword1Deposit',
				packager: 'VireoExport'
			};
		}

		$scope.resetDepositLocation();

	});	

});