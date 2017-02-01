vireo.directive("field",  function($controller, $q, $timeout, FileApi) {
	return {
		templateUrl: 'views/directives/fieldProfile.html',
		restrict: 'E',
		replace: 'false',
		scope: {
			profile: "=",
			hfp: "="
		},
		link: function($scope) {
			
			angular.extend(this, $controller('AbstractController', {$scope: $scope}));
			
			$scope.submission = $scope.$parent.submission;
			
			$scope.progress = 0;
			
			$scope.image = undefined;
			
			var refreshValues = function() {
				$scope.fieldValues = $scope.submission.getFieldValuesByFieldPredicate($scope.profile.fieldPredicate);
			};

			$scope.submission.ready().then(function() {
				refreshValues();
			});

			$scope.save = function(fieldValue) {
				if ($scope.fieldProfileForm.$dirty) {
					fieldValue.updating = true;
					return $q(function(resolve) {
						// give typeahead time to set the value
						$timeout(function() {
							$scope.submission.saveFieldValue(fieldValue, $scope.profile).then(function() {
								delete fieldValue.updating;
								if($scope.fieldProfileForm !== undefined) {
									$scope.fieldProfileForm.$setPristine();
								}
								refreshValues();
								resolve();
							});
						}, 500);
					});
				}
			};

			$scope.addFieldValue = function() {
				refreshValues();
				return $scope.submission.addFieldValue($scope.profile.fieldPredicate);
			};
			
			var remove = function(fieldValue) {
				$scope.fieldValues.splice($scope.fieldValues.indexOf(fieldValue), 1);
				$scope.submission.fieldValues.splice($scope.submission.fieldValues.indexOf(fieldValue), 1);
			}

			$scope.removeFieldValue = function(fieldValue) {
				if(fieldValue.id === null) {
					remove(fieldValue);
				}
				else {
					fieldValue.updating = true;
					$scope.submission.removeFieldValue(fieldValue).then(function() {
						delete fieldValue.updating;
						remove(fieldValue);	
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
			
			$scope.queueUpload = function(files) {
				if(files.length > 0) {
					$scope.previewing = true;
					if($scope.profile.repeatable === true) {
						var lastExistingFieldValue = $scope.fieldValues[$scope.fieldValues.length - 1];
						for(var i in files) {
							if(i == 0 && $scope.hasFile(lastExistingFieldValue)) {
								$scope.addFieldValue();
							}
							if(i > 0) {
								$scope.addFieldValue();
							}
						}
						var j = 0;
						for(var i in $scope.fieldValues) {
							var fieldValue = $scope.fieldValues[i];
							if(!$scope.hasFile(fieldValue)) {
								fieldValue.file = files[j];
								j++;
							}
						}
					}
					else {
						$scope.fieldValues[0].file = files[0];
					}
				}
			};

			$scope.beginUpload = function() {
				$scope.progress = 0;
				$scope.uploading = true;
				var promises = [];
				for(var i in $scope.fieldValues) {
					var fieldValue = $scope.fieldValues[i];					
					if(fieldValue.file.uploaded === undefined) {
						fieldValue.progress = 0;
						fieldValue.uploading = true;
						promises.push(upload(fieldValue));
					}
				}
				 $q.all(promises).then(function() {
					 $scope.previewing = false;
					 $scope.uploading = false;
				 });
			};
			
			var upload = function(fieldValue) {
				return $q(function(resolve) {
					FileApi.upload({
						'endpoint': '', 
						'controller': 'submission',
						'method': 'upload',
						'file': fieldValue.file
					}).then(function (response) {
						if($scope.hasFile(fieldValue)) {
			            	$scope.submission.removeFile(fieldValue.value);
			            }
			            fieldValue.value = response.data.meta.message;
			            $scope.save(fieldValue).then(function() {
							fieldValue.uploading = false;
							$scope.fetchFileInfo(fieldValue);
							resolve();
						});
			        }, function (response) {
			            console.log('Error status: ' + response.status);
			        }, function (progress) {
			            $scope.progress = progress;
			            fieldValue.progress = progress;
			        });
				})
			};
			
			$scope.cancelUpload = function() {
				for(var i = $scope.fieldValues.length - 1; i >= 0; i--) {
					var fieldValue = $scope.fieldValues[i];
					if(!$scope.hasFile(fieldValue)) {
						if(i > 0) {
							remove(fieldValue);
						}
					}
				}
				$scope.previewing = false;
			};

			$scope.cancel = function(fieldValue) {
				if($scope.fieldValues.length == 0) {
					delete fieldValue.file;
				}
				else {
					remove(fieldValue);
				}
				var stillPreviewing = false;
				for(var i in $scope.fieldValues) {
					if(!$scope.hasFile($scope.fieldValues[i]) && $scope.fieldValues[i].file !== undefined) {
						stillPreviewing = true;
						break;
					}
				}
				$scope.previewing = stillPreviewing;
			};
			
			$scope.hasFile = function(fieldValue) {
				return fieldValue.value.length > 0;
			};
			
			$scope.hasFiles = function() {
				var hasFiles = false;
				for(var i in $scope.fieldValues) {
					if($scope.hasFile($scope.fieldValues[i])) {
						hasFiles = true;
						break;
					}
				}
				return hasFiles;
			};

			$scope.fetchFileInfo = function(fieldValue) {
				if($scope.hasFile(fieldValue)) {
					$scope.submission.fileInfo(fieldValue.value).then(function(data) {
						fieldValue.file = angular.fromJson(data.body).payload.ObjectNode;
					});
				}
			};
			
			$scope.getPreview = function(fieldValue) {
				var preview;
				if(fieldValue.file !== undefined && fieldValue.file.type !== null) {
					if(fieldValue.file.type.includes("image/png")) { 
						preview = "resources/images/png-logo.jpg";
					}
					else if(fieldValue.file.type.includes("image/jpeg")) { 
						preview = "resources/images/jpg-logo.png";
					}
					else if(fieldValue.file.type.includes("pdf")) { 
						preview = "resources/images/pdf-logo.gif";
					}
				}
				return preview;
			};

			$scope.getFile = function(fieldValue) {
				if($scope.hasFile(fieldValue)) {
					$scope.submission.file(fieldValue.value).then(function(data) {
						saveAs(new Blob([data], { type:fieldValue.file.type }), fieldValue.file.name);
					});
				}
				else {
					saveAs(fieldValue.file);
				}
			};
			
			$scope.getUriHash = function(fieldValue) {
				var hash = 0;
				if(fieldValue !== undefined) {
					var uri = fieldValue.value;
					for (i = 0; i < uri.length; i++) {
						char = uri.charCodeAt(i);
						hash = ((hash<<5)-hash)+char;
						hash = hash & hash;
					}
				}
				return hash;
			};

			$scope.removeFile = function(fieldValue) {
				$scope.deleting = true;
				$scope.submission.removeFile(fieldValue.value).then(function(res) {
					$scope.closeModal();
					if($scope.fieldValues.length > 1) {
						$scope.submission.removeFieldValue(fieldValue).then(function() {
							$scope.deleting = false;
							remove(fieldValue);
						});
					}
					else {
						var cloneFieldValue = angular.copy(fieldValue);
						cloneFieldValue.value = "";
						$scope.fieldProfileForm.$dirty = true;
						$scope.save(cloneFieldValue).then(function() {
							$scope.deleting = false;
							fieldValue.value = "";
						});
					}
				});
			};

			$scope.isHashedFieldProfile = function(fv) {
				// console.log("fv.fieldPredicate.value", fv.fieldPredicate.value);
				// console.log("$scope.hfp", $scope.hfp);
				// console.log("fv.fieldPredicate.value === $scope.hfp", fv.fieldPredicate.value === $scope.hfp);
				return fv.fieldPredicate.value === $scope.hfp;
			};
			
			console.log("$scope.hfp", $scope.hfp);

			$scope.includeTemplateUrl = "views/inputtype/"+$scope.profile.inputType.name.toLowerCase().replace("_", "-")+".html";
		}
	};
});
