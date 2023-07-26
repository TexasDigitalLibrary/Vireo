vireo.controller('NewSubmissionController', function ($controller, $location, $q, $scope, Organization, OrganizationRepo, StudentSubmissionRepo, ManagedConfigurationRepo, Submission, SubmissionStates) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.organizations = [];

    $scope.configuration = ManagedConfigurationRepo.getAll();

    $scope.studentSubmissions = StudentSubmissionRepo.getAll();

    $scope.selectedOrganization = new Organization({});

    $scope.ready = false;
    $scope.loadingOrganization = false;

    $scope.getSelectedOrganizationId = function () {
        if (!!$scope.selectedOrganization && !!$scope.selectedOrganization.id) {
            return $scope.selectedOrganization.id;
        }
    };

    $scope.getSelectedOrganizationName = function () {
        if ($scope.getSelectedOrganizationId()) {
            return $scope.selectedOrganization.name;
        }
    };

    $scope.getSelectedOrganizationAcceptsSubmissions = function () {
        if (!!$scope.getSelectedOrganizationId()) {
            return $scope.selectedOrganization.acceptsSubmissions;
        }
    };

    $scope.getSelectedOrganization = function () {
        return $scope.selectedOrganization;
    };

    $scope.setSelectedOrganization = function (organization) {
        $scope.selectedOrganization = new Organization(organization);

        if (!!organization && !!organization.id) {
            OrganizationRepo.setSelectedOrganization(organization);
        }
    };

    $scope.hasSubmission = function (organization) {
        var hasSubmission = false;
        for (var i in $scope.studentSubmissions) {
            var submission = $scope.studentSubmissions[i];
            if (!!organization && submission.organization.id === organization.id) {
                hasSubmission = true;
                break;
            }
        }
        return hasSubmission;
    };

    $scope.gotoSubmission = function (organization) {
        for (var i in $scope.studentSubmissions) {
            var submission = $scope.studentSubmissions[i];
            if (!!organization && submission.organization.id === organization.id) {
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

        var sub = {
            'organizationId': $scope.getSelectedOrganizationId()
        };

        StudentSubmissionRepo.create(sub).then(function (response) {
            $scope.creatingSubmission = false;

            var apiRes = angular.fromJson(response.body);
            if (apiRes.meta.status === 'SUCCESS') {
                var submission = new Submission(apiRes.payload.Submission);
                StudentSubmissionRepo.add(submission);
                $location.path("/submission/" + submission.id);
            }
        });
    };

    $scope.rebuildOrganizationTree = function () {
        return $q(function (resolve, reject) {
            OrganizationRepo.getAllSpecific('tree').then(function (orgs) {
                $scope.organizations.length = 0;
                angular.extend($scope.organizations, orgs);

                resolve();
            }).catch(function(reason) {
                reject(reason);
            });
        });
    };

    OrganizationRepo.getAllSpecific('tree').then(function (orgs) {
        $scope.organizations.length = 0;

        if (!!orgs && orgs.length > 0) {
            $scope.setSelectedOrganization(new Organization(orgs[0]));

            angular.extend($scope.organizations, orgs);
        }

        $scope.ready = true;
    });

});
