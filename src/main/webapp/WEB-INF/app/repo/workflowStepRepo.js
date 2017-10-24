vireo.repo("WorkflowStepRepo", function WorkfloStepRepo(OrganizationRepo, RestApi, WsApi) {

	var workflowStepRepo = this;

	// additional repo methods and variables

	this.addFieldProfile = function(workflowStep, fieldProfile) {
		workflowStepRepo.clearValidationResults();
		angular.extend(this.mapping.addFieldProfile, {
			'method': OrganizationRepo.getSelectedOrganization().id + '/' + workflowStep.id + '/add-field-profile',
			'data': fieldProfile
		});
		var promise = RestApi.post(this.mapping.addFieldProfile);
		promise.then(function(res) {
			if(res.meta.status === "INVALID") {
				angular.extend(workflowStepRepo, res.payload);
				console.log(workflowStepRepo);
			}
		});
		return promise;
	};

	this.updateFieldProfile = function(workflowStep, fieldProfile) {
		workflowStepRepo.clearValidationResults();
		angular.extend(this.mapping.updateFieldProfile, {
			'method': OrganizationRepo.getSelectedOrganization().id + '/' + workflowStep.id + '/update-field-profile',
			'data': fieldProfile
		});
		var promise = RestApi.post(this.mapping.updateFieldProfile);
		promise.then(function(res) {
			if(res.meta.status === "INVALID") {
				angular.extend(workflowStepRepo, res.payload);
				console.log(workflowStepRepo);
			}
		});
		return promise;
	};

	this.removeFieldProfile = function(workflowStep, fieldProfile) {
		workflowStepRepo.clearValidationResults();
		angular.extend(this.mapping.removeFieldProfile, {
			'method': OrganizationRepo.getSelectedOrganization().id + '/' + workflowStep.id + '/remove-field-profile',
			'data': fieldProfile
		});
		var promise = WsApi.fetch(this.mapping.removeFieldProfile);
		promise.then(function(res) {
			if(angular.fromJson(res.body).meta.status === "INVALID") {
				angular.extend(workflowStepRepo, angular.fromJson(res.body).payload);
				console.log(workflowStepRepo);
			}
		});
		return promise;
	};

	this.reorderFieldProfile = function(workflowStep, src, dest) {
		workflowStepRepo.clearValidationResults();
		angular.extend(this.mapping.reorderFieldProfile, {
			'method': OrganizationRepo.getSelectedOrganization().id + '/' + workflowStep.id + '/reorder-field-profiles/' + src + '/' + dest
		});
		var promise = WsApi.fetch(this.mapping.reorderFieldProfile);
		promise.then(function(res) {
			if(angular.fromJson(res.body).meta.status === "INVALID") {
				angular.extend(workflowStepRepo, angular.fromJson(res.body).payload);
				console.log(workflowStepRepo);
			}
		});
		return promise;
	};


	this.addNote = function(workflowStep, note) {
		workflowStepRepo.clearValidationResults();
		angular.extend(this.mapping.addNote, {
			'method': OrganizationRepo.getSelectedOrganization().id + '/' + workflowStep.id + '/add-note',
			'data': note
		});
		var promise = WsApi.fetch(this.mapping.addNote);
		promise.then(function(res) {
			if(angular.fromJson(res.body).meta.status === "INVALID") {
				angular.extend(workflowStepRepo, angular.fromJson(res.body).payload);
				console.log(workflowStepRepo);
			}
		});
		return promise;
	};

	this.updateNote = function(workflowStep, note) {
		workflowStepRepo.clearValidationResults();
		angular.extend(this.mapping.updateNote, {
			'method': OrganizationRepo.getSelectedOrganization().id + '/' + workflowStep.id + '/update-note',
			'data': note
		});
		var promise = WsApi.fetch(this.mapping.updateNote);
		promise.then(function(res) {
			if(angular.fromJson(res.body).meta.status === "INVALID") {
				angular.extend(workflowStepRepo, angular.fromJson(res.body).payload);
				console.log(workflowStepRepo);
			}
		});
		return promise;
	};

	this.removeNote = function(workflowStep, note) {
		workflowStepRepo.clearValidationResults();
		angular.extend(this.mapping.removeNote, {
			'method': OrganizationRepo.getSelectedOrganization().id + '/' + workflowStep.id + '/remove-note',
			'data': note
		});
		var promise = WsApi.fetch(this.mapping.removeNote);
		promise.then(function(res) {
			if(angular.fromJson(res.body).meta.status === "INVALID") {
				angular.extend(workflowStepRepo, angular.fromJson(res.body).payload);
				console.log(workflowStepRepo);
			}
		});
		return promise;
	};

	this.reorderNote = function(workflowStep, src, dest) {
		workflowStepRepo.clearValidationResults();
		angular.extend(this.mapping.reorderNote, {
			'method': OrganizationRepo.getSelectedOrganization().id + '/' + workflowStep.id + '/reorder-notes/' + src + '/' + dest
		});
		var promise = WsApi.fetch(this.mapping.reorderNote);
		promise.then(function(res) {
			if(angular.fromJson(res.body).meta.status === "INVALID") {
				angular.extend(workflowStepRepo, angular.fromJson(res.body).payload);
				console.log(workflowStepRepo);
			}
		});
		return promise;
	};

	return this;

});
