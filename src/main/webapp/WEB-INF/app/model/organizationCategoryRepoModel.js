vireo.service("OrganizationCategoryRepoModel", function(WsApi, AbstractModel, AlertService) {

  var self;

  var OrganizationCagtegoryRepo = function(futureData) {
    self = this;
    angular.extend(self, AbstractModel);
    self.unwrap(self, futureData);
  };

  OrganizationCagtegoryRepo.data = null;

  OrganizationCagtegoryRepo.listener = null;

  OrganizationCagtegoryRepo.promise = null;

  OrganizationCagtegoryRepo.set = function(data) {
    self.unwrap(self, data);
  };

  OrganizationCagtegoryRepo.get = function() {

    if(OrganizationCagtegoryRepo.promise) return OrganizationCagtegoryRepo.data;

    var newOrganizationCagtegoryRepoPromise = WsApi.fetch({
      endpoint: '/private/queue',
      controller: 'settings/organization-category',
      method: 'all',
    });

    OrganizationCagtegoryRepo.promise = newOrganizationCagtegoryRepoPromise;

    if(OrganizationCagtegoryRepo.data) {
      newOrganizationCagtegoryRepoPromise.then(function(data) {
        OrganizationCagtegoryRepo.set(JSON.parse(data.body).payload.HashMap);
      });
    }
    else {
      OrganizationCagtegoryRepo.data = new OrganizationCagtegoryRepo(newOrganizationCagtegoryRepoPromise);
    }

    OrganizationCagtegoryRepo.listener = WsApi.listen({
      endpoint: '/channel',
      controller: 'settings/organization-category',
      method: '',
    });

    OrganizationCagtegoryRepo.set(OrganizationCagtegoryRepo.listener);

    return OrganizationCagtegoryRepo.data;
  };

  OrganizationCagtegoryRepo.add = function(organizationCategory) {
    return WsApi.fetch({
      'endpoint': '/private/queue',
      'controller': 'settings/organization-category',
      'method': 'create',
      'data': organizationCategory
    });
  };

  OrganizationCagtegoryRepo.update = function(organizationCategory) {
    return WsApi.fetch({
      'endpoint': '/private/queue',
      'controller': 'settings/organization-category',
      'method': 'update',
      'data': organizationCategory
    });
  };

  OrganizationCagtegoryRepo.remove = function(index) {
    return WsApi.fetch({
      'endpoint': '/private/queue',
      'controller': 'settings/organization-category',
      'method': 'remove/' + index
    });
  };

  OrganizationCagtegoryRepo.ready = function() {
    return OrganizationCagtegoryRepo.promise;
  };

  OrganizationCagtegoryRepo.listen = function() {
    return OrganizationCagtegoryRepo.listener;
  };

  return OrganizationCagtegoryRepo;

});
