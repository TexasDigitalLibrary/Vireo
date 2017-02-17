vireo.service("ItemViewService", function($q, Submission, SubmissionRepo) {

  var ItemViewService = this;

  ItemViewService.setSelectedSubmission = function(submission) {
    ItemViewService.submission = submission;
  };

  ItemViewService.selectSubmission = function(id) {
    ItemViewService.setSelectedSubmission(SubmissionRepo.findById(id));
    return ItemViewService.submission;
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
          var submission = angular.fromJson(response.body).payload.Submission;
          ItemViewService.setSelectedSubmission(submission);
          resolve(ItemViewService.getSelectedSubmission());
        });
      }
    });
  };

  ItemViewService.clearSelectedSubmission = function() {
    delete ItemViewService.submission;
  };

  return ItemViewService;

});
