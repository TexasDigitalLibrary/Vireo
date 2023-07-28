vireo.repo("WorkflowStepRepo", function WorkflowStepRepo(OrganizationRepo, RestApi, WsApi) {

    var workflowStepRepo = this;

    // additional repo methods and variables

    this.addFieldProfile = function (workflowStep, fieldProfile) {
        workflowStepRepo.clearValidationResults();

        fieldProfile.originatingWorkflowStep = workflowStep.id;
        angular.extend(this.mapping.addFieldProfile, {
            'method': OrganizationRepo.getSelectedOrganizationId() + '/' + workflowStep.id + '/add-field-profile',
            'data': fieldProfile
        });
        var promise = RestApi.post(this.mapping.addFieldProfile);
        promise.then(function (res) {
            if (res.meta.status === "INVALID") {
                angular.extend(workflowStepRepo, res.payload);
            }
        });
        return promise;
    };

    this.updateFieldProfile = function (workflowStep, fieldProfile) {
        workflowStepRepo.clearValidationResults();

        fieldProfile.originatingWorkflowStep = workflowStep.id;
        angular.extend(this.mapping.updateFieldProfile, {
            'method': OrganizationRepo.getSelectedOrganizationId() + '/' + workflowStep.id + '/update-field-profile',
            'data': fieldProfile
        });
        var promise = RestApi.post(this.mapping.updateFieldProfile);
        promise.then(function (res) {
            if (res.meta.status === "INVALID") {
                angular.extend(workflowStepRepo, res.payload);
            }
        });
        return promise;
    };

    this.removeFieldProfile = function (workflowStep, fieldProfile) {
        workflowStepRepo.clearValidationResults();

        var endpoint = angular.copy(this.mapping.removeFieldProfile);
        endpoint.method = OrganizationRepo.getSelectedOrganizationId() + '/' + workflowStep.id + '/remove-field-profile/' + fieldProfile.id;
        endpoint.data = ''; // Provide empty data to force this to be a POST

        return WsApi.fetch(endpoint);
    };

    this.reorderFieldProfiles = function (workflowStep, src, dest) {
        workflowStepRepo.clearValidationResults();

        angular.extend(this.mapping.reorderFieldProfile, {
            'method': OrganizationRepo.getSelectedOrganizationId() + '/' + workflowStep.id + '/reorder-field-profiles/' + src + '/' + dest
        });
        var promise = WsApi.fetch(this.mapping.reorderFieldProfile);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(workflowStepRepo, angular.fromJson(res.body).payload);
            }
        });
        return promise;
    };


    this.addNote = function (workflowStep, note) {
        workflowStepRepo.clearValidationResults();

        angular.extend(this.mapping.addNote, {
            'method': OrganizationRepo.getSelectedOrganizationId() + '/' + workflowStep.id + '/add-note',
            'data': note
        });
        var promise = WsApi.fetch(this.mapping.addNote);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(workflowStepRepo, angular.fromJson(res.body).payload);
            }
        });
        return promise;
    };

    this.updateNote = function (workflowStep, note) {
        workflowStepRepo.clearValidationResults();

        angular.extend(this.mapping.updateNote, {
            'method': OrganizationRepo.getSelectedOrganizationId() + '/' + workflowStep.id + '/update-note',
            'data': note
        });
        var promise = WsApi.fetch(this.mapping.updateNote);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(workflowStepRepo, angular.fromJson(res.body).payload);
            }
        });
        return promise;
    };

    this.removeNote = function (workflowStep, note) {
        workflowStepRepo.clearValidationResults();

        angular.extend(this.mapping.removeNote, {
            'method': OrganizationRepo.getSelectedOrganizationId() + '/' + workflowStep.id + '/remove-note',
            'data': note
        });
        var promise = WsApi.fetch(this.mapping.removeNote);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(workflowStepRepo, angular.fromJson(res.body).payload);
            }
        });
        return promise;
    };

    this.reorderNotes = function (workflowStep, src, dest) {
        workflowStepRepo.clearValidationResults();

        angular.extend(this.mapping.reorderNote, {
            'method': OrganizationRepo.getSelectedOrganizationId() + '/' + workflowStep.id + '/reorder-notes/' + src + '/' + dest
        });
        var promise = WsApi.fetch(this.mapping.reorderNote);
        promise.then(function (res) {
            if (angular.fromJson(res.body).meta.status === "INVALID") {
                angular.extend(workflowStepRepo, angular.fromJson(res.body).payload);
            }
        });
        return promise;
    };

    return this;

});
