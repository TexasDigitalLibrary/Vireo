vireo.directive('displayname', function($controller, UserService, UserSettings) {
  return {
    template: '<span>{{userSettings.displayName}}</span>',
    restrict: 'E',
    scope: true,
    link: function($scope, elem) {
      angular.extend(this, $controller("AbstractController", {
        $scope: $scope
      }));
      if (!$scope.isAnonymous()) {
        $scope.userSettings = new UserSettings();
        $scope.userSettings.fetch();
      }
    }
  };
});

vireo.directive('usersettings', function($controller, UserSettings) {
  return {
    template: '<span>{{displayValue}}</span>',
    restrict: 'E',
    scope: true,
    link: function($scope, elem, attr) {
      angular.extend(this, $controller("AbstractController", {
        $scope: $scope
      }));

      if (!$scope.isAnonymous()) {
        $scope.userSettings = new UserSettings();
        $scope.userSettings.fetch();

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
