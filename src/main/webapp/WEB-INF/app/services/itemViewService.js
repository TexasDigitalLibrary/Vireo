vireo.service("ItemViewService", function($location) {

	var ItemViewService = this;

	ItemViewService.setSelectedSubmission = function(submission) {
		ItemViewService.selectedSubmission = submission;
		$location.path("/admin/view");
	};
	
	ItemViewService.getSelectedSubmission = function(submission) {
		return ItemViewService.selectedSubmission;
	};
	
	ItemViewService.clearSelectedSubmission = function() {
		delete ItemViewService.selectedSubmission;
	};
	
	ItemViewService.submissionSelected = function() {
		return ItemViewService.selectedSubmission !== undefined;
	};

	return ItemViewService;
	
});