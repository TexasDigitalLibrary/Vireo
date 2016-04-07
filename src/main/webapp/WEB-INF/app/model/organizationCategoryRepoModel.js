vireo.service("OrganizationCategoryRepoModel", function(WsApi, AbstractModel, AlertService) {

  var self;
  
  var OrganizationCategoryRepo = function(futureData) {
    self = this;

    angular.extend(self, AbstractModel);

    self.unwrap(self, futureData);		
  };
  
  OrganizationCategoryRepo.data = null;
  
  OrganizationCategoryRepo.listener = null;

  OrganizationCategoryRepo.promise = null;
  
  OrganizationCategoryRepo.set = function(data) {
    self.unwrap(self, data);
  };

  OrganizationCategoryRepo.get = function() {

    if(OrganizationCategoryRepo.promise) return OrganizationCategoryRepo.data;

    var newOrganizationCategoryRepoPromise = WsApi.fetch({
      endpoint: '/private/queue', 
      controller: 'settings/organization-category', 
      method: 'all',
    });

    OrganizationCategoryRepo.promise = newOrganizationCategoryRepoPromise;

    if(OrganizationCategoryRepo.data) {
      newOrganizationCategoryRepoPromise.then(function(data) {
        OrganizationCategoryRepo.set(JSON.parse(data.body).payload.HashMap);
      });
    }
    else {
      OrganizationCategoryRepo.data = new OrganizationCategoryRepo(newOrganizationCategoryRepoPromise);	
    }

    OrganizationCategoryRepo.listener = WsApi.listen({
      endpoint: '/channel', 
      controller: 'settings/organization-category', 
      method: '',
    });
    
    OrganizationCategoryRepo.set(OrganizationCategoryRepo.listener);

    return OrganizationCategoryRepo.data;	
  };

  OrganizationCategoryRepo.add = function(organizationCategory) {
    return WsApi.fetch({
      'endpoint': '/private/queue', 
      'controller': 'settings/organization-category', 
      'method': 'create',
      'data': organizationCategory
    });
  };

  OrganizationCategoryRepo.update = function(organizationCategory) {
    return WsApi.fetch({
      'endpoint': '/private/queue', 
      'controller': 'settings/organization-category', 
      'method': 'update',
      'data': organizationCategory
    });
  };

  OrganizationCategoryRepo.reorder = function(src, dest) {
    return WsApi.fetch({
      'endpoint': '/private/queue', 
      'controller': 'settings/organization-category', 
      'method': 'reorder/' + src + '/' + dest
    });
  };

  OrganizationCategoryRepo.remove = function(organizationCategory) {
    return WsApi.fetch({
      'endpoint': '/private/queue', 
      'controller': 'settings/organization-category', 
      'method': 'remove/' + organizationCategory.id
    });
  };
  
  OrganizationCategoryRepo.ready = function() {
    return OrganizationCategoryRepo.promise;
  };

  OrganizationCategoryRepo.listen = function() {
    return OrganizationCategoryRepo.listener;
  };
  
  return OrganizationCategoryRepo;
  
});
