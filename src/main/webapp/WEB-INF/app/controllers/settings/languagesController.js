vireo.controller("LanguagesController", function ($timeout, $controller, $q, $scope, LanguageRepo, DragAndDropListenerFactory) {
	
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.languageRepo = LanguageRepo;

	$scope.languages = LanguageRepo.getAll();

	LanguageRepo.listen(function(data) {
        $scope.resetLanguages();
	});

	var proquestPromise = LanguageRepo.getProquestLanguageCodes().then(function(data) {
		$scope.proquestLanguageCodes = angular.fromJson(data.body).payload.HashMap;
	});

	$scope.ready = $q.all([LanguageRepo.ready(), proquestPromise]);

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

			for(var i in $scope.languages) {
				var language = $scope.languages[i];
				language.proquestCode = $scope.proquestLanguageCodes[language.name]
			}
			
			if($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
    			$scope.modalData.refresh();
    		}
			$scope.modalData = { 
				languages: $scope.languages[0] 
			};
			$scope.closeModal();
		};

		$scope.resetLanguages();
		
		$scope.createLanguage = function() {
			LanguageRepo.create($scope.modalData);
		};
		
		$scope.selectLanguage = function(index) {
			$scope.modalData = $scope.languages[index];
		};
		
		$scope.editLanguage = function(index) {
			$scope.selectLanguage(index - 1);
			$scope.openModal('#languagesEditModal');
		};
		
		$scope.updateLanguage = function() {
			$scope.modalData.save();
		};

		$scope.removeLanguage = function() {
			$scope.modalData.delete();
		};

		$scope.reorderLanguages = function(src, dest) {
	    	LanguageRepo.reorder(src+10, dest);
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