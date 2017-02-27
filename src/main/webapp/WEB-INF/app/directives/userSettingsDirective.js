vireo.service("UserSettingsService", function(UserSettings) {

	var UserSettingsService = this;

	var userSettings = new UserSettings();

	userSettings.fetch();

	UserSettingsService.getUserSettings = function() {
		return userSettings;
	}

	return UserSettingsService;
});

vireo.directive('displayname', function($controller, UserSettingsService) {
  return {
    template: '<span>{{userSettings.displayName}}</span>',
    restrict: 'E',
    scope: true,
    link: function($scope, elem) {
      angular.extend(this, $controller("AbstractController", {
        $scope: $scope
      }));
      if (!$scope.isAnonymous()) {
        $scope.userSettings = UserSettingsService.getUserSettings();
      }
    }
  };
});

vireo.directive('usersettings', function($controller, UserSettingsService) {
  return {
    template: '<span>{{displayValue}}</span>',
    restrict: 'E',
    scope: true,
    link: function($scope, elem, attr) {
      angular.extend(this, $controller("AbstractController", {
        $scope: $scope
      }));

      if (!$scope.isAnonymous()) {
        $scope.userSettings = UserSettingsService.getUserSettings();

        $scope.displayValue = "";

        $scope.userSettings.ready().then(function() {
          for (var a in attr) {
            if (a.indexOf("$") == -1 && (typeof $scope.userSettings[a] != 'undefined')) {
              $scope.displayValue += $scope.userSettings[a] + " ";
            }
          }
        });
      }
    }
  };
});
