vireo.controller("GraduationMonthRepoController", function ($controller, $scope, $q, GraduationMonthRepo, DragAndDropListenerFactory) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.graduationMonths = GraduationMonthRepo.get();
	
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
		for(var i in months) {
			$scope.monthOptions[i] = months[i];
		}
		for(var i in $scope.graduationMonths.list) {
			delete $scope.monthOptions[$scope.graduationMonths.list[i].month];
		}
	};
		
	$scope.ready.then(function() {

		$scope.resetGraduationMonth = function() {
			$scope.modalData = {'name':'', 'subject':'', 'message':''};
			$scope.resetMonthOptions();
		};
		
		$scope.resetGraduationMonth();

		$scope.createGraduationMonth = function() {
			GraduationMonthRepo.add($scope.modalData).then(function(data) {
				var validationResponse = angular.fromJson(data.body).payload.ValidationResponse;
                console.log(validationResponse);
				$scope.resetGraduationMonth();
			});
		};
		
		$scope.selectGraduationMonth = function(index) {
			$scope.resetMonthOptions();
			$scope.modalData = $scope.graduationMonths.list[index];
			$scope.modalData.month = $scope.modalData.month.toString();
		};
		
		$scope.editGraduationMonth = function(index) {
			$scope.selectGraduationMonth(index - 1);
			angular.element('#graduationMonthEditModal').modal('show');
		};
		
		$scope.updateGraduationMonth = function() {
			GraduationMonthRepo.update($scope.modalData).then(function(data) {
				var validationResponse = angular.fromJson(data.body).payload.ValidationResponse;
                console.log(validationResponse);
				$scope.resetGraduationMonth();
			});
		};

		$scope.reorderGraduationMonth = function(src, dest) {
	    	GraduationMonthRepo.reorder(src, dest).then(function(data) {
	    		var validationResponse = angular.fromJson(data.body).payload.ValidationResponse;
                console.log(validationResponse);
				$scope.resetGraduationMonth();
			});
		};

		$scope.sortGraduationMonths = function(column) {
			if($scope.sortAction == 'confirm') {
				$scope.sortAction = 'sort';
			}
			else if($scope.sortAction == 'sort') {
				GraduationMonthRepo.sort(column).then(function(data) {
					var validationResponse = angular.fromJson(data.body).payload.ValidationResponse;
	                console.log(validationResponse);
					$scope.resetGraduationMonth();
				});
				$scope.sortAction = 'confirm';
			}
	    	
		};

		$scope.removeGraduationMonth = function(index) {
	    	GraduationMonthRepo.remove(index).then(function(data) {
	    		var validationResponse = angular.fromJson(data.body).payload.ValidationResponse;
                console.log(validationResponse);
	    		$scope.resetGraduationMonth();
	    	});
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