vireo.controller("ItemViewController", function ($controller, $q, $routeParams, $scope, ItemViewService, SidebarService) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	ItemViewService.selectSubmissionById($routeParams.id).then(function(submission) {

		$scope.submission = submission;

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
			console.log($scope.reviewerNotes);
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
		
		
		$scope.documentFieldValues = [];
		
		
		var getFileInfo = function(fieldValue) {
			$scope.submission.fileInfo(fieldValue.value).then(function(data) {
				fieldValue.fileInfo = angular.fromJson(data.body).payload.ObjectNode;
				$scope.documentFieldValues.push(fieldValue);
			});
		}
		
		for(var i in $scope.submission.fieldValues) {
			var fieldValue = $scope.submission.fieldValues[i];
			if(fieldValue.fieldPredicate.documentTypePredicate) {
				getFileInfo(fieldValue);
			}
		}
		
		SidebarService.addBoxes([
		    {
		        "title": "Active Document",
		        "viewUrl": "views/sideboxes/activeDocument.html",
		        "getPrimaryDocumentFileName": function() {
		        	return "Title";
		        },
		        "downloadPrimaryDocument": function() {
		        	console.log('download primary document')
		        },
		        "viewAllFiles": function() {
		        	console.log('view all files')
		        },
		        "uploadNewFile": function() {
		        	console.log('upload new files')
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
