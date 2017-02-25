vireo.controller("AdvisorReviewController", function($controller, $scope, $routeParams, AdvisorSubmissionRepo, AdvisorSubmission, InputTypes, Submission) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.InputType = InputTypes;

    $scope.approval = {};

	$scope.advisorSubmissionRepoReady = false;
	AdvisorSubmissionRepo.findSubmissionByhash($routeParams.advisorAccessHash).then(function(data) {
		$scope.advisorSubmissionRepoReady = true;
		$scope.submission = new AdvisorSubmission(angular.fromJson(data.body).payload.Submission);
        console.log($scope.submission);
	});

	$scope.required = function(aggregateFieldProfile) {
		return !aggregateFieldProfile.optional;
	};

	$scope.predicateMatch = function(fv) {
		return function(aggregateFieldProfile) {
	        return aggregateFieldProfile.fieldPredicate.id == fv.fieldPredicate.id;
	    };
	};

    $scope.addComment = function(approval) {
        $scope.updatingApproval = true;
        $scope.submission.updateAdvisorApproval(approval).then(function(res) {
        	$scope.submission = new Submission(angular.fromJson(res.body).payload.Submission);
            $scope.approval.message="";
            $scope.approval.clearApproveApplication=false;
            $scope.approval.clearApproveEmbargo=false
            $scope.approval.approveEmbargo=undefined;
            $scope.submission.approveApplication=undefined;
            $scope.updatingApproval = false;
        });
    };

    $scope.disableCheck = function(approval) {
    	var dissabled = true;
    	//((approval.approveEmbargo===undefined&&approval.approveApplication===undefined)&&!approval.message)||(approval.approveApplication===false&&!approval.message)||(approval.approveEmbargo===false&&!approval.message)
    	
    	if(approval.approveEmbargo && (approval.approveApplication===undefined||approval.approveApplication)) {
    		dissabled = false;
    	}

    	if(approval.approveApplication && (approval.approveEmbargo===undefined||approval.approveEmbargo)) {
    		dissabled = false;
    	}

    	if(approval.message) {
    		dissabled = false;
    	} 

    	return dissabled;

    }

});
