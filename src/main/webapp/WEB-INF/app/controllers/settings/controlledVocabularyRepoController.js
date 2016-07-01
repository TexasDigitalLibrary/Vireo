vireo.controller("ControlledVocabularyRepoController", function ($controller, $q, $scope, ControlledVocabularyRepo, DragAndDropListenerFactory, LanguageRepo) {
	
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	console.log('do something damnit')

	console.log(ControlledVocabularyRepo);

	$scope.controlledVocabulary = ControlledVocabularyRepo.getAll();

	$scope.languages = LanguageRepo.getAll();

	$scope.ready = $q.all([ControlledVocabularyRepo.ready(), LanguageRepo.ready()]);

	$scope.dragging = false;
	
	$scope.serverErrors = [];

	$scope.trashCanId = 'controlled-vocabulary-trash';
	
	$scope.sortAction = "confirm";

	$scope.uploadAction = "confirm";

	$scope.ready.then(function() {

		console.log($scope.controlledVocabulary)

		var getDefaultIndex = function() {
	    	var defaultIndex = 0;
			for(var i in $scope.controlledVocabulary) {
				var cv = $scope.controlledVocabulary[i];
				if(cv.isEntityProperty == false) {
					defaultIndex = i;
					break;
				}
			}
			return defaultIndex;
	    }

		$scope.resetControlledVocabulary = function() {

			if($scope.uploadAction == 'process') {

				ControlledVocabularyRepo.cancel($scope.uploadModalData.cv.name).then(function(data){
					$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				});
				$scope.uploadAction = 'confirm';
				$scope.uploadStatus();
			}

			$scope.uploadModalData = {
				cv: $scope.controlledVocabulary[getDefaultIndex()]
			};

			$scope.columnHeaders = "";

			$scope.uploadWordMap = {};

			$scope.modalData = { 
				language: $scope.languages[0] 
			};
		};
		
		$scope.closeModal = function(modalId) {
    		angular.element('#' + modalId).modal('hide');
    		// clear all errors, but not infos or warnings
    		if($scope.serverErrors !== undefined) {
    			$scope.serverErrors.errors = undefined;
    		}
    	}

		$scope.resetControlledVocabulary();

		ControlledVocabularyRepo.change.then(null, null, function(data) {
			$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
			if($scope.uploadAction != "process") {
				$scope.uploadStatus();
				$scope.uploadModalData = {
					cv: $scope.controlledVocabulary[getDefaultIndex()]
				};
			}
		});
		
		$scope.createControlledVocabulary = function() {
			ControlledVocabularyRepo.create($scope.modalData).then(function(data) {
				$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
					$scope.resetControlledVocabulary();
            		$scope.closeModal("controlledVocabularyNewModal");
            	}
			});
		};

		$scope.uploadStatus = function() {
			if($scope.uploadModalData.cv != undefined) {
				ControlledVocabularyRepo.status($scope.uploadModalData.cv.name).then(function(data) {
					$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
					$scope.uploadModalData.cv.inProgress = angular.fromJson(data.body).payload.Boolean;
				});
			}
		}
		
		$scope.selectControlledVocabulary = function(index) {
			$scope.modalData = $scope.controlledVocabulary[index];
		};
		
		$scope.editControlledVocabulary = function(index) {
			$scope.serverErrors = [];
			$scope.selectControlledVocabulary(index - 1);
			angular.element('#controlledVocabularyEditModal').modal('show');
		};
		
		$scope.updateControlledVocabulary = function() {
			$scope.modalData.save().then(function(data) {
				$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
					$scope.resetControlledVocabulary();
            		$scope.closeModal("controlledVocabularyEditModal");
            	}
			});
		};

		$scope.reorderControlledVocabulary = function(src, dest) {
	    	ControlledVocabularyRepo.reorder(src, dest).then(function(data) {
	    		$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				$scope.resetControlledVocabulary();
			});
		};

		$scope.sortControlledVocabulary = function(column) {
			if($scope.sortAction == 'confirm') {
				$scope.sortAction = 'sort';
			}
			else if($scope.sortAction == 'sort') {
				ControlledVocabularyRepo.sort(column).then(function(data) {
					$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
	                $scope.resetControlledVocabulary();
			});
				$scope.sortAction = 'confirm';
			}	    	
		};

		$scope.removeControlledVocabulary = function(index) {
	    	ControlledVocabularyRepo.deleteById(index).then(function(data) {
	    		$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
	    		if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
					$scope.resetControlledVocabulary();
            		$scope.closeModal("controlledVocabularyConfirmRemoveModal");
            	}
			});
		};

		$scope.uploadControlledVocabulary = function() {
			if($scope.uploadAction == 'confirm') {
				var reader = new FileReader();
		        reader.onload = function() {
		            ControlledVocabularyRepo.confirmCSV(reader.result, $scope.uploadModalData.cv.name).then(function(data) {
		            	$scope.serverErrors = data.payload.ValidationResponse;
		            	$scope.uploadWordMap = data.payload.HashMap;		            	
		            });
		        };
		        reader.readAsDataURL($scope.uploadModalData.file);    	
				$scope.uploadAction = 'process';
			}
			else if($scope.uploadAction == 'process') {	
				ControlledVocabularyRepo.uploadCSV($scope.uploadModalData.cv.name).then(function(data){
					$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
	                $scope.resetControlledVocabulary();
				});
				$scope.resetControlledVocabulary();
				$scope.uploadAction = 'confirm';
				angular.element('#controlledVocabularyUploadModal').modal('hide');
			}			
		};


		$scope.exportControlledVocabulary = function() {
			$scope.headers = [];
			return ControlledVocabularyRepo.downloadCSV($scope.uploadModalData.cv.name).then(function(data) {
				$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				var csvMap = angular.fromJson(data.body).payload.HashMap;
				for(var key in csvMap.headers) {
					$scope.headers.push(csvMap.headers[key]);
				}
				$scope.resetControlledVocabulary();
				return csvMap.rows;
			});
		};
		
		$scope.filterWord = function (word) {
			return {
				name: word.name,
				definition: word.definition,
				identifier: word.identifier
			};
	    };

	    $scope.filterWordArray = function (words) {

	    	var definition = "";

	    	if(words[0].definition.length > 0) {
	    		definition += '<span class="red">' + words[0].definition + '</span>';
	    	}

	    	if(definition.length > 0 && words[1].definition.length > 0) {
	    		definition += '<span class="glyphicon glyphicon-arrow-right cv-change"></span><span>' + words[1].definition + '</span>'
	    	}

	    	var identifier = "";

	    	if(words[0].identifier.length > 0) {
	    		identifier += '<span class="red">' + words[0].identifier + '</span>';
	    	}

	    	if(identifier.length > 0 && words[1].identifier.length > 0) {
	    		identifier += '<span class="glyphicon glyphicon-arrow-right cv-change"></span><span>' + words[1].identifier + '</span>'
	    	}

			return {
				name: words[0].name,
				definition: definition,
				identifier: identifier
			};
	    };

	    $scope.beginImport = function(file) {
	    	if(file) {
	    		$scope.uploadModalData.file = file;
	    		angular.element('#controlledVocabularyUploadModal').modal('show');
	    	}
	    };

	    $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectControlledVocabulary,			
			model: $scope.controlledVocabulary,
			confirm: '#controlledVocabularyConfirmRemoveModal',
			reorder: $scope.reorderControlledVocabulary,
			container: '#controlled-vocabulary'
		});
		
	});

});