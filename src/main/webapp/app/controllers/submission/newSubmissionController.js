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

        $scope.hasSubmission = function (organization) {
            var hasSubmission = false;
            for (var i in $scope.studentSubmissions) {
                var submission = $scope.studentSubmissions[i];
                if (submission.organization.id === organization.id) {
                    hasSubmission = true;
                    break;
                }
            }
            return hasSubmission;
        };

        $scope.gotoSubmission = function (organization) {
            for (var i in $scope.studentSubmissions) {
                var submission = $scope.studentSubmissions[i];
                if (submission.organization.id === organization.id) {
                    if (submission.submissionStatus.submissionState === SubmissionStates.IN_PROGRESS) {
                        $location.path("/submission/" + submission.id);
                    } else {
                        $location.path("/submission/" + submission.id + "/view");
                    }
                }
            }
        };

        $scope.createSubmission = function () {
            $scope.creatingSubmission = true;
            StudentSubmissionRepo.create({
                'organizationId': $scope.getSelectedOrganization().id
            }).then(function (response) {
                $scope.creatingSubmission = false;
                var apiRes = angular.fromJson(response.body);
                if (apiRes.meta.status === 'SUCCESS') {
                    var submission = angular.isDefined(apiRes.payload.SimpleSubmission) ? apiRes.payload.SimpleSubmission : apiRes.payload.Submission;
                    StudentSubmissionRepo.add(submission);
                    $location.path("/submission/" + submission.id);
                }
            });
        };

    });

});
