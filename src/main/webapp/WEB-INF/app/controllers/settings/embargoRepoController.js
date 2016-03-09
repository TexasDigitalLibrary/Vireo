vireo.controller("EmbargoRepoController", function($controller, $scope, $q, EmbargoRepo, DragAndDropListenerFactory, $filter) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.embargoes = EmbargoRepo.get();
	
	$scope.ready = $q.all([EmbargoRepo.ready()]);
	
	$scope.dragging = false;

	$scope.trashCanId = 'embargo-trash';
	
	// defaults
	$scope.resetEmbargo = function() {
		$scope.modalData = { 
			isStudentVisible: false 
		};
	}

	$scope.resetEmbargo();

	$scope.ready.then(function() {
		$scope.proquestEmbargoes = $filter('filter')($scope.embargoes.list, {guarantor: "PROQUEST"});
		$scope.defaultEmbargoes = $filter('filter')($scope.embargoes.list, {guarantor: "DEFAULT"});
		
		$scope.createEmbargo = function() {
			EmbargoRepo.create($scope.modalData);
			$scope.resetEmbargo();
		};
		
		$scope.selectEmbargo = function(index) {
			$scope.modalData = $scope.embargoes.list[index];
		};
		
		$scope.editEmbargo = function(index) {
			$scope.selectEmbargo(index - 1);
			angular.element('#embargoEditModal').modal('show');
		};
		
		$scope.updateEmbargo = function() {
			EmbargoRepo.update($scope.modalData);
			$scope.resetEmbargo();
		};
		
		$scope.reorderEmbargo = function(src, dest) {
			EmbargoRepo.reorder(src, dest);
		};
		
		$scope.removeEmbargo = function(index) {
			EmbargoRepo.remove(index);
			$scope.resetEmbargo();
		};
		
		$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectEmbargo,			
			list: $scope.embargoes.list,
			confirm: '#embargoConfirmRemoveModal',
			reorder: $scope.reorderEmbargo,
			container: '#embargo'
		});
		
	});
});