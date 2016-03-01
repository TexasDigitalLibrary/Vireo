vireo.controller("GraduationMonthRepoController", function ($controller, $scope, $q, GraduationMonthRepo, DragAndDropListenerFactory) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.ready = $q.all([GraduationMonthRepo.ready()]);

	$scope.graduationMonths = GraduationMonthRepo.get();

	$scope.dragging = false;

	$scope.trashCanId = 'graduation-month-trash';
	
	// defaults
	$scope.resetGraduationMonth = function() {
		$scope.modalData = {
			month: '0'
		};
	}

	$scope.resetGraduationMonth();

	$scope.ready.then(function() {	

		$scope.monthOptions = {
		    "0": "January",
		    "1": "February",
		    "2": "March",
		    "3": "April",
		    "4": "May",
		    "5": "June",
		    "6": "July",
		    "7": "August",
		    "8": "September",
		    "9": "October",
		    "10": "November",
		    "11": "December"
		};

		$scope.toMonthString = function(month) {
			return $scope.monthOptions[month];
		};	
		
		$scope.createGraduationMonth = function() {
			GraduationMonthRepo.add($scope.modalData);
			$scope.resetGraduationMonth();
		};
		
		$scope.selectGraduationMonth = function(index) {
			$scope.modalData = $scope.graduationMonths.list[index];
			$scope.modalData.month = $scope.modalData.month.toString();
		};
		
		$scope.editGraduationMonth = function(index) {
			console.log(index)
			$scope.selectGraduationMonth(index - 1);
			console.log($scope.modalData.month)
			angular.element('#graduationMonthEditModal').modal('show');
		};
		
		$scope.updateGraduationMonth = function() {
			GraduationMonthRepo.update($scope.modalData);
			$scope.resetGraduationMonth();
		};

		$scope.reorderGraduationMonth = function(src, dest) {
	    	GraduationMonthRepo.reorder(src, dest);
		};

		$scope.removeGraduationMonth = function(index) {
	    	GraduationMonthRepo.remove(index);
	    	$scope.resetGraduationMonth();	    	
		};
		
		$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectGraduationMonth,			
			list: $scope.graduationMonths.list,
			confirm: '#graduationMonthConfirmRemoveModal',
			reorder: $scope.reorderGraduationMonth
		});
		
	});	

});