vireo.repo("OrganizationRepo", function OrganizationRepo($q, Organization, RestApi, WsApi) {

    var organizationRepo = this;

    var selectiveListenCallbacks = [];

    var selectedId;
    var selectedOrganization;

    // additional repo methods and variables

    this.newOrganization = {};

    /**
     * Get the organization using a specific method, such as "tree" or "shallow".
     *
     * This does not create a new Organization() model for each organization.
     *
     * This returns a promise.
     */
    this.getAllSpecific = function (specific) {
        var endpoint = angular.copy(this.mapping.all);

        if (specific == 'tree') {
            endpoint.method = 'all/tree';
        } else if (specific == 'shallow') {
            endpoint.method = 'all/shallow';
        }

        return $q(function (resolve, reject) {
            WsApi.fetch(endpoint).then(function (res) {
                var apiRes = angular.fromJson(res.body);

                if (apiRes.meta.status === 'SUCCESS') {
                    var keys = Object.keys(apiRes.payload);

                    if (specific == 'tree') {
                        apiRes.payload[keys[0]].shallow = false;
                        apiRes.payload[keys[0]].tree = true;
                    } else if (specific == 'shallow') {
                        apiRes.payload[keys[0]].shallow = true;
                        apiRes.payload[keys[0]].tree = false;
                    }

                    if (keys.length) {
                        resolve(apiRes.payload[keys[0]]);
                    } else {
                        reject(apiRes.meta);
                    }
                } else {
                    reject(apiRes.meta);
                }
            });
        }.bind(this));
    };

    this.create = function (organization, parentOrganization) {
        organizationRepo.clearValidationResults();
        angular.extend(this.mapping.create, {
            'method': 'create/' + parentOrganization.id,
            'data': organization
        });
        var promise = WsApi.fetch(this.mapping.create);
        promise.then(function (res) {
            var apiRes = angular.fromJson(res.body);
            if (apiRes.meta.status === "INVALID") {
                angular.extend(organizationRepo, apiRes.payload);
            }
        });
        return promise;
    };

    this.resetNewOrganization = function () {
        for (var key in this.newOrganization) {
            if (key !== 'category' && key !== 'parent') {
                delete this.newOrganization[key];
            }
        }
        return this.newOrganization;
    };

    this.getNewOrganization = function () {
        return this.newOrganization;
    };

    this.getSelectedOrganization = function () {
        var organization = organizationRepo.findById(selectedId);

        if (!!organization) {
            selectedOrganization = selectedOrganization;
        }

        return organization;
    };

    this.getSelectedOrganizationId = function () {
        return selectedId;
    };

    /**
     * Perform the actual request to the back-end to get the organization by the given ID.
     *
     * If specific is passed, then use the given specific variation when querying the back-end.
     *
     * This returns a promise.
     */
    this.getById = function (id, specific) {
        var extra = '';
        var endpoint = angular.copy(this.mapping.get);

        if (specific === 'shallow') {
            extra = '/shallow';
        } else if (specific === 'tree') {
            extra = '/tree';
        }

        endpoint.method = 'get/' + id + extra;

        return $q(function (resolve, reject) {
            WsApi.fetch(endpoint).then(function (res) {
                var apiRes = angular.fromJson(res.body);

                if (apiRes.meta.status === 'SUCCESS') {
                    var keys = Object.keys(apiRes.payload);

                    if (keys.length) {
                        var organization = new Organization(apiRes.payload[keys[0]]);
                        organization.shallow = false;
                        organization.tree = false;

                        if (specific === 'shallow') {
                            organization.shallow = true;
                        } else if (specific === 'tree') {
                            organization.tree = true;
                        }

                        resolve(organization, specific);
                    } else {
                        reject(apiRes.meta);
                    }
                } else {
                    reject(apiRes.meta);
                }
            });
        }.bind(this));
    };

    this.setSelectedOrganization = function (organization) {
        if (!!organization && !!organization.id) {
            selectedOrganization = organization;
            selectedId = organization.id;
        } else {
            selectedOrganization = undefined;
            selectedId = undefined;
        }
    };

    this.addWorkflowStep = function (workflowStep) {
        organizationRepo.clearValidationResults();
        angular.extend(this.mapping.addWorkflowStep, {
            'method': selectedId + '/create-workflow-step',
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
            'method': selectedId + '/update-workflow-step',
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
            'method': selectedId + '/delete-workflow-step',
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

    this.deleteById = function (organizationId) {
        organizationRepo.clearValidationResults();

        var endpoint = angular.copy(this.mapping.remove);
        endpoint.method += '/' + organizationId;
        endpoint.data = ''; // Provide empty data to force this to be a POST.

        return WsApi.fetch(endpoint);
    };

    this.reorderWorkflowSteps = function (upOrDown, workflowStepID) {
        organizationRepo.clearValidationResults();
        angular.extend(this.mapping.reorderWorkflowStep, {
            'method': selectedId + '/shift-workflow-step-' + upOrDown + '/' + workflowStepID
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

    this.countSubmissions = function (orgId) {
        angular.extend(this.mapping.countSubmissions, {
            'method': orgId + '/count-submissions'
        });
        var defer = $q(function (resolve, reject) {
            WsApi.fetch(this.mapping.countSubmissions).then(function (res) {
                var resObj = angular.fromJson(res.body);
                if (resObj.meta.status === "SUCCESS") {
                    resolve(resObj.payload.Long);
                } else {
                    reject();
                }
            });
        }.bind(this));
        return defer;
    };

    this.addEmailWorkflowRule = function (organization, templateId, recipient, submissionStatusId) {
        angular.extend(apiMapping.Organization.addEmailWorkflowRule, {
            'method': organization.id + "/add-email-workflow-rule",
            'data': {
                templateId: templateId,
                recipient: recipient,
                submissionStatusId: submissionStatusId
            }
        });

        return WsApi.fetch(apiMapping.Organization.addEmailWorkflowRule);
    };

    this.removeEmailWorkflowRule = function (organization, rule) {
        angular.extend(apiMapping.Organization.removeEmailWorkflowRule, {
            'method': organization.id + "/remove-email-workflow-rule/" + rule.id,
        });

        return WsApi.fetch(apiMapping.Organization.removeEmailWorkflowRule);
    };

    this.editEmailWorkflowRule = function (organization, rule) {
        angular.extend(apiMapping.Organization.editEmailWorkflowRule, {
            'method': organization.id + "/edit-email-workflow-rule/" + rule.id,
            'data': {
                templateId: rule.emailTemplate.id,
                recipient: rule.emailRecipient
            }
        });

        return WsApi.fetch(apiMapping.Organization.editEmailWorkflowRule);
    };

    this.changeEmailWorkflowRuleActivation = function (organization, rule) {
        angular.extend(apiMapping.Organization.changeEmailWorkflowRuleActivation, {
            'method': organization.id + "/change-email-workflow-rule-activation/" + rule.id,
        });

        return WsApi.fetch(apiMapping.Organization.changeEmailWorkflowRuleActivation);
    };

    this.addEmailWorkflowRuleByAction = function (organization, templateId, recipient, action) {
        angular.extend(apiMapping.Organization.addEmailWorkflowRule, {
            'method': organization.id + "/add-email-workflow-rule-by-action",
            'data': {
                templateId: templateId,
                recipient: recipient,
                action: action
            }
        });

        return WsApi.fetch(apiMapping.Organization.addEmailWorkflowRule);
    };

    this.removeEmailWorkflowRuleByAction = function (organization, rule) {
        angular.extend(apiMapping.Organization.removeEmailWorkflowRule, {
            'method': organization.id + "/remove-email-workflow-rule-by-action/" + rule.id,
        });

        return WsApi.fetch(apiMapping.Organization.removeEmailWorkflowRule);
    };

    this.editEmailWorkflowRuleByAction = function (organization, rule) {
        angular.extend(apiMapping.Organization.editEmailWorkflowRule, {
            'method': organization.id + "/edit-email-workflow-rule-by-action/" + rule.id,
            'data': {
                templateId: rule.emailTemplate.id,
                recipient: rule.emailRecipient
            }
        });

        return WsApi.fetch(apiMapping.Organization.editEmailWorkflowRule);
    };

    this.changeEmailWorkflowRuleByActionActivation = function (organization, rule) {
        angular.extend(apiMapping.Organization.changeEmailWorkflowRuleActivation, {
            'method': organization.id + "/change-email-workflow-rule-by-action-activation/" + rule.id,
        });

        return WsApi.fetch(apiMapping.Organization.changeEmailWorkflowRuleActivation);
    };

    return this;

});
