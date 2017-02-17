vireo.repo("AdvisorSubmissionRepo", function AdvisorSubmissionRepo(WsApi) {

  var AdvisorSubmissionRepo = this;

  AdvisorSubmissionRepo.findSubmissionByhash = function(hash) {

    angular.extend(AdvisorSubmissionRepo.mapping.getByHash, {
      'method': 'advisor-review/' + hash
    });

    return WsApi.fetch(AdvisorSubmissionRepo.mapping.getByHash);
  };

  return AdvisorSubmissionRepo;

});
