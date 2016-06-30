vireo.controller("DepositLocationRepoController", function ($controller, $scope, $q, DepositLocationRepo, DragAndDropListenerFactory) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.depositLocations = DepositLocationRepo.getAll();
	
	$scope.ready = $q.all([DepositLocationRepo.ready()]);

	$scope.dragging = false;
	
	$scope.serverErrors = [];

	$scope.trashCanId = 'deposit-location-trash';
		
	$scope.ready.then(function() {

		$scope.resetDepositLocation = function() {
			$scope.modalData = {
				depositor: 'Sword1Deposit',
				packager: 'VireoExport'
			};
		}
		
		$scope.closeModal = function(modalId) {
			angular.element('#' + modalId).modal('hide');
			// clear all errors, but not infos or warnings
			if($scope.serverErrors !== undefined) {
				$scope.serverErrors.errors = undefined;
			}
		}

		$scope.resetDepositLocation();

		$scope.createDepositLocation = function() {
			DepositLocationRepo.create($scope.modalData).then(function(data) {
				$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
					$scope.resetDepositLocation();
					$scope.closeModal("depositLocationNewModal");
				}
			});
		};
		
		$scope.selectDepositLocation = function(index) {
			$scope.modalData = $scope.depositLocations[index];
		};
		
		$scope.editDepositLocation = function(index) {
			$scope.serverErrors = [];
			$scope.selectDepositLocation(index - 1);
			angular.element('#depositLocationEditModal').modal('show');
		};
		
		$scope.updateDepositLocation = function() {
			DepositLocationRepo.update($scope.modalData).then(function(model) {
				$scope.serverErrors = model.ValidationResponse;
				if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
					$scope.resetDepositLocation();
					$scope.closeModal("depositLocationEditModal");
				}
			});
		};

		$scope.reorderDepositLocation = function(src, dest) {
	    	DepositLocationRepo.reorder(src, dest).then(function(data) {
	    		$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
					$scope.resetDepositLocation();
				}
			});
		};

		$scope.removeDepositLocation = function(index) {
	    	DepositLocationRepo.deleteById(index).then(function(data) {
	    		$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
					$scope.resetDepositLocation();
					$scope.closeModal("depositLocationConfirmRemoveModal");
				}
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

	});	

});