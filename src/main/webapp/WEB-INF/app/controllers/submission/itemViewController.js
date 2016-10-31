vireo.controller("ItemViewController", function ($anchorScroll, $controller, $location, $q, $routeParams, $scope, FieldPredicateRepo, FieldValue, ItemViewService, SidebarService) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	$scope.fieldPredicates = FieldPredicateRepo.getAll();
	
	var ready = $q.all([ItemViewService.selectSubmissionById($routeParams.id), FieldPredicateRepo.ready()])

	ready.then(function(results) {

		$scope.submission = results[0];

		var firstName = $scope.submission.submitter.firstName;
		var lastName = $scope.submission.submitter.lastName;
		var organization = $scope.submission.organization.name;
			
		$scope.title = lastName + ', ' + firstName + ' (' + organization + ')';
		
		$scope.showTab = function(workflowStep) {
			var show = false;
			for(var i in workflowStep.aggregateFieldProfiles) {
				if(workflowStep.aggregateFieldProfiles[i].inputType.name !== 'INPUT_FILE') {
					show = true;
					break;
				}
			}
			return show;
		};
		
		$scope.getTabPath = function(path) {
			return path + "/" + $scope.submission.id;
		};
		
		
		$scope.editReviewerNotes = function() {
			$scope.editingReviewerNotes = true;
			$scope.reviewerNotes = angular.copy($scope.submission.reviewerNotes);
		};
		
		$scope.saveReviewerNotes = function() {
			$scope.savingReviewerNotes = true;
			$scope.editingReviewerNotes = false;
			$scope.submission.saveReviewerNotes($scope.submission.reviewerNotes).then(function(response) {
				$scope.savingReviewerNotes = false;
			});
		};
		
		$scope.cancelReviewerNotes = function() {
			$scope.editingReviewerNotes = false;
			$scope.submission.reviewerNotes = angular.copy($scope.reviewerNotes);
		};
		
		$scope.getFile = function(fieldValue) {
			$scope.submission.file(fieldValue.value).then(function(data) {
				saveAs(new Blob([data], { type:fieldValue.fileInfo.type }), fieldValue.fileInfo.name);
			});
		};
		
		$scope.getFileType = function(fieldPredicate) {
			var type = fieldPredicate.value;
			return type.substring(9).toUpperCase();
		};
		
		$scope.isPrimaryDocument = function(fieldPredicate) {
			return $scope.getFileType(fieldPredicate) == 'PRIMARY';
		};
		
		$scope.deleteFieldValue = function(fieldValue) {
			fieldValue.updating = true;
			$scope.submission.removeFile(fieldValue.value).then(function(res) {
				$scope.closeModal();
				$scope.submission.removeFieldValue(fieldValue).then(function() {
					delete fieldValue.updating;
					$scope.documentFieldValues.splice($scope.documentFieldValues.indexOf(fieldValue), 1);
				});
			});
			
		};
		
		$scope.saveFieldValue = function(fieldValue) {
			fieldValue.updating = true;
			$scope.closeModal();
			$scope.submission.renameFile(fieldValue.value, fieldValue.fileInfo.name).then(function(response) {
				fieldValue.value = angular.fromJson(response.body).meta.message;
				fieldValue.save($scope.submission.id).then(function() {
					fieldValue.updating = false;
				})
			});
		};
		
		$scope.toggleConfirm = function() {
			$scope.confirm = true;
		};
		
		$scope.cancel = function(fieldValue) {
			$scope.closeModal();
			fieldValue.refresh();
			$scope.submission.fileInfo(fieldValue.value).then(function(data) {
				for(var i in $scope.documentFieldValues) {
					if($scope.documentFieldValues[i].id == fieldValue.id) {
						$scope.documentFieldValues[i].fileInfo = angular.fromJson(data.body).payload.ObjectNode;
						break;
					}
				}
			});
		};

		
		$scope.documentFieldValues = [];
		
		var primaryDocumentFieldValue;
		
		var getFileInfo = function(fieldValue) {
			$scope.submission.fileInfo(fieldValue.value).then(function(data) {
				fieldValue.fileInfo = angular.fromJson(data.body).payload.ObjectNode;
				$scope.documentFieldValues.push(new FieldValue(fieldValue));
			});
		}
		
		for(var i in $scope.submission.fieldValues) {
			var fieldValue = $scope.submission.fieldValues[i];
			if(fieldValue.fieldPredicate.documentTypePredicate) {
				if(fieldValue.fieldPredicate.value == '_doctype_primary') {
					primaryDocumentFieldValue = fieldValue;
				}
				getFileInfo(fieldValue);
			}
		}
		
		SidebarService.addBoxes([
		    {
		        "title": "Active Document",
		        "viewUrl": "views/sideboxes/activeDocument.html",
		        "getPrimaryDocumentFileName": function() {
		        	return primaryDocumentFieldValue !== undefined ? primaryDocumentFieldValue.fileInfo !== undefined ? primaryDocumentFieldValue.fileInfo.name : '' : '';
		        },
		        "downloadPrimaryDocument": function() {
		        	$scope.getFile(primaryDocumentFieldValue);
		        },
		        "uploadNewFile": function() {
		        	$scope.openModal('#addFileModal');
		        },
		        "gotoAllFiles": function() {
		        	$location.hash('all-files');
		        	$anchorScroll();
		        }
		    },
		    {
		        "title": "Submission Status",
		        "viewUrl": "views/sideboxes/submissionStatus.html"
		    },
		    {
		        "title": "Custom Actions",
		        "viewUrl": "views/sideboxes/customActions.html"
		    }
		]);

		
	});

});
