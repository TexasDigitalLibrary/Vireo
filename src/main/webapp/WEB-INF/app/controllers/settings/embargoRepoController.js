vireo.controller("EmbargoRepoController", function ($controller, $scope, $q, EmbargoRepo, DragAndDropListenerFactory, $filter) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.embargoRepo = EmbargoRepo;

	$scope.embargoes = EmbargoRepo.getAll();

	EmbargoRepo.listen(function(data) {
        $scope.resetEmbargo();
	});

	$scope.ready = $q.all([EmbargoRepo.ready()]);

	$scope.dragging = false;

	$scope.trashCanId = 'embargo-trash';

	$scope.sortAction = "confirm";

	$scope.sortDefault = "sortDefaultEmbargoes";
	$scope.sortProquest = "sortProquestEmbargoes";

	$scope.sortLabel = "";

	$scope.forms = {};

	$scope.ready.then(function() {

		$scope.resetEmbargo = function() {
			$scope.embargoRepo.clearValidationResults();
			for(var key in $scope.forms) {
    			if($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
    				$scope.forms[key].$setPristine();
    			}
    		}
			if($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
    			$scope.modalData.refresh();
    		}
			$scope.modalData = {
				isActive: false,
				duration: 12
			};
			$scope.proquestEmbargoes = $filter('filter')($scope.embargoes, {guarantor: "PROQUEST"});
			$scope.defaultEmbargoes = $filter('filter')($scope.embargoes, {guarantor: "DEFAULT"});
			$scope.closeModal();
		};

		$scope.resetEmbargo();

		$scope.createEmbargo = function() {
			EmbargoRepo.create($scope.modalData);
		};

		$scope.selectEmbargo = function(id) {
			$scope.modalData = $filter('filter')($scope.embargoes, {id: id})[0];
		};

		$scope.editEmbargo = function(id) {
			$scope.selectEmbargo(id);
			$scope.openModal('#embargoEditModal');
		};

		$scope.updateEmbargo = function() {
			$scope.modalData.save();
		};

		$scope.removeEmbargo = function() {
			$scope.modalData.delete();
        };

		$scope.reorderEmbargoDefault = function(src, dest) {
			EmbargoRepo.reorder("DEFAULT", src, dest);
		};

		$scope.reorderEmbargoProquest = function(src, dest) {
			EmbargoRepo.reorder("PROQUEST", src, dest);
		};

		$scope.sortEmbargoesDefault = function(column) {
			if($scope.sortAction != $scope.sortDefault && $scope.sortAction != $scope.sortProquest) {
				$scope.sortAction = $scope.sortDefault;
			}
			else if($scope.sortAction == $scope.sortDefault || $scope.sortAction == $scope.sortProquest) {
				EmbargoRepo.sort("DEFAULT", column);
				$scope.sortAction = 'confirm';
			}
		};

		$scope.sortEmbargoesProquest = function(column) {
			if($scope.sortAction != $scope.sortDefault && $scope.sortAction != $scope.sortProquest) {
				$scope.sortAction = $scope.sortProquest;
			}
			else if($scope.sortAction == $scope.sortDefault || $scope.sortAction == $scope.sortProquest) {
				EmbargoRepo.sort("PROQUEST", column);
				$scope.sortAction = 'confirm';
			}
		};

		$scope.dragControlListenersDefault = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectEmbargo,
			model: $scope.embargoes,
			confirm: '#embargoConfirmRemoveModal',
			reorder: $scope.reorderEmbargoDefault,
			sortLabel: $scope.sortLabel,
			container: '#embargoes'
		});

		$scope.dragControlListenersProquest = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectEmbargo,
			model: $scope.embargoes,
			confirm: '#embargoConfirmRemoveModal',
			reorder: $scope.reorderEmbargoProquest,
			sortLabel: $scope.sortLabel,
			container: '#embargoes'
		});

	});
});
