vireo.service("EmailTemplateRepo", function(WsApi, AbstractModel, AlertService) {

  var self;
  
  var EmailTemplatesRepo = function(futureData) {
    self = this;

    //This causes our model to extend AbstractModel
    angular.extend(self, AbstractModel);
    
    self.unwrap(self, futureData);		
  };
  
  EmailTemplatesRepo.data = null;
  
  EmailTemplatesRepo.listener = null;

  EmailTemplatesRepo.promise = null;
  
  EmailTemplatesRepo.set = function(data) {
    self.unwrap(self, data);
  };

  EmailTemplatesRepo.get = function() {

    if(EmailTemplatesRepo.promise) return EmailTemplatesRepo.data;

    var newEmailTemplatesRepoPromise = WsApi.fetch({
      endpoint: '/private/queue', 
      controller: 'settings/email-template', 
      method: 'all',
    });

    EmailTemplatesRepo.promise = newEmailTemplatesRepoPromise;

    if(EmailTemplatesRepo.data) {
      newEmailTemplatesRepoPromise.then(function(data) {
        EmailTemplatesRepo.set(JSON.parse(data.body).payload.HashMap);
      });
    }
    else {
      EmailTemplatesRepo.data = new EmailTemplatesRepo(newEmailTemplatesRepoPromise);	
    }

    EmailTemplatesRepo.listener = WsApi.listen({
      endpoint: '/channel', 
      controller: 'settings/email-template', 
      method: '',
    });
    
    EmailTemplatesRepo.set(EmailTemplatesRepo.listener);

    return EmailTemplatesRepo.data;	
  };

  EmailTemplatesRepo.add = function(depositLocation) {
    return WsApi.fetch({
      'endpoint': '/private/queue', 
      'controller': 'settings/email-template', 
      'method': 'create',
      'data': depositLocation
    }).then(function(response) {
      var responseType = angular.fromJson(response.body).meta.type;
      var responseMessage = angular.fromJson(response.body).meta.message;
      if(responseType != 'SUCCESS') {
        AlertService.add({type: responseType, message: responseMessage}, "/settings/email-template");  
      }
    });
  };

  EmailTemplatesRepo.update = function(depositLocation) {
    return WsApi.fetch({
      'endpoint': '/private/queue', 
      'controller': 'settings/email-template', 
      'method': 'update',
      'data': depositLocation
    }).then(function(response) {
      var responseType = angular.fromJson(response.body).meta.type;
      var responseMessage = angular.fromJson(response.body).meta.message;
      if(responseType != 'SUCCESS') {
        AlertService.add({type: responseType, message: responseMessage}, "/settings/email-template");  
      }
    });
  };

  EmailTemplatesRepo.reorder = function(src, dest) {
    console.info('reorder!! ' + src + ' ' + dest);
    return WsApi.fetch({
      'endpoint': '/private/queue', 
      'controller': 'settings/email-template', 
      'method': 'reorder/' + src + '/' + dest
    }).then(function(response) {
      var responseType = angular.fromJson(response.body).meta.type;
      var responseMessage = angular.fromJson(response.body).meta.message;
      if(responseType != 'SUCCESS') {
        AlertService.add({type: responseType, message: responseMessage}, "/settings/email-template");  
      }
    });
  };

  EmailTemplatesRepo.sort = function(column) {
    return WsApi.fetch({
      'endpoint': '/private/queue', 
      'controller': 'settings/email-template', 
      'method': 'sort/' + column
    }).then(function(response) {
      var responseType = angular.fromJson(response.body).meta.type;
      var responseMessage = angular.fromJson(response.body).meta.message;
      if(responseType != 'SUCCESS') {
        AlertService.add({type: responseType, message: responseMessage}, "/settings/email-template");  
      }
    });
  };

  EmailTemplatesRepo.remove = function(index) {
    return WsApi.fetch({
      'endpoint': '/private/queue', 
      'controller': 'settings/email-template', 
      'method': 'remove/' + index
    }).then(function(response) {
      var responseType = angular.fromJson(response.body).meta.type;
      var responseMessage = angular.fromJson(response.body).meta.message;
      if(responseType != 'SUCCESS') {
        AlertService.add({type: responseType, message: responseMessage}, "/settings/email-template");  
      }
    });
  };
  
  EmailTemplatesRepo.ready = function() {
    return EmailTemplatesRepo.promise;
  };

  EmailTemplatesRepo.listen = function() {
    return EmailTemplatesRepo.listener;
  };
  
  return EmailTemplatesRepo;
  
});
