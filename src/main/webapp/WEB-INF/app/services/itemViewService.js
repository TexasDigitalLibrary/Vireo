vireo.service("ItemViewService", function($q, SubmissionRepo) {

	var ItemViewService = this;
	
	ItemViewService.setSelectedSubmission = function(submission) {
		ItemViewService.submission = submission;
	};
	
	ItemViewService.getSelectedSubmission = function() {
		return ItemViewService.submission;
	};
	
	ItemViewService.selectSubmissionById = function(id) {
		return $q(function(resolve) {
			if(ItemViewService.submission !== undefined) {
				resolve(ItemViewService.submission)
			}
			else {
				SubmissionRepo.findSubmissionById(id).then(function(response) {
					resolve(angular.fromJson(response.body).payload.Submission);
				});
			}
		});
	};
	
	ItemViewService.clearSelectedSubmission = function() {
		delete ItemViewService.submission;
	};

	return ItemViewService;
	
});
