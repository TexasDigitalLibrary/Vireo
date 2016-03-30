vireo.controller("EmbargoRepoController", function($controller, $scope, $q, EmbargoRepo, DragAndDropListenerFactory, $filter) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.embargoes = EmbargoRepo.get();
	
	$scope.ready = $q.all([EmbargoRepo.ready()]);
	
	$scope.dragging = false;

	$scope.trashCanId = 'embargo-trash';
	
	$scope.sortAction = "confirm";
	
	$scope.sortDefault = "sortDefaultEmbargoes";
	$scope.sortProquest = "sortProquestEmbargoes";
	
	$scope.sortLabel = "";

	$scope.ready.then(function() {
		
		$scope.createEmbargo = function() {
			EmbargoRepo.create($scope.modalData).then(function(){
			    $scope.resetEmbargo();
			});
		};
		
		$scope.selectEmbargo = function(id) {
			$scope.modalData = $filter('filter')($scope.embargoes.list, {id: id})[0];
		};
		
		$scope.editEmbargo = function(id) {
			$scope.selectEmbargo(id);
			angular.element('#embargoEditModal').modal('show');
		};
		
		$scope.updateEmbargo = function() {
			EmbargoRepo.update($scope.modalData).then(function(){
				$scope.resetEmbargo();
			});
		};
		
		$scope.removeEmbargo = function(id) {
            EmbargoRepo.remove(id).then(function(){
                $scope.resetEmbargo();
            });
        };
		
		$scope.reorderEmbargo = function(src, dest) {
			EmbargoRepo.reorder(src, dest).then(function(){
				$scope.resetEmbargo();
			});
		};
		
		$scope.sortEmbargoes = function(column, where) {
			if($scope.sortAction != $scope.sortDefault && $scope.sortAction != $scope.sortProquest) {
				if(where == "default") {
					$scope.sortAction = $scope.sortDefault;
				} else if (where == "proquest") {
					$scope.sortAction = $scope.sortProquest;
				}
			} else if($scope.sortAction == $scope.sortDefault || $scope.sortAction == $scope.sortProquest) {
				EmbargoRepo.sort(column, where).then(function(){
					$scope.resetEmbargo();
					$scope.sortAction = 'confirm';
				});
			}
		};

		$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectEmbargo,			
			model: $scope.embargoes,
			confirm: '#embargoConfirmRemoveModal',
			reorder: $scope.reorderEmbargo,
			sortLabel: $scope.sortLabel,
			container: '#embargo'
		});
		
		$scope.resetEmbargo = function() {
			$scope.modalData = {};

			$scope.proquestEmbargoes = $filter('filter')($scope.embargoes.list, {guarantor: "PROQUEST"});
			$scope.defaultEmbargoes = $filter('filter')($scope.embargoes.list, {guarantor: "DEFAULT"});
			
		};
		
		$scope.resetEmbargo();
		
	});
});