vireo.controller("LanguageRepoController", function ($controller, $q, $scope, LanguageRepo, DragAndDropListenerFactory) {
	
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.languages = LanguageRepo.get();

	LanguageRepo.getProquestLanguageCodes().then(function(data) {
		$scope.proquestLanguageCodes = angular.fromJson(data.body).payload.HashMap;
	});

	$scope.ready = $q.all([LanguageRepo.ready(), LanguageRepo.getProquestLanguageCodes()]);

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

			for(var i in $scope.languages.list) {
				var language = $scope.languages.list[i];
				language.proquestCode = $scope.proquestLanguageCodes[language.name]
			}

			$scope.modalData = { 
				languages: $scope.languages.list[0] 
			};
		};

		$scope.resetLanguages();
		
		$scope.createLanguage = function() {
			LanguageRepo.add($scope.modalData).then(function() {
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
			LanguageRepo.update($scope.modalData).then(function() {
				$scope.resetLanguages();
			});
		};

		$scope.reorderLanguages = function(src, dest) {
	    	LanguageRepo.reorder(src, dest).then(function() {
	    		$scope.resetLanguages();
	    	});
		};

		$scope.sortLanguages = function(column) {
			console.log('sorting ' + column)
			if($scope.sortAction == 'confirm') {
				$scope.sortAction = 'sort';
			}
			else if($scope.sortAction == 'sort') {
				LanguageRepo.sort(column).then(function() {
					$scope.resetLanguages();
					$scope.sortAction = 'confirm';
				});
			}
		};

		$scope.removeLanguage = function(index) {
	    	LanguageRepo.remove(index).then(function() {
	    		$scope.resetLanguages();
	    	});
		};

		$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectLanguage,
			model: $scope.languages,
			confirm: '#languagesConfirmRemoveModal',
			reorder: $scope.reorderLanguages,
			container: '#languages'
		});

	});	


});