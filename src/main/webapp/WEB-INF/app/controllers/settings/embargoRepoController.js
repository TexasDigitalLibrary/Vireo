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
		
	// defaults
	$scope.resetEmbargo = function() {
		$scope.modalData = {};
	}

	$scope.resetEmbargo();

	$scope.ready.then(function() {
		
		$scope.refresh = function() {
			$scope.proquestEmbargoes = $filter('filter')($scope.embargoes.list, {guarantor: "PROQUEST"});
			$scope.defaultEmbargoes = $filter('filter')($scope.embargoes.list, {guarantor: "DEFAULT"});
			$scope.resetEmbargo();
		}
		
		$scope.refresh();
		
		$scope.createEmbargo = function() {
			EmbargoRepo.create($scope.modalData).then(function(){
			    $scope.refresh();
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
				$scope.refresh();
			});
		};
		
		$scope.removeEmbargo = function(id) {
            EmbargoRepo.remove(id).then(function(){
                $scope.refresh();
            });
        };
		
		$scope.reorderEmbargo = function(src, dest) {
			EmbargoRepo.reorder(src, dest).then(function(){
				$scope.refresh();
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
					$scope.refresh();
					$scope.sortAction = 'confirm';
				});
			}
		};
		
		$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectEmbargo,			
			list: $scope.embargoes.list,
			confirm: '#embargoConfirmRemoveModal',
			reorder: $scope.reorderEmbargo,
			sortLabel: $scope.sortLabel,
			container: '#embargo'
		});
	});
});