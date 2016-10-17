vireo.directive("field",  function(RestApi) {
	return {
		templateUrl: 'views/directives/fieldProfile.html',
		restrict: 'E',
		replace: 'false',
		scope: {
			profile: "="
		},
		link: function($scope) {

			$scope.saving = 0;

			$scope.submission = $scope.$parent.submission;

			$scope.values = [];
			
			$scope.image = undefined;

			$scope.getValues = function() {
				angular.extend($scope.values, $scope.submission.findFieldValuesByFieldPredicate($scope.profile.fieldPredicate));
				if ($scope.values.length === 0) {
					$scope.values.push({
						id: null,
						value: "",
						fieldPredicate: $scope.profile.fieldPredicate
					});
				}
				return $scope.values;
			};

			$scope.save = function(value) {

				if ($scope.fieldProfileForm.$dirty) {
					$scope.saving = value.id;
					var savePromsie = $scope.submission.saveFieldValue(value);

					savePromsie.then(function() {
						$scope.saving = 0;
						if($scope.fieldProfileForm !== undefined) {
							$scope.fieldProfileForm.$setPristine();
						}
					});

					return savePromsie;
				}

			};

			$scope.addFieldValue = function() {
				$scope.submission.addFieldValue($scope.profile.fieldPredicate);
			};

			$scope.removeFieldValue = function(value) {
				var indexOfValue = $scope.submission.fieldValues.indexOf(value);
				$scope.submission.fieldValues.splice(indexOfValue, 1);
			};

			$scope.filterValuesByFieldPredicate = function(value) {
				return $scope.profile.fieldPredicate.id === value.fieldPredicate.id;
			};

			$scope.showRemove = function(value) {
				return $scope.profile.repeatable && !$scope.first(value);
			};

			$scope.showAdd = function(value) {
				return $scope.profile.repeatable && $scope.first(value);
			};

			$scope.first = function(value) {
				return $scope.submission.findFieldValuesByPredicate($scope.profile.fieldPredicate).indexOf(value) === 0;
			};

			$scope.getPattern = function() {
				
				var pattern = "*";
				var cv = $scope.profile.controlledVocabularies[0];
	
				if(typeof cv !== "undefined") {
					pattern = "";
					for(var i in cv.dictionary) {
						var word = cv.dictionary[i];
						pattern+=word.name;
						if(i+1!==cv.dictionary.length) pattern+=",";
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
			
			$scope.getFile = function(index) {
				
				if($scope.file === undefined && $scope.values[index].value.length > 0) {

					$scope.submission.fileInfo($scope.values[index].value).then(function(data) {						
						
						var file = angular.fromJson(data.body).payload.ObjectNode;
						
						var byteCharacters = atob(file.bytes);
						
						var byteNumbers = new Array(byteCharacters.length);
						for (var i = 0; i < byteCharacters.length; i++) {
						    byteNumbers[i] = byteCharacters.charCodeAt(i);
						}
						
						var byteArray = new Uint8Array(byteNumbers);
						
						$scope.file = new File(byteArray, file.name, {type: file.mime});
					    
					    var fr = new FileReader;

						fr.onload = function() {
							
						    $scope.image = new Image;

						    $scope.image.onload = function() {
						        alert($scope.image.width);
						    };

						    $scope.image.src = fr.result;
						    
						    $scope.file.uploaded = true;
						};

						fr.readAsDataURL($scope.file); 
						
					});
				}
			};

			$scope.getPreview = function(index) {
				var preview = null;
				if($scope.file.type.includes("image/")) preview = $scope.file;
				if($scope.file.type.includes("pdf")) preview = "resources/images/pdf-logo.gif";
				return preview;
			};

			$scope.includeTemplateUrl = "views/inputtype/"+$scope.profile.inputType.name.toLowerCase().replace("_", "-")+".html";
		}
	};
});
