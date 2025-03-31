vireo.controller('NewSubmissionController', function ($controller, $location, $q, $scope, $timeout, Organization, OrganizationRepo, StudentSubmissionRepo, ManagedConfigurationRepo, Submission, SubmissionStates) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.organizations = [];

    $scope.configuration = ManagedConfigurationRepo.getAll();

    $scope.studentSubmissions = StudentSubmissionRepo.getAll();

    $scope.selectedOrganization = new Organization({});

    $scope.ready = false;

    $scope.loadingOrganization = false;

    $scope.handleKeyup = function (event, attr, cb, options) {

        // allow for use in nested elements
        event.stopPropagation();

        const callback = () => {
            if (!attr.managed && $scope.getSelectedOrganizationAcceptsSubmissions()) {
                cb($scope.selectedOrganization);
            }
        };

        let next;

        switch (event.which) {
            // enter
            case 13:
            // space
            case 32:
                if (event.which === 13) console.log('enter');
                if (event.which === 32) console.log('space');
                callback();
                break;
            // up
            case 38:
                for (let i = options.length - 1; i >= 0; i--) {
                    if ($scope.selectedOrganization.id === options[i].id) {
                        next = i - 1 >= 0 ? options[i - 1].id : options[options.length - 1].id;
                        break;
                    }
                }
                break;
            // down
            case 40:
                for (let i = 0; i < options.length; i++) {
                    if ($scope.selectedOrganization.id === options[i].id) {
                        next = i + 1 < options.length ? options[i + 1].id : options[0].id;
                        break;
                    }
                }
                break;
            // left
            case 37:
                if ($scope.selectedOrganization.parentOrganization) {
                    next = $scope.selectedOrganization.parentOrganization;
                }
                break;
            // right
            case 39:
                if ($scope.selectedOrganization.childrenOrganizations?.length > 0) {
                    next = $scope.selectedOrganization.childrenOrganizations[0].id;
                }
                break;
            default:
                break;
        }

        if (next) {
            const nextElement = document.getElementById(`organization-${next}`);

            if (nextElement) {
                nextElement.focus();
            }
        }

    };

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
            if (apiRes.meta.status === 'SUCCESS' && !!apiRes.payload.Long) {
                $location.path("/submission/" + apiRes.payload.Long);
            }
        });
    };

    $scope.rebuildOrganizationTree = function () {
        return $q(function (resolve, reject) {
            OrganizationRepo.getAllSpecific('tree').then(function (orgs) {
                $scope.organizations.length = 0;
                angular.extend($scope.organizations, orgs);

                resolve();
            }).catch(function (reason) {
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
