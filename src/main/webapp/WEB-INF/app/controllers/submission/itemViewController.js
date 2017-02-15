vireo.controller("ItemViewController", function ($anchorScroll, $controller, $location, $q, $routeParams, $scope, FieldPredicateRepo, FieldValue, FileApi, ItemViewService, SidebarService, SubmissionRepo, SubmissionStateRepo, UserRepo, User) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	$scope.fieldPredicates = FieldPredicateRepo.getAll();

	$scope.allUsers = UserRepo.getAll();

	var ready = $q.all([FieldPredicateRepo.ready(), SubmissionRepo.findSubmissionById($routeParams.id), UserRepo.ready(), SubmissionStateRepo.ready()]);

	ready.then(function() {

		$scope.loaded = true;

		$scope.submission = ItemViewService.selectSubmission($routeParams.id);

		SubmissionStateRepo.ready().then(function() {
			$scope.submissionStatusBox.newStatus = submissionStates[0];
		});

		UserRepo.ready().then(function() {
			$scope.submissionStatusBox.assignee = firstAssignable();
		});

		var firstName = $scope.submission.submitter.firstName;
		var lastName = $scope.submission.submitter.lastName;
		var organization = $scope.submission.organization.name;
		var submissionStates = SubmissionStateRepo.getAll();
			
		$scope.title = lastName + ', ' + firstName + ' (' + organization + ')';
		
		$scope.documentFieldValues = [];
		
		$scope.primaryDocumentFieldValue;

		var getFileInfo = function(fieldValue) {
			$scope.submission.fileInfo(fieldValue.value).then(function(data) {
				fieldValue.fileInfo = angular.fromJson(data.body).payload.ObjectNode;
				$scope.documentFieldValues.push(fieldValue);
				if($scope.isPrimaryDocument(fieldValue.fieldPredicate)) {
					$scope.primaryDocumentFieldValue = fieldValue;
				}
			});
		};
		
		for(var i in $scope.submission.fieldValues) {
			var fieldValue = $scope.submission.fieldValues[i];
			if(fieldValue.fieldPredicate.documentTypePredicate) {
				getFileInfo(new FieldValue(fieldValue));
			}
		}


		var updateFileInfo = function(fieldValue) {
			$scope.submission.fileInfo(fieldValue.value).then(function(data) {
				for(var i in $scope.documentFieldValues) {
					if($scope.documentFieldValues[i].id == fieldValue.id) {
						$scope.documentFieldValues[i].fileInfo = angular.fromJson(data.body).payload.ObjectNode;
						break;
					}
				}
			});
		};

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
		
		$scope.hasPrimaryDocument = function() {
        	return $scope.primaryDocumentFieldValue !== undefined && $scope.primaryDocumentFieldValue.id;
        }

		$scope.deleteFieldValue = function(fieldValue) {
			fieldValue.updating = true;
			$scope.submission.removeFile(fieldValue.value).then(function(res) {
				$scope.closeModal();
				$scope.submission.removeFieldValue(fieldValue).then(function() {
					$scope.confirm = false;
					delete fieldValue.updating;
					if($scope.isPrimaryDocument(fieldValue.fieldPredicate)) {
						delete $scope.primaryDocumentFieldValue;
					}
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
		
		$scope.confirm = false;
		
		$scope.toggleConfirm = function() {
			$scope.confirm = !$scope.confirm;
		};
		
		$scope.cancel = function(fieldValue) {
			$scope.closeModal();
			fieldValue.refresh();
			updateFileInfo(fieldValue);
		};
		
		
		$scope.addFileData = {};
		
		$scope.queueUpload = function(files) {
			$scope.addFileData.files = files;
		};
		
		$scope.removeFiles = function() {
			delete $scope.addFileData.files;
		};
		
		$scope.submitAddFile = function() {
			
			$scope.addFileData.uploading = true;
			
			FileApi.upload({
				'endpoint': '', 
				'controller': 'submission',
				'method': 'upload',
				'file': $scope.addFileData.files[0]
			}).then(function (response) {
				
				var fieldValue = $scope.addFileData.addFileSelection == 'replace' ? $scope.primaryDocumentFieldValue : new FieldValue({
					fieldPredicate: $scope.addFileData.fieldPredicate
				});

				if($scope.addFileData.addFileSelection == 'replace') {
					$scope.submission.removeFile($scope.primaryDocumentFieldValue.value);
				}
 
	            fieldValue.value = response.data.meta.message;
	            
	            fieldValue.save($scope.submission.id).then(function() {
	            	updateFileInfo(fieldValue);

	            	if($scope.documentFieldValues.indexOf(fieldValue) === -1) {
	            		$scope.documentFieldValues.push(fieldValue);
	            	}

	            	if($scope.isPrimaryDocument(fieldValue.fieldPredicate)) {
	            		$scope.primaryDocumentFieldValue = fieldValue;
	            	}
	            	
	            	$scope.resetAddFile();
				});
	            
	        }, function (response) {
	            console.log('Error status: ' + response.status);
	        }, function (progress) {
	            $scope.addFileData.progress = progress;
	        });
			
			if($scope.addFileData.needsCorrection) {
				$scope.submission.needsCorrection();
			}

		};
		
		$scope.resetAddFile = function() {
			$scope.addFileData = {};
			$scope.closeModal();
		};
		
		$scope.disableSubmitAddFile = function() {
			var disable = true;
			if($scope.addFileData.addFileSelection == 'replace') {
				disable = $scope.addFileData.files === undefined || $scope.addFileData.uploading;
			}
			else {
				disable = $scope.addFileData.files === undefined || $scope.addFileData.fieldPredicate === undefined || $scope.addFileData.uploading;
			}
			return disable;
		};

		var firstAssignable =  function() {
			var firstAssignable;
			for(var i in $scope.allUsers) {
				if($scope.allUsers[i].role === "ADMINISTRATOR" || $scope.allUsers[i].role === "MANAGER") {
					firstAssignable = $scope.allUsers[i];
					break;	
				}	
			}
			return firstAssignable;
		};

		$scope.activeDocumentBox = {
	        "title": "Active Document",
	        "viewUrl": "views/sideboxes/activeDocument.html",
	        "getPrimaryDocumentFileName": function() {
	        	return $scope.primaryDocumentFieldValue !== undefined ? $scope.primaryDocumentFieldValue.fileInfo !== undefined ? $scope.primaryDocumentFieldValue.fileInfo.name : '' : '';
	        },
	        "downloadPrimaryDocument": function() {
	        	$scope.getFile($scope.primaryDocumentFieldValue);
	        },
	        "uploadNewFile": function() {
	        	$scope.openModal('#addFileModal');
	        },
	        "gotoAllFiles": function() {
	        	$location.hash('all-files');
	        	$anchorScroll();
	        },
	        "hasPrimaryDocument": function() {
	        	return $scope.hasPrimaryDocument();
	        }
	    };

		$scope.submissionStatusBox = {
	        "title": "Submission Status",
	        "viewUrl": "views/sideboxes/submissionStatus.html",
	        "submission": $scope.submission,
	        "SubmissionStateRepo": SubmissionStateRepo,
	        "submissionStates": submissionStates,
	        "advanced": true,
	        "allUsers": $scope.allUsers,
	        "user": new User(),
	        "sending": false,
	        "sendAdvisorEmail": function() {
	        	$scope.submissionStatusBox.sending = true;
	        	$scope.submission.sendAdvisorEmail().then(function() {
	        		$scope.submissionStatusBox.sending = false;
	        		$scope.closeModal();
	        	});
	        },
	        "cancelStatus": SubmissionStateRepo.findByName('Cancelled'),
	        "changeStatus": function(newStatus) {
				$scope.submission.changeStatus(newStatus).then(function() {
					$scope.submissionStatusBox.resetStatus();
				});
			},
	        "deleteSubmission": function() {
				$scope.submission.delete().then(function() {
					$scope.submissionStatusBox.deleteWorking=false;
					$location.path("/admin/list");
				});
			},
	        "changeAssignee": function(assignee) {
				$scope.submission.assign(assignee).then(function() {
					$scope.submissionStatusBox.resetStatus();
				});
			},
			"assignee": firstAssignable(),
			"resetStatus": function() {
				$scope.submissionStatusBox.advanced=true;
				$scope.submissionStatusBox.cancelWorking=false;
				$scope.submissionStatusBox.saveWorking=false;
				$scope.submissionStatusBox.assignWorking=false;
				$scope.submissionStatusBox.assignSaveWorking=false;
				$scope.submissionStatusBox.unassignWorking=false;
				$scope.submissionStatusBox.newStatus = submissionStates[0];
				$scope.submissionStatusBox.assignee = firstAssignable();
				$scope.closeModal();
			},
			"setSubmitDate": function(newDate) {
				$scope.submissionStatusBox.savingDate = true;
				$scope.submission.setSubmissionDate(newDate).then(function() {
					$scope.submissionStatusBox.savingDate = false;
				});
			}
	    };

	  $scope.customActionsBox = {
		  "title": "Custom Actions",
		  "viewUrl": "views/sideboxes/customActions.html",
		  "submission": $scope.submission,
		  "updateCustomActionValue": function(cav) {
		   	$scope.submission.updateCustomActionValue(cav);
		  }
	  };
		
		SidebarService.addBoxes([
		    $scope.activeDocumentBox,
		    $scope.submissionStatusBox,
		    $scope.customActionsBox
		]);

	});

});
