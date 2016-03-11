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

		$scope.resetControlledVocabulary = function() {
			var defaultIndex = 0;
			for(var i in $scope.controlledVocabulary.list) {
				var cv = $scope.controlledVocabulary.list[i];
				if(cv.entityProperty == false) {
					defaultIndex = i;
					break;
				}
			}

			$scope.uploadModalData = {
				cv: $scope.controlledVocabulary.list[defaultIndex]
			};

			$scope.columnHeaders = "";

			$scope.modalData = { 
				language: $scope.languages.list[0] 
			};

			$scope.uploadAction = 'confirm';
			$scope.uploadWordMap = {};
		};

		$scope.resetControlledVocabulary();
		
		$scope.createControlledVocabulary = function() {
			console.log($scope.modalData)
			ControlledVocabularyRepo.add($scope.modalData).then(function(response) {
				var responseType = angular.fromJson(response.body).meta.type;
				var responseMessage = angular.fromJson(response.body).meta.message;
				if(responseType != 'SUCCESS') {
					console.log(responseMessage);
				}

				$scope.resetControlledVocabulary();
			});
		};
		
		$scope.selectControlledVocabulary = function(index) {
			var cv = $scope.controlledVocabulary.list[index];
			$scope.modalData = {
				name: cv.name,
				language: cv.language
			};
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
		        	console.log($scope.uploadModalData)
		        	console.log($scope.uploadModalData.cv.name)
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
				angular.element('#controlledVocabularyUploadModal').modal('hide');
				$scope.uploadAction = 'confirm';
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

		$scope.isNew = function() {
			var match = false;
			for(var i in $scope.controlledVocabulary.list) {
				var cv = $scope.controlledVocabulary.list[i];
				if(cv.name == $scope.modalData.name) {
					$scope.modalData.inUse = match = true;
					break;
				}
			}
			if(!match) {
				$scope.modalData.inUse = false;
			}
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
				definition: words[0].definition + ' -> ' + words[1].definition,
				identifier: words[0].identifier + ' -> ' + words[1].identifier
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