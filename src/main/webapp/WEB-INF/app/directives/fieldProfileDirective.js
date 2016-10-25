vireo.directive("field",  function($controller, $q, FileApi) {
	return {
		templateUrl: 'views/directives/fieldProfile.html',
		restrict: 'E',
		replace: 'false',
		scope: {
			profile: "="
		},
		link: function($scope) {
			
			angular.extend(this, $controller('AbstractController', {$scope: $scope}));
			
			$scope.submission = $scope.$parent.submission;
			
			$scope.progress = 0;
			
			$scope.image = undefined;
			
			var refreshValues = function() {
				$scope.values = $scope.submission.getFieldValuesByFieldPredicate($scope.profile.fieldPredicate);
			};

			$scope.submission.ready().then(function() {
				refreshValues();
			});

			$scope.showInfo = function(fieldValue) {
				var show = true;
				if($scope.updating !== undefined && $scope.updating === fieldValue.id && fieldValue.value.length > 0) {
					show = false;
				}
				return show;
			};

			$scope.save = function(fieldValue) {
				if ($scope.fieldProfileForm.$dirty) {
					$scope.updating = fieldValue.id;
					var savePromsie = $scope.submission.saveFieldValue(fieldValue);
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
			
			var remove = function(fieldValue) {
				$scope.values.splice($scope.values.indexOf(fieldValue), 1);
				$scope.submission.fieldValues.splice($scope.submission.fieldValues.indexOf(fieldValue), 1);
			}

			$scope.removeFieldValue = function(fieldValue) {
				if(fieldValue.id === null) {
					remove(fieldValue);
				}
				else {
					$scope.updating = value.id;
					$scope.submission.removeFieldValue(fieldValue).then(function() {
						delete $scope.updating;
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

			
			$scope.files = [];
			
			$scope.filesInfo = {};
			
			var previewFilesInfo = [];
			
			

			$scope.queueUpload = function(files) {
				$scope.previewing = true;
				$scope.files = files;
				for(var i in $scope.files) {
					var file = $scope.files[i];
					previewFilesInfo.push({
						name: file.name,
						type: file.type,
						size: file.size
					});
				}
			};

			


			$scope.beginUpload = function(fieldValue) {
				$scope.uploading = true;
				for(var i in $scope.files) {
					var file = $scope.files[i];
					FileApi.upload({
						'endpoint': '', 
						'controller': 'submission',
						'method': 'upload',
						'file': file
					}).then(function (response) {
			            var uri = response.data.meta.message;
			            fieldValue.value = uri;
						$scope.save(fieldValue).then(function() {
							$scope.previewing = false;
							$scope.uploading = false;
							$scope.uploaded = true;
							$scope.fetchFileInfo(fieldValue);
						});
			        }, function (response) {
			            console.log('Error status: ' + response.status);
			        }, function (progress) {
			            $scope.progress = progress;
			        });
				}
			};
			
			
			$scope.cancelUpload = function() {
				$scope.files = [];
				$scope.filesInfo = {};
				previewFilesInfo = [];
				$scope.previewing = false;
			};
			
			
			$scope.hasFile = function(fieldValue) {
				return fieldValue.value.length > 0;
			};

			
			

			$scope.fetchFileInfo = function(fieldValue) {
				if($scope.filesInfo[fieldValue.value] === undefined) {
					if($scope.hasFile(fieldValue)) {
						$scope.submission.fileInfo(fieldValue.value).then(function(data) {
							$scope.filesInfo[$scope.getUriHash(fieldValue.value)] = angular.fromJson(data.body).payload.ObjectNode;
						});
					}
				}
			};
			
			
			$scope.getFileInfo = function(fieldValue) {
				var fileInfo = $scope.filesInfo[$scope.getUriHash(fieldValue.value)];
				if(fileInfo === undefined) {
					// TODO: handle repeatable
					fileInfo = previewFilesInfo[0];
				}
				return fileInfo;
			};

			
			$scope.getPreview = function(fieldValue) {
				var preview;
				var fileInfo = $scope.getFileInfo(fieldValue);				
				if(fileInfo !== undefined) {
					if(fileInfo.type.includes("image/png")) { 
						preview = "resources/images/png-logo.jpg";
					}
					else if(fileInfo.type.includes("image/jpeg")) { 
						preview = "resources/images/jpg-logo.png";
					}
					else if(fileInfo.type.includes("pdf")) { 
						preview = "resources/images/pdf-logo.gif";
					}
				}
				return preview;
			};
			
			

			$scope.getFile = function(fieldValue) {
				if($scope.hasFile(fieldValue)) {
					$scope.submission.file(fieldValue.value).then(function(data) {
						var fileInfo = $scope.getFileInfo(fieldValue);
						saveAs(new Blob([data], { type:fileInfo.type }), fileInfo.name);
					});
				}
				else {
					// TODO: handle repeatable
					saveAs($scope.files[0]);
				}
			};
			
			$scope.getUriHash = function(uri) {
				var hash = 0;
				if (uri.length == 0) return hash;
				for (i = 0; i < uri.length; i++) {
					char = uri.charCodeAt(i);
					hash = ((hash<<5)-hash)+char;
					hash = hash & hash;
				}
				return hash;
			};
			
			$scope.removeFile = function(fieldValue) {
				$scope.deleting = true;
				$scope.submission.removeFile(fieldValue.value).then(function(res) {
					var cloneFieldValue = angular.copy(fieldValue);
					cloneFieldValue.value = "";
					$scope.fieldProfileForm.$dirty = true;
					$scope.save(cloneFieldValue).then(function() {
						$scope.deleting = false;
						delete $scope.file;
						delete $scope.fileInfo;
						$scope.closeModal();
						fieldValue.value = "";
					});
				});
			};
			
			
			
			

			$scope.includeTemplateUrl = "views/inputtype/"+$scope.profile.inputType.name.toLowerCase().replace("_", "-")+".html";
		}
	};
});
