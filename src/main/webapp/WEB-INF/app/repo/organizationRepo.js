vireo.repo("OrganizationRepo", function OrganizationRepo($q, Organization, RestApi, WsApi) {

    var organizationRepo = this;

    var selectiveListenCallbacks = [];

    var selectedId;

    // additional repo methods and variables

    this.newOrganization = {};

    this.ready().then(function () {
        var organizations = organizationRepo.getAll();
        if (selectedId === undefined && organizations.length > 0) {
            organizationRepo.setSelectedOrganization(organizations[0]);
        }
    });

    this.create = function (organization, parentOrganization) {
        organizationRepo.clearValidationResults();
        angular.extend(this.mapping.create, {
            'method': 'create/' + parentOrganization.id,
            'data': organization
        });
        var promise = WsApi.fetch(this.mapping.create);
        promise.then(function (res) {
            var resObj = angular.fromJson(res.body);
            if (resObj.meta.status === "INVALID") {
                angular.extend(organizationRepo, resObj.payload);
            }
        });
        return promise;
    };

    this.resetNewOrganization = function () {
        for (var key in this.newOrganization) {
            if (key != 'category' && key != 'parent') {
                delete this.newOrganization[key];
            }
        }
        return this.newOrganization;
    };

    this.getNewOrganization = function () {
        return this.newOrganization;
    };

    this.getSelectedOrganization = function () {
        return organizationRepo.findById(selectedId);
    };

    this.setSelectedOrganization = function (organization, fetchWorkflow, ignoreOrgCache) {
        selectedId = organization.id;
        this.lazyFetch(organization.id, fetchWorkflow, false, ignoreOrgCache).then(function (fetchedOrg) {
            angular.extend(organization, fetchedOrg);
            var childOrgs = {};
            for (var i in organization.childrenOrganizations) {
                childOrgs[i] = organizationRepo.findById(organization.childrenOrganizations[i].id);
            }
            angular.extend(organization.childrenOrganizations, childOrgs);
        });
        return organizationRepo.getSelectedOrganization();
    };

    this.lazyFetch = function (orgId, fetchWorkflow, ignoreWorkflowCache, ignoreOrgCache) {
        var orgDefer = $q.defer();
        var cachedOrg = ignoreOrgCache ? undefined : organizationRepo.findById(orgId);
        if (cachedOrg !== undefined) {
            if (fetchWorkflow) {
                if (ignoreWorkflowCache || (cachedOrg.aggregateWorkflowSteps.length > 0 && typeof cachedOrg.aggregateWorkflowSteps[0] === 'number')) {
                    fetchAggregateWorkflow(cachedOrg, orgDefer);
                } else {
                    orgDefer.resolve(cachedOrg);
                }
            } else {
                orgDefer.resolve(cachedOrg);
            }
        } else {
            angular.extend(this.mapping.get, {
                'method': 'get/' + orgId
            });
            var orgPromise = WsApi.fetch(this.mapping.get);
            orgPromise.then(function (res) {
                var resObj = angular.fromJson(res.body);
                var fetchedOrg = new Organization(resObj.payload.Organization);
                organizationRepo.add(fetchedOrg);
                fetchAggregateWorkflow(fetchedOrg, orgDefer);
            });
        }
        return orgDefer.promise;
    };

    this.getChildren = function (id) {
        organizationRepo.clearValidationResults();
        angular.extend(this.mapping.children, {
            'method': 'get-children/' + id
        });
        var promise = WsApi.fetch(this.mapping.children);
        promise.then(function (res) {
            var resObj = angular.fromJson(res.body);
            if (resObj.meta.status === "INVALID") {
                angular.extend(organizationRepo, resObj.payload);
                console.log(organizationRepo);
            }
        });
        return promise;
    };

    this.addWorkflowStep = function (workflowStep) {
        organizationRepo.clearValidationResults();
        angular.extend(this.mapping.addWorkflowStep, {
            'method': this.getSelectedOrganization().id + '/create-workflow-step',
            'data': workflowStep
        });
        var promise = WsApi.fetch(this.mapping.addWorkflowStep);
        promise.then(function (res) {
            var resObj = angular.fromJson(res.body);
            if (resObj.meta.status === "INVALID") {
                angular.extend(organizationRepo, resObj.payload);
            }
        });
        return promise;
    };

    this.restoreDefaults = function (organization) {
        angular.extend(this.mapping.restoreDefaults, {
            'data': organization
        });
        var promise = RestApi.post(apiMapping.Organization.restoreDefaults);
        promise.then(function (resObj) {
            if (resObj && resObj.meta.status === "INVALID") {
                angular.extend(organizationRepo, resObj.payload);
            }
        });
        return promise;
    };

    this.updateWorkflowStep = function (workflowStep) {
        organizationRepo.clearValidationResults();
        angular.extend(this.mapping.updateWorkflowStep, {
            'method': this.getSelectedOrganization().id + '/update-workflow-step',
            'data': workflowStep
        });
        var promise = RestApi.post(this.mapping.updateWorkflowStep);
        promise.then(function (resObj) {
            if (resObj.meta.status === "INVALID") {
                angular.extend(organizationRepo, resObj.payload);
            }
        });
        return promise;
    };

    this.deleteWorkflowStep = function (workflowStep) {
        organizationRepo.clearValidationResults();
        angular.extend(this.mapping.deleteWorkflowStep, {
            'method': this.getSelectedOrganization().id + '/delete-workflow-step',
            'data': workflowStep
        });
        var promise = RestApi.post(this.mapping.deleteWorkflowStep);
        promise.then(function (resObj) {
            if (resObj.meta.status === "INVALID") {
                angular.extend(organizationRepo, resObj.payload);
            }
        });
        return promise;
    };

    this.reorderWorkflowStep = function (upOrDown, workflowStepID) {
        organizationRepo.clearValidationResults();
        angular.extend(this.mapping.reorderWorkflowStep, {
            'method': this.getSelectedOrganization().id + '/shift-workflow-step-' + upOrDown + '/' + workflowStepID
        });
        var promise = WsApi.fetch(this.mapping.reorderWorkflowStep);
        promise.then(function (res) {
            var resObj = angular.fromJson(res.body);
            if (resObj.meta.status === "INVALID") {
                angular.extend(organizationRepo, resObj.payload);
            }
        });
        return promise;
    };

    var extendWithOverwrite = function (targetObj, srcObj) {
        var srcKeys = Object.keys(srcObj);
        angular.forEach(srcKeys, function (key) {
            targetObj[key] = srcObj[key];
        });

        var targetKeys = Object.keys(targetObj);
        angular.forEach(targetKeys, function (key) {
            if (typeof srcObj[key] === undefined) {
                delete targetObj[key];
            }
        });
    };

    var fetchAggregateWorkflow = function (org, defer) {
        angular.extend(organizationRepo.mapping.workflow, {
            'method': org.id + '/workflow'
        });
        var workflowStepsPromise = WsApi.fetch(organizationRepo.mapping.workflow);
        workflowStepsPromise.then(function (response) {
            var aggregateWorkflowSteps = angular.fromJson(response.body).payload.PersistentList;
            if (aggregateWorkflowSteps !== undefined) {
                org.extend({
                    aggregateWorkflowSteps: aggregateWorkflowSteps
                });
            }
            defer.resolve(org);
        });
    };

    return this;

});
