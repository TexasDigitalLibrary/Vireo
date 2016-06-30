vireo.controller("EmbargoRepoController", function($controller, $scope, $q, EmbargoRepo, DragAndDropListenerFactory, $filter) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.embargoes = EmbargoRepo.get();
	
	$scope.ready = $q.all([EmbargoRepo.ready()]);
	
	$scope.dragging = false;
	
	$scope.serverErrors = [];

	$scope.trashCanId = 'embargo-trash';
	
	$scope.sortAction = "confirm";
	
	$scope.sortDefault = "sortDefaultEmbargoes";
	$scope.sortProquest = "sortProquestEmbargoes";
	
	$scope.sortLabel = "";

	$scope.ready.then(function() {

		$scope.resetEmbargo = function() {
			$scope.modalData = { isActive: false };
			$scope.proquestEmbargoes = $filter('filter')($scope.embargoes.list, {guarantor: "PROQUEST"});
			$scope.defaultEmbargoes = $filter('filter')($scope.embargoes.list, {guarantor: "DEFAULT"});	
		};
		
		$scope.closeModal = function(modalId) {
			angular.element('#' + modalId).modal('hide');
			// clear all errors, but not infos or warnings
			if($scope.serverErrors['DEFAULT'] !== undefined) {
				$scope.serverErrors['DEFAULT'].errors = undefined;
			}
			if($scope.serverErrors['PROQUEST'] !== undefined) {
				$scope.serverErrors['PROQUEST'].errors = undefined;
			}
		}
		
		$scope.resetEmbargo();
		
		$scope.createEmbargo = function() {
			EmbargoRepo.create($scope.modalData).then(function(data) {
				if($scope.serverErrors[$scope.modalData.guarantor] === undefined) {
					$scope.serverErrors[$scope.modalData.guarantor] = [];
				}
				$scope.serverErrors[$scope.modalData.guarantor] = angular.fromJson(data.body).payload.ValidationResponse;
                if($scope.serverErrors[$scope.modalData.guarantor] === undefined || $scope.serverErrors[$scope.modalData.guarantor].errors.length == 0) {
                	$scope.resetEmbargo();
					$scope.closeModal("embargoNewModal");
                }
			});
		};
		
		$scope.selectEmbargo = function(id) {
			$scope.modalData = $filter('filter')($scope.embargoes.list, {id: id})[0];
		};
		
		$scope.editEmbargo = function(id) {
			$scope.serverErrors = [];
			$scope.selectEmbargo(id);
			angular.element('#embargoEditModal').modal('show');
		};
		
		$scope.updateEmbargo = function() {
			EmbargoRepo.update($scope.modalData).then(function(model){
				if($scope.serverErrors[$scope.modalData.guarantor] === undefined) {
					$scope.serverErrors[$scope.modalData.guarantor] = [];
				}
				$scope.serverErrors[$scope.modalData.guarantor] = model.ValidationResponse;
                if($scope.serverErrors[$scope.modalData.guarantor] === undefined || $scope.serverErrors[$scope.modalData.guarantor].errors.length == 0) {
                	$scope.resetEmbargo();
					$scope.closeModal("embargoEditModal");
                }
			});
		};
		
		$scope.removeEmbargo = function(id) {
            EmbargoRepo.remove(id).then(function(data){
            	if($scope.serverErrors[$scope.modalData.guarantor] === undefined) {
					$scope.serverErrors[$scope.modalData.guarantor] = [];
				}
				$scope.serverErrors[$scope.modalData.guarantor] = angular.fromJson(data.body).payload.ValidationResponse;
				if($scope.serverErrors[$scope.modalData.guarantor] === undefined || $scope.serverErrors[$scope.modalData.guarantor].errors.length == 0) {
                	$scope.resetEmbargo();
					$scope.closeModal("embargoEditModal");
                }
            });
        };
		
		$scope.reorderEmbargoDefault = function(src, dest) {
			EmbargoRepo.reorder("DEFAULT", src, dest).then(function(data){
				$scope.serverErrors['DEFAULT'] = angular.fromJson(data.body).payload.ValidationResponse;
                if($scope.serverErrors['DEFAULT'] === undefined || $scope.serverErrors['DEFAULT'].errors.length == 0) {
                	$scope.resetEmbargo();
                }
			});
		};
		
		$scope.reorderEmbargoProquest = function(src, dest) {
			EmbargoRepo.reorder("PROQUEST", src, dest).then(function(data){
				$scope.serverErrors['PROQUEST'] = angular.fromJson(data.body).payload.ValidationResponse;
                if($scope.serverErrors['PROQUEST'] === undefined || $scope.serverErrors['PROQUEST'].errors.length == 0) {
                	$scope.resetEmbargo();
                }
			});
		};
		
		$scope.sortEmbargoesDefault = function(column) {
			if($scope.sortAction != $scope.sortDefault && $scope.sortAction != $scope.sortProquest) {
				$scope.sortAction = $scope.sortDefault;
			} else if($scope.sortAction == $scope.sortDefault || $scope.sortAction == $scope.sortProquest) {
				EmbargoRepo.sort("DEFAULT", column).then(function(data){
					$scope.serverErrors['DEFAULT'] = angular.fromJson(data.body).payload.ValidationResponse;
	                if($scope.serverErrors['DEFAULT'] === undefined || $scope.serverErrors['DEFAULT'].errors.length == 0) {
	                	$scope.resetEmbargo();
	                	$scope.sortAction = 'confirm';
	                }
				});
			}
		};
		
		$scope.sortEmbargoesProquest = function(column) {
			if($scope.sortAction != $scope.sortDefault && $scope.sortAction != $scope.sortProquest) {
				$scope.sortAction = $scope.sortProquest;
			} else if($scope.sortAction == $scope.sortDefault || $scope.sortAction == $scope.sortProquest) {
				EmbargoRepo.sort("PROQUEST", column).then(function(data){
					$scope.serverErrors['PROQUEST'] = angular.fromJson(data.body).payload.ValidationResponse;
	                if($scope.serverErrors['PROQUEST'] === undefined || $scope.serverErrors['PROQUEST'].errors.length == 0) {
	                	$scope.resetEmbargo();
						$scope.sortAction = 'confirm';
	                }
				});
			}
		};
		
		$scope.dragControlListenersDefault = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectEmbargo,			
			model: $scope.embargoes.list,
			confirm: '#embargoConfirmRemoveModal',
			reorder: $scope.reorderEmbargoDefault,
			sortLabel: $scope.sortLabel,
			container: '#embargo'
		});

		$scope.dragControlListenersProquest = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectEmbargo,			
			model: $scope.embargoes.list,
			confirm: '#embargoConfirmRemoveModal',
			reorder: $scope.reorderEmbargoProquest,
			sortLabel: $scope.sortLabel,
			container: '#embargo'
		});
		
	});
});
