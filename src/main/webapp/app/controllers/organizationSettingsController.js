vireo.controller('OrganizationSettingsController', function ($controller, $scope, $timeout, $q, AccordionService, Organization, OrganizationRepo, SidebarService) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.organizationRepo = OrganizationRepo;

    SidebarService.addBox({
        "title": "Create Organization",
        "viewUrl": "views/sideboxes/organization.html"
    });

    $scope.organizations = [];

    $scope.activeManagementPane = 'edit';

    $scope.newOrganization = OrganizationRepo.getNewOrganization();

    $scope.selectedOrganization;

    $scope.ready = false;
    $scope.loadingOrganization = false;

    $scope.getSelectedOrganizationId = function () {
        if (!!$scope.selectedOrganization && !!$scope.selectedOrganization.id) {
            return $scope.selectedOrganization.id;
        }
    };

    $scope.getSelectedOrganization = function () {
        return $scope.selectedOrganization;
    };

    $scope.getSelectedOrganizationName = function () {
        if ($scope.getSelectedOrganizationId()) {
            return $scope.selectedOrganization.name;
        }
    };

    $scope.getSelectedOrganizationEmailWorkflowRules = function () {
        if ($scope.getSelectedOrganizationId()) {
            return ($scope.selectedOrganization?.emailWorkflowRules || [])
                .concat($scope.selectedOrganization?.emailWorkflowRulesByAction || []);
        }
    };

    $scope.getSelectedOrganizationValidations = function () {
        if ($scope.getSelectedOrganizationId()) {
            return $scope.selectedOrganization.getValidations;
        }
    };

    $scope.getSelectedOrganizationValidationResults = function () {
        if ($scope.getSelectedOrganizationId()) {
            return $scope.selectedOrganization.getValidationResults;
        }
    };

    $scope.getSelectedOrganizationAcceptsSubmissions = function () {
        if ($scope.getSelectedOrganizationId()) {
            return $scope.selectedOrganization.acceptsSubmissions;
        }
    };

    $scope.getSelectedOrganizationAggregateWorkflowSteps = function () {
        if ($scope.getSelectedOrganizationId()) {
            return $scope.selectedOrganization.aggregateWorkflowSteps;
        }
    };

    $scope.getSelectedOrganizationOriginalWorkflowSteps = function () {
        if ($scope.getSelectedOrganizationId()) {
            return $scope.selectedOrganization.originalWorkflowSteps;
        }
    };

    $scope.setSelectedOrganization = function (organization) {

        // Do not select when in the process of loading.
        if ($scope.loadingOrganization) {
            return;
        }

        const focusSelected = () => {
            $timeout(function () {
                const selectedElement = document.getElementById(`organization-${$scope.selectedOrganization.id}`);

                if (selectedElement && selectedElement !== document.activeElement) {
                    selectedElement.focus();
                }
            });
        };

        $scope.loadingOrganization = true;

        var existingSelected = $scope.selectedOrganization;

        if (!!organization && !!organization.id) {
            if (!organization.complete && !organization.shallow || organization.$dirty) {
                OrganizationRepo.getById(organization.id, 'shallow').then(function (response) {
                    if (!!response && !!response.id) {
                        var org = response;
                        var i;
                        org.complete = false;
                        org.shallow = true;
                        org.tree = false;

                        $scope.newOrganization.parent = org;
                        $scope.selectedOrganization = org;

                        for (i = 0; i < $scope.organizations.length; i++) {
                            if ($scope.organizations[i].id === org.id) {
                                $scope.organizations[i] = org;

                                break;
                            }
                        }

                        if (i == $scope.organizations.length) {
                            $scope.organizations.push(org);
                        }

                        if (!!org.parentOrganization) {
                            for (i = 0; i < $scope.organizations.length; i++) {
                                if ($scope.organizations[i].id === org.parentOrganization) {
                                    if (!!$scope.organizations[i].childrenOrganizations) {
                                        for (var j = 0; j < $scope.organizations[i].childrenOrganizations.length; j++) {
                                            if ($scope.organizations[i].childrenOrganizations[j].id === org.id) {
                                                $scope.organizations[i].childrenOrganizations[j] = org;

                                                break;
                                            }
                                        }
                                    }

                                    break;
                                }
                            }
                        }

                        OrganizationRepo.setSelectedOrganization(org);

                        if (!existingSelected || existingSelected.id !== organization.id) {
                            AccordionService.closeAll();
                        }

                        focusSelected();
                    }

                    $scope.setDeleteDisabled();
                    $scope.loadingOrganization = false;
                }).catch(function(reason) {
                    if (!!reason) console.error(reason);

                    $scope.setDeleteDisabled();
                    $scope.loadingOrganization = false;
                });
            } else {
                $scope.selectedOrganization = organization;
                $scope.setDeleteDisabled();
                $scope.loadingOrganization = false;

                if (!existingSelected || existingSelected.id !== organization.id) {
                    AccordionService.closeAll();
                }

                focusSelected();
            }
        } else {
            $scope.selectedOrganization = undefined;
            $scope.newOrganization.parent = undefined;
            $scope.deleteDisabled = true;

            AccordionService.closeAll();
            $scope.loadingOrganization = false;
        }
    };

    $scope.activateManagementPane = function (pane) {
        $scope.activeManagementPane = pane;
    };

    $scope.managementPaneIsActive = function (pane) {
        return ($scope.activeManagementPane === pane);
    };

    $scope.setDeleteDisabled = function () {
        if (!!$scope.selectedOrganization && !!$scope.selectedOrganization.id) {
            OrganizationRepo.countSubmissions($scope.selectedOrganization.id).then(function (res) {
                $scope.deleteDisabled = res > 0;
            });
        }
    };

    $scope.initializeOrganizationTree = function (orgs) {
        if (!!orgs) {
            var initializeChildren = function (parent) {
                for (var i = 0; i < parent.childrenOrganizations.length; i++) {
                    parent.childrenOrganizations[i] = new Organization(parent.childrenOrganizations[i]);
                    parent.childrenOrganizations[i].complete = false;
                    parent.childrenOrganizations[i].shallow = false;
                    parent.childrenOrganizations[i].tree = true;

                    if (!!parent.childrenOrganizations[i].childrenOrganizations) {
                        initializeChildren(parent.childrenOrganizations[i]);
                    }
                }
            };

            for (var i = 0; i < orgs.length; i++) {
                orgs[i] = new Organization(orgs[i]);
                orgs[i].complete = false;
                orgs[i].shallow = false;
                orgs[i].tree = true;

                if (!!orgs[i].childrenOrganizations) {
                    initializeChildren(orgs[i]);
                }
            }
        }
    };

    $scope.rebuildOrganizationTree = function () {
        return $q(function (resolve, reject) {
            OrganizationRepo.getAllSpecific('tree').then(function (orgs) {
                $scope.organizations.length = 0;

                if (!!orgs && orgs.length > 0) {
                    $scope.initializeOrganizationTree(orgs);
                }

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
            $scope.initializeOrganizationTree(orgs);
            $scope.newOrganization.parent = orgs[0];
            $scope.setSelectedOrganization(orgs[0]);

            angular.extend($scope.organizations, orgs);
        }

        $scope.ready = true;
    });

});
