vireo.directive("field",  function(RestApi) {
	return {
		templateUrl: 'views/directives/fieldProfile.html',
		restrict: 'E',
		replace: 'false',
		scope: {
			profile: "="
		},
		link: function($scope) {

			$scope.submission = $scope.$parent.submission;
			
			$scope.image = undefined;
			
			var refreshValues = function() {
				$scope.values = $scope.submission.getFieldValuesByFieldPredicate($scope.profile.fieldPredicate);
			};

			$scope.submission.ready().then(function() {
				refreshValues();
			});
			
			
			$scope.showInfo = function(value) {
				var show = true;
				if($scope.updating !== undefined && $scope.updating === value.id && value.value.length > 0) {
					show = false;
				}
				return show;
			};

			$scope.save = function(value) {
				if ($scope.fieldProfileForm.$dirty) {
					$scope.updating = value.id;
					var savePromsie = $scope.submission.saveFieldValue(value);

					savePromsie.then(function() {
						delete $scope.updating;
						if($scope.fieldProfileForm !== undefined) {
							$scope.fieldProfileForm.$setPristine();
						}
						refreshValues();
					});

					return savePromsie;
				}
			};

			$scope.addFieldValue = function() {
				$scope.submission.addFieldValue($scope.profile.fieldPredicate);
				refreshValues();
			};
			
			var remove = function(value) {
				$scope.values.splice($scope.values.indexOf(value), 1);
				$scope.submission.fieldValues.splice($scope.submission.fieldValues.indexOf(value), 1);
			}

			$scope.removeFieldValue = function(value) {
				if(value.id === null) {
					remove(value);
				}
				else {
					$scope.updating = value.id;
					$scope.submission.removeFieldValue(value).then(function() {
						delete $scope.updating;
						remove(value);	
					});
				}
			};

			$scope.showAdd = function(isFirst) {
				return $scope.profile.repeatable && isFirst;
			};
			
			$scope.showRemove = function(isFirst) {
				return $scope.profile.repeatable && !isFirst;
			};

			$scope.getPattern = function() {
				var pattern = "*";
				var cv = $scope.profile.controlledVocabularies[0];
				if(typeof cv !== "undefined") {
					pattern = "";
					for(var i in cv.dictionary) {
						var word = cv.dictionary[i];
						pattern += pattern.length > 0 ? ", " + word.name : word.name;
					}
				}
				return pattern;
			};

			$scope.queueUpload = function(file) {
				$scope.file = file;
			};

			$scope.cancelUpload = function() {
				delete $scope.file;
			};

			$scope.beginUpload = function(fieldValue) {
				$scope.uploading = true;

				var uploadPromise = RestApi.post({
					'endpoint': '', 
					'controller': 'submission',  
					'method': 'upload',
					'data': {
						'fileName': $scope.file.name,
					},
					'file': $scope.file
				});

				uploadPromise.then(
					function(data) {

						var uri = data.meta.message;
						
						fieldValue.value = uri;
						
						$scope.save(fieldValue).then(function() {
							$scope.uploading = false;
							$scope.file.uploaded = true;
						});

						
					}, 
					function(data) {
						console.log("Error", data);
					}
				);

			};
			
			$scope.getFileInfo = function(index) {
				if($scope.file === undefined && $scope.values[index].value.length > 0) {
					$scope.submission.fileInfo($scope.values[index].value).then(function(data) {
						$scope.fileInfo = angular.fromJson(data.body).payload.ObjectNode;
					});
				}
			};

			$scope.getPreview = function(index) {
				var preview = null;
				if($scope.file !== undefined) {
					if($scope.file.type.includes("image/")) {
						preview = $scope.file;
					}
					else if($scope.file.type.includes("pdf")) { 
						preview = "resources/images/pdf-logo.gif";
					}
				}
				if($scope.fileInfo !== undefined) {
					if($scope.fileInfo.type.includes("image/png")) { 
						preview = "resources/images/png-logo.jpg";
					}
					else if($scope.fileInfo.type.includes("image/jpeg")) { 
						preview = "resources/images/jpg-logo.png";
					}
					else if($scope.fileInfo.type.includes("pdf")) { 
						preview = "resources/images/pdf-logo.gif";
					}
				}
				return preview;
			};

			$scope.includeTemplateUrl = "views/inputtype/"+$scope.profile.inputType.name.toLowerCase().replace("_", "-")+".html";
		}
	};
});
