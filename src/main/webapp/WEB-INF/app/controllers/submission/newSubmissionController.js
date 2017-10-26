vireo.controller('NewSubmissionController', function ($controller, $location, $q, $scope, OrganizationRepo, StudentSubmissionRepo, ManagedConfigurationRepo, SubmissionStates) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.organizations = OrganizationRepo.getAll();

    $scope.configuration = ManagedConfigurationRepo.getAll();

    $scope.studentSubmissions = StudentSubmissionRepo.getAll();

    $scope.ready = false;

    $q.all([OrganizationRepo.ready(), ManagedConfigurationRepo.getAll(), StudentSubmissionRepo.getAll()]).then(function () {

        $scope.ready = true;

        $scope.getSelectedOrganization = function () {
            return OrganizationRepo.getSelectedOrganization();
        };

        $scope.setSelectedOrganization = function (organization) {
            OrganizationRepo.setSelectedOrganization(organization);
        };

        $scope.hasSubmission = function () {
            var hasSubmission = false;
            var selectedOrganization = OrganizationRepo.getSelectedOrganization();
            for (var i in $scope.studentSubmissions) {
                var submission = $scope.studentSubmissions[i];
                if (submission.organization.id === selectedOrganization.id) {
                    hasSubmission = true;
                    break;
                }
            }
            return hasSubmission;
        };

        $scope.gotoSubmission = function () {
            var selectedOrganization = OrganizationRepo.getSelectedOrganization();
            for (var i in $scope.studentSubmissions) {
                var submission = $scope.studentSubmissions[i];
                if (submission.organization.id === selectedOrganization.id) {
                    if (submission.submissionStatus.submissionState === SubmissionStates.IN_PROGRESS) {
                        $location.path("/submission/" + submission.id);
                    } else {
                        $location.path("/submission/" + submission.id + "/view");
                    }
                }
            }
        };

        $scope.createSubmission = function () {
            StudentSubmissionRepo.create({
                'organizationId': $scope.getSelectedOrganization().id
            }).then(function (response) {
                var obj = angular.fromJson(response.body);
                if (obj.meta.status === 'SUCCESS') {
                    var submission = obj.payload.Submission;
                    StudentSubmissionRepo.add(submission);
                    $location.path("/submission/" + submission.id);
                }
            });
        };

    });

});
