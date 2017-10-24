vireo.controller('NewSubmissionController', function($controller, $location, $q, $scope, OrganizationRepo, StudentSubmissionRepo, ManagedConfigurationRepo, SubmissionStates) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    $scope.organizations = OrganizationRepo.getAll();

    $scope.selectedOrganization = OrganizationRepo.getSelectedOrganization();

    $scope.configuration = ManagedConfigurationRepo.getAll();

    var studentSubmissions = StudentSubmissionRepo.getAll();

    $scope.getSelectedOrganization = function() {
        return $scope.selectedOrganization;
    };

    $scope.setSelectedOrganization = function(organization) {
        OrganizationRepo.setSelectedOrganization(organization);
    };

    $scope.hasSubmission = function(organization) {
        var hasSubmission = false;
        for (var i in studentSubmissions) {
            var submission = studentSubmissions[i];
            if (submission.organization.id === organization.id) {
                hasSubmission = true;
                break;
            }
        }
        return hasSubmission;
    };

    $scope.gotoSubmission = function(organization) {
        for (var i in studentSubmissions) {
            var submission = studentSubmissions[i];
            if (submission.organization.id === organization.id) {
                if (submission.submissionStatus.submissionState === SubmissionStates.IN_PROGRESS) {
                    $location.path("/submission/" + submission.id);
                } else {
                    $location.path("/submission/" + submission.id + "/view");
                }
            }
        }
    };

    $scope.createSubmission = function() {
        StudentSubmissionRepo.create({'organizationId': $scope.getSelectedOrganization().id}).then(function(response) {
            var obj = angular.fromJson(response.body);
            if (obj.meta.status === 'SUCCESS') {
                var submission = obj.payload.Submission;
                StudentSubmissionRepo.add(submission);
                $location.path("/submission/" + submission.id);
            }
        });
    };

});
