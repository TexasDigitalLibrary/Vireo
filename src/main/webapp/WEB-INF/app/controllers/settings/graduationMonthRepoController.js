vireo.controller("GraduationMonthRepoController", function ($controller, $scope, $q, GraduationMonthRepo, DragAndDropListenerFactory) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.graduationMonthRepo = GraduationMonthRepo;

	$scope.graduationMonths = GraduationMonthRepo.getAll();

	GraduationMonthRepo.listen(function(data) {
        $scope.resetGraduationMonth();
	});

	$scope.ready = $q.all([GraduationMonthRepo.ready()]);

	$scope.dragging = false;
	
	$scope.trashCanId = 'graduation-month-trash';
	
	$scope.monthOptions = {};

	$scope.sortAction = "confirm";
	
	var months = [
		'January', 'February', 'March', 'April', 'May', 'June',
	    'July', 'August', 'September', 'October', 'November', 'December'
	];
	
	$scope.toMonthString = function(month) {
		return months[month];
	};	
	
	$scope.resetMonthOptions = function() {
		$scope.monthOptions = {};
		for(var i in months) {
			$scope.monthOptions[i] = months[i];
		}
		for(var i in $scope.graduationMonths) {
			delete $scope.monthOptions[$scope.graduationMonths[i].month];
		}
	};
		
	$scope.ready.then(function() {

		$scope.resetGraduationMonth = function() {

			$scope.resetMonthOptions();

			if($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
    			$scope.modalData.refresh();
    		}
			$scope.modalData = {};

			for(var i in $scope.monthOptions) {
				if($scope.modalData.month === undefined) {
					$scope.modalData.month = i;
					break;
				}
			}

			$scope.closeModal();
		};
				
		$scope.resetGraduationMonth();

		$scope.createGraduationMonth = function() {
			GraduationMonthRepo.create($scope.modalData);
		};
		
		$scope.selectGraduationMonth = function(index) {
			$scope.resetMonthOptions();
			$scope.modalData = $scope.graduationMonths[index];
			$scope.modalData.month = $scope.modalData.month.toString();
		};
		
		$scope.editGraduationMonth = function(index) {
			$scope.selectGraduationMonth(index - 1);
			$scope.openModal('#graduationMonthEditModal');
		};
		
		$scope.updateGraduationMonth = function() {
			$scope.modalData.save();
		};

		$scope.removeGraduationMonth = function() {
			$scope.modalData.delete();
		};

		$scope.reorderGraduationMonth = function(src, dest) {
	    	GraduationMonthRepo.reorder(src, dest);
		};

		$scope.sortGraduationMonths = function(column) {
			if($scope.sortAction == 'confirm') {
				$scope.sortAction = 'sort';
			}
			else if($scope.sortAction == 'sort') {
				GraduationMonthRepo.sort(column);
				$scope.sortAction = 'confirm';
			}	    	
		};

		$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectGraduationMonth,			
			model: $scope.graduationMonths,
			confirm: '#graduationMonthConfirmRemoveModal',
			reorder: $scope.reorderGraduationMonth,
			container: '#graduation-month'
		});

	});	

});