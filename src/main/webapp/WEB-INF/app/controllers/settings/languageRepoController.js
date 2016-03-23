vireo.controller("LanguageRepoController", function ($controller, $q, $scope, LanguageRepo, DragAndDropListenerFactory) {
	
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.languages = LanguageRepo.get();

	$scope.ready = $q.all([LanguageRepo.ready()]);

	$scope.dragging = false;

	$scope.trashCanId = 'language-trash';
	
	$scope.sortAction = "confirm";

	$scope.uploadAction = "confirm";

	$scope.ready.then(function() {

		$scope.resetLanguages = function() {
			if($scope.uploadAction == 'process') {
				$scope.uploadAction = 'confirm';
				$scope.uploadStatus();
			}

			$scope.modalData = { 
				languages: $scope.languages.list[0] 
			};
		};

		$scope.resetLanguages();
		
		$scope.createLanguage = function() {
			LanguageRepo.add($scope.modalData).then(function(response) {
				var responseType = angular.fromJson(response.body).meta.type;
				var responseMessage = angular.fromJson(response.body).meta.message;
				if(responseType != 'SUCCESS') {
					console.log(responseMessage);
				}
				$scope.resetLanguages();
			});
		};
		
		$scope.selectLanguage = function(index) {
			$scope.modalData = $scope.languages.list[index];
		};
		
		$scope.editLanguage = function(index) {
			$scope.selectLanguage(index - 1);
			angular.element('#languagesEditModal').modal('show');
		};
		
		$scope.updateLanguage = function() {
			LanguageRepo.update($scope.modalData).then(function(response) {
				var responseType = angular.fromJson(response.body).meta.type;
				var responseMessage = angular.fromJson(response.body).meta.message;
				if(responseType != 'SUCCESS') {
					console.log(responseMessage);
				}
				$scope.resetLanguages();
			});
		};

		$scope.reorderLanguages = function(src, dest) {
	    	LanguageRepo.reorder(src, dest);
		};

		$scope.sortLanguages = function(column) {
			if($scope.sortAction == 'confirm') {
				$scope.sortAction = 'sort';
			}
			else if($scope.sortAction == 'sort') {
				LanguageRepo.sort(column);
				$scope.sortAction = 'confirm';
			}	    	
		};

		$scope.removeLanguage = function(index) {
	    	LanguageRepo.remove(index).then(function(response) {
	    		var responseType = angular.fromJson(response.body).meta.type;
				var responseMessage = angular.fromJson(response.body).meta.message;
				if(responseType != 'SUCCESS') {
					console.log(responseMessage);
				}
	    		$scope.resetLanguages();
	    	});
		};
		
		$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectLanguage,			
			list: $scope.languages.list,
			confirm: '#languagesConfirmRemoveModal',
			reorder: $scope.reorderLanguages,
			container: '#languages'
		});
		
	});	


});