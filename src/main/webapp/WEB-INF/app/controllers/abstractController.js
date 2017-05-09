vireo.controller('AbstractController', function($scope, $window, ModalService, StorageService, RestApi) {

  angular.extend($scope, ModalService);

  $scope.modals = ModalService;

  $scope.isAnonymous = function() {
    return sessionStorage.token === undefined || sessionStorage.role === appConfig.anonymousRole;
  };

  $scope.isUser = function() {
    return sessionStorage.role === "STUDENT";
  };

  $scope.isStudent = function() {
    return sessionStorage.role === "STUDENT";
  };

  $scope.isReviewer = function() {
    return sessionStorage.role === "REVIEWER";
  };

  $scope.isManager = function() {
    return sessionStorage.role === "MANAGER";
  };

  $scope.isAdmin = function() {
    return sessionStorage.role === "ADMINISTRATOR";
  };

  $scope.reportError = function(alert) {
    RestApi.post({
      controller: 'report',
      method: 'error',
      data: alert
    }).then(function() {
      angular.element("#reportModal").modal('show');
    }, function(response) {
      if (response.data === null || response.data.message != "EXPIRED_JWT") {
        var subject = 'Error Report';
        var body = 'Error Report\n\nchannel: ' + alert.channel +
          '\ntime: ' + new Date(alert.time) +
          '\ntype: ' + alert.type +
          '\nmessage: ' + alert.message;
        $window.location.href = "mailto:" + coreConfig.alerts.email +
          "?subject=" + escape(subject) +
          "&body=" + escape(body);
      }
    }, function() {

    });
  };

});
