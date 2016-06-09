vireo.controller("LanguageRepoController", function ($controller, $q, $scope, LanguageRepo, DragAndDropListenerFactory) {
	
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.languages = LanguageRepo.get();

	LanguageRepo.getProquestLanguageCodes().then(function(data) {
		$scope.proquestLanguageCodes = angular.fromJson(data.body).payload.HashMap;
	});

	$scope.ready = $q.all([LanguageRepo.ready(), LanguageRepo.getProquestLanguageCodes()]);

	$scope.dragging = false;
	
	$scope.serverErrors = [];

	$scope.trashCanId = 'language-trash';
	
	$scope.sortAction = "confirm";

	$scope.uploadAction = "confirm";

	$scope.ready.then(function() {

		$scope.resetLanguages = function() {
			if($scope.uploadAction == 'process') {
				$scope.uploadAction = 'confirm';
				$scope.uploadStatus();
			}

			for(var i in $scope.languages.list) {
				var language = $scope.languages.list[i];
				language.proquestCode = $scope.proquestLanguageCodes[language.name]
			}

			$scope.modalData = { 
				languages: $scope.languages.list[0] 
			};
		};
		
		$scope.closeModal = function(modalId) {
    		angular.element('#' + modalId).modal('hide');
    		// clear all errors, but not infos or warnings
    		if($scope.serverErrors !== undefined) {
    			$scope.serverErrors.errors = undefined;
    		}
    	}

		$scope.resetLanguages();
		
		$scope.createLanguage = function() {
			LanguageRepo.add($scope.modalData).then(function(data) {
				$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
            	if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
            		$scope.resetLanguages();
            		$scope.closeModal("languagesNewModal");
            	}
			});
		};
		
		$scope.selectLanguage = function(index) {
			$scope.modalData = $scope.languages.list[index];
		};
		
		$scope.editLanguage = function(index) {
			$scope.serverErrors = [];
			$scope.selectLanguage(index - 1);
			angular.element('#languagesEditModal').modal('show');
		};
		
		$scope.updateLanguage = function() {
			LanguageRepo.update($scope.modalData).then(function(data) {
				$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
            	if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
            		$scope.resetLanguages();
            		$scope.closeModal("languagesEditModal");
            	}
			});
		};

		$scope.reorderLanguages = function(src, dest) {
	    	LanguageRepo.reorder(src, dest).then(function(data) {
	    		$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
            	if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
            		$scope.resetLanguages();
            	}
	    	});
		};

		$scope.sortLanguages = function(column) {
			console.log('sorting ' + column)
			if($scope.sortAction == 'confirm') {
				$scope.sortAction = 'sort';
			}
			else if($scope.sortAction == 'sort') {
				LanguageRepo.sort(column).then(function(data) {
					$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
	            	if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
	            		$scope.resetLanguages();
	            	}
					$scope.sortAction = 'confirm';
				});
			}
		};

		$scope.removeLanguage = function(index) {
	    	LanguageRepo.remove(index).then(function(data) {
	    		$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
            	if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
            		$scope.resetLanguages();
            		$scope.closeModal("languagesConfirmRemoveModal");
            	}
	    	});
		};

		$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectLanguage,
			model: $scope.languages.list,
			confirm: '#languagesConfirmRemoveModal',
			reorder: $scope.reorderLanguages,
			container: '#languages'
		});

	});	


});