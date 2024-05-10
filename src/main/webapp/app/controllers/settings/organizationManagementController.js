vireo.controller("OrganizationManagementController", function ($controller, $location, $q, $route, $scope, $timeout, AccordionService, AlertService, ApiResponseActions, Organization, OrganizationRepo, OrganizationCategoryRepo, WorkflowStepRepo) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.organizationRepo = OrganizationRepo;

    $scope.workflowStepRepo = WorkflowStepRepo;

    $scope.organizationCategories = OrganizationCategoryRepo.getAll();

    $scope.ready = $q.all([OrganizationCategoryRepo.ready()]);

    $scope.forms = {};

    $scope.resetWorkflowSteps = function () {
        $scope.organizationRepo.clearValidationResults();
        for (var key in $scope.forms) {
            if ($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
                $scope.forms[key].$setPristine();
                $scope.forms[key].$setUntouched();
            }
        }
        if ($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
            $scope.modalData.refresh();
        }
        $scope.modalData = {
            overrideable: true
        };
        $scope.closeModal();
    };

    $scope.showOrganizationManagement = function () {
        var selectedId = $scope.getSelectedOrganizationId();
        return !!selectedId && (selectedId !== 1 || (selectedId == 1 && $scope.isAdmin()));
    };

    $scope.updateOrganization = function (organization) {
        $scope.updatingOrganization = true;
        organization.save().then(function () {
            // update the parent scoped selected organization
            $scope.setSelectedOrganization(organization);
            $scope.updatingOrganization = false;
        });
    };

    $scope.deleteOrganization = function (organization) {
        $scope.organizationRepo.deleteById(organization.id).then(function (res) {
            var apiRes = angular.fromJson(res.body);
            if (apiRes.meta.status !== 'INVALID') {
                $scope.closeModal();
                $timeout(function () {
                    AlertService.add(apiRes.meta, 'organization/delete');
                }, 300);
            } else {
                $scope.closeModal();
            }
        });
    };

    $scope.cancelDeleteOrganization = function () {
        $scope.closeModal();
        OrganizationRepo.clearValidationResults();
    };

    $scope.restoreOrganizationDefaults = function (organization) {
        OrganizationRepo.restoreDefaults(organization).then(function (data) {
            if (data.meta.status !== 'INVALID') {
                $scope.closeModal();
                $timeout(function () {
                    AlertService.add(data.meta, 'organization/restore-defaults');
                }, 300);
            }
        });
    };

    $scope.cancelRestoreOrganizationDefaults = function () {
        $scope.closeModal();
        OrganizationRepo.clearValidationResults();
    };

    $scope.addWorkflowStep = function () {
        var name = $scope.modalData.name;
        OrganizationRepo.addWorkflowStep($scope.modalData);
    };

    $scope.deleteWorkflowStep = function (workflowStep) {
        OrganizationRepo.deleteWorkflowStep(workflowStep).then(function (resObj) {
            if (resObj.meta.status === 'SUCCESS') {
                AccordionService.close(workflowStep.name);
            }
        });
    };

    $scope.updateWorkflowStep = function (workflowStep) {
        return OrganizationRepo.updateWorkflowStep(workflowStep);
    };

    $scope.reorderWorkflowStepUp = function (workflowStepID) {
        $scope.getSelectedOrganization().$dirty = true;
        AccordionService.closeAll();
        return OrganizationRepo.reorderWorkflowSteps("up", workflowStepID);
    };

    $scope.reorderWorkflowStepDown = function (workflowStepID) {
        $scope.getSelectedOrganization().$dirty = true;
        AccordionService.closeAll();
        return OrganizationRepo.reorderWorkflowSteps("down", workflowStepID);
    };

    $scope.openConfirmDeleteModal = function (step) {
        $scope.openModal('#workflow-step-delete-confirm-' + step.id);
    };

    $scope.resetManageOrganization = function (organization) {
        if (!!organization && !!organization.id) {
            organization.complete = false;

            organization.clearValidationResults();
            organization.refresh();
        }
    };

    $scope.ready.then(function () {
        $scope.resetWorkflowSteps();

        OrganizationRepo.listen(function (res) {
            var resObj = !!res && !!res.body ? angular.fromJson(res.body) : {};
            if (!!resObj && !!resObj.meta && !!resObj.meta.status && resObj.meta.status === 'SUCCESS') {
                $scope.resetWorkflowSteps();
            }
        });

        OrganizationRepo.listen(ApiResponseActions.READ, function (res) {
            var resObj = !!res && !!res.body ? angular.fromJson(res.body) : {};
            if (!!resObj && !!resObj.meta && !!resObj.meta.status && resObj.meta.status === 'SUCCESS') {
              $scope.resetManageOrganization($scope.getSelectedOrganization());
            }
        });
    });

    $scope.acceptsSubmissions = [{
        "true": "Yes"
    }, {
        "false": "No"
    }];

});
