vireo.controller("ControlledVocabularyRepoController", function ($controller, $q, $scope, ControlledVocabularyRepo, DragAndDropListenerFactory, LanguageRepo) {
	
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.controlledVocabulary = ControlledVocabularyRepo.get();

	$scope.languages = LanguageRepo.get();

	$scope.ready = $q.all([ControlledVocabularyRepo.ready(), LanguageRepo.ready()]);

	$scope.dragging = false;

	$scope.trashCanId = 'controlled-vocabulary-trash';
	
	$scope.sortAction = "confirm";

	$scope.uploadAction = "confirm";

	$scope.ready.then(function() {

		var getDefaultIndex = function() {
	    	var defaultIndex = 0;
			for(var i in $scope.controlledVocabulary.list) {
				var cv = $scope.controlledVocabulary.list[i];
				if(cv.entityProperty == false) {
					defaultIndex = i;
					break;
				}
			}
			return defaultIndex;
	    }

		ControlledVocabularyRepo.listenForChange().then(null, null, function() {
			if($scope.uploadAction != "process") {
				$scope.uploadStatus();
				$scope.uploadModalData = {
					cv: $scope.controlledVocabulary.list[getDefaultIndex()]
				};
			}
		});

		$scope.resetControlledVocabulary = function() {

			if($scope.uploadAction == 'process') {
				ControlledVocabularyRepo.cancel($scope.uploadModalData.cv.name)
				$scope.uploadAction = 'confirm';
				$scope.uploadStatus();
			}

			$scope.uploadModalData = {
				cv: $scope.controlledVocabulary.list[getDefaultIndex()]
			};

			$scope.columnHeaders = "";

			$scope.uploadWordMap = {};

			$scope.modalData = { 
				language: $scope.languages.list[0] 
			};
		};

		$scope.resetControlledVocabulary();
		
		$scope.createControlledVocabulary = function() {
			ControlledVocabularyRepo.add($scope.modalData).then(function(response) {
				var responseType = angular.fromJson(response.body).meta.type;
				var responseMessage = angular.fromJson(response.body).meta.message;
				if(responseType != 'SUCCESS') {
					console.log(responseMessage);
				}
				$scope.resetControlledVocabulary();
			});
		};

		$scope.uploadStatus = function() {
			if($scope.uploadModalData.cv != undefined) {
				ControlledVocabularyRepo.status($scope.uploadModalData.cv.name).then(function(response) {
					$scope.uploadModalData.cv.inProgress = angular.fromJson(response.body).payload.Boolean;
				});
			}
		}
		
		$scope.selectControlledVocabulary = function(index) {
			$scope.modalData = $scope.controlledVocabulary.list[index];
		};
		
		$scope.editControlledVocabulary = function(index) {
			$scope.selectControlledVocabulary(index - 1);
			angular.element('#controlledVocabularyEditModal').modal('show');
		};
		
		$scope.updateControlledVocabulary = function() {
			ControlledVocabularyRepo.update($scope.modalData).then(function(response) {
				var responseType = angular.fromJson(response.body).meta.type;
				var responseMessage = angular.fromJson(response.body).meta.message;
				if(responseType != 'SUCCESS') {
					console.log(responseMessage);
				}
				$scope.resetControlledVocabulary();
			});
		};

		$scope.reorderControlledVocabulary = function(src, dest) {
	    	ControlledVocabularyRepo.reorder(src, dest);
		};

		$scope.sortControlledVocabulary = function(column) {
			if($scope.sortAction == 'confirm') {
				$scope.sortAction = 'sort';
			}
			else if($scope.sortAction == 'sort') {
				ControlledVocabularyRepo.sort(column);
				$scope.sortAction = 'confirm';
			}	    	
		};

		$scope.removeControlledVocabulary = function(index) {
	    	ControlledVocabularyRepo.remove(index).then(function(response) {
	    		var responseType = angular.fromJson(response.body).meta.type;
				var responseMessage = angular.fromJson(response.body).meta.message;
				if(responseType != 'SUCCESS') {
					console.log(responseMessage);
				}
	    		$scope.resetControlledVocabulary();
	    	});
		};

		$scope.uploadControlledVocabulary = function() {
			if($scope.uploadAction == 'confirm') {
				var reader = new FileReader();
		        reader.onload = function() {
		            ControlledVocabularyRepo.confirmCSV(reader.result, $scope.uploadModalData.cv.name).then(function(response) {
		            	$scope.uploadWordMap = response.payload.HashMap;		            	
		            });
		        };
		        reader.readAsDataURL($scope.uploadModalData.file);    	
				$scope.uploadAction = 'process';
			}
			else if($scope.uploadAction == 'process') {	
				ControlledVocabularyRepo.uploadCSV($scope.uploadModalData.cv.name);
				$scope.resetControlledVocabulary();
				$scope.uploadAction = 'confirm';
				angular.element('#controlledVocabularyUploadModal').modal('hide');
			}			
		};


		$scope.exportControlledVocabulary = function() {
			$scope.headers = [];
			return ControlledVocabularyRepo.downloadCSV($scope.uploadModalData.cv.name).then(function(response) {
				var csvMap = angular.fromJson(response.body).payload.HashMap;
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
			return {
				name: words[0].name,
				definition: '<span>' + words[0].definition + '</span><span> -> </span><span>' + words[1].definition + '</span>',
				identifier: '<span>' + words[0].identifier + '</span><span> -> </span><span>' + words[1].identifier + '</span>'
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
			list: $scope.controlledVocabulary.list,
			confirm: '#controlledVocabularyConfirmRemoveModal',
			reorder: $scope.reorderControlledVocabulary,
			container: '#controlled-vocabulary'
		});
		
	});	


});