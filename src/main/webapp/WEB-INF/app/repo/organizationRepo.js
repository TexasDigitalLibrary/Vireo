vireo.repo("OrganizationRepo", function OrganizationRepo($q, Organization, WsApi) {

    var organizationRepo = this;

    var selectedOrganization = new Organization({});

    var selectiveListenCallbacks = [];

    // additional repo methods and variables

    this.newOrganization = {};

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
            var aggregateWorkflowSteps = JSON.parse(response.body).payload.PersistentList;
            if (aggregateWorkflowSteps !== undefined) {
                org.aggregateWorkflowSteps = aggregateWorkflowSteps;
            }
            defer.resolve(org);
        });
    };

    this.create = function (organization, parentOrganization) {
        organizationRepo.clearValidationResults();
        angular.extend(this.mapping.create, {
            'method': 'create/' + parentOrganization.id,
            'data': organization
        });
        var promise = WsApi.fetch(this.mapping.create);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.type == "INVALID") {
                angular.extend(organizationRepo, angular.fromJson(res.body).payload);
            }
        });
        return promise;
    };

    this.selectiveListen = function () {
        WsApi.listen(this.mapping.selectiveListen).then(null, null, function (rawApiResponse) {
            var broadcastedOrg = new Organization(JSON.parse(rawApiResponse.body).payload.Organization);
            if (broadcastedOrg.id == selectedOrganization.id) {
                organizationRepo.setSelectedOrganization(broadcastedOrg, true, true);
                angular.forEach(selectiveListenCallbacks, function (cb) {
                    cb(broadcastedOrg);
                });
            }
        });
    };

    this.selectiveListen();

    this.listenSelectively = function (cb) {
        selectiveListenCallbacks.push(cb);
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
        return selectedOrganization;
    };

    // TODO: simplify
    this.setSelectedOrganization = function (organization, fetchWorkflow, ignoreOrgCache) {
        this.lazyFetch(organization.id, fetchWorkflow, false, ignoreOrgCache).then(function (fetchedOrg) {
            extendWithOverwrite(selectedOrganization, fetchedOrg);
        });
        return selectedOrganization;
    };

    // TODO: simplify
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
            orgPromise.then(function (rawApiResponse) {
                var fetchedOrg = new Organization(JSON.parse(rawApiResponse.body).payload.Organization);

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
            if (angular.fromJson(res.body).meta.type == "INVALID") {
                angular.extend(organizationRepo, angular.fromJson(res.body).payload);
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
            if (angular.fromJson(res.body).meta.type == "INVALID") {
                angular.extend(organizationRepo, angular.fromJson(res.body).payload);
                console.log(organizationRepo);
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
        var promise = WsApi.fetch(this.mapping.updateWorkflowStep);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.type == "INVALID") {
                angular.extend(organizationRepo, angular.fromJson(res.body).payload);
                console.log(organizationRepo);
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
        var promise = WsApi.fetch(this.mapping.deleteWorkflowStep);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.type == "INVALID") {
                angular.extend(organizationRepo, angular.fromJson(res.body).payload);
                console.log(organizationRepo);
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
            if (angular.fromJson(res.body).meta.type == "INVALID") {
                angular.extend(organizationRepo, angular.fromJson(res.body).payload);
                console.log(organizationRepo);
            }
        });
        return promise;
    };

    return this;

});
