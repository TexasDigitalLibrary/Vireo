vireo.directive('displayname', function (UserSettingsNew) {
	return {
		template: '<span>{{userSettings.displayName}}</span>',
		restrict: 'E',
		scope:true,
		link: function($scope, elem) {
			$scope.userSettings = new UserSettingsNew();
		}
	};
});

vireo.directive('usersettings', function (UserSettingsNew) {
	return {
		template: '<span>{{displayValue}}</span>',
		restrict: 'E',
		scope:true,
		link: function($scope, elem, attr) {

			$scope.userSettings = new UserSettingsNew();

			$scope.displayValue = "";

			$scope.userSettings.ready().then(function() {
				for(var a in attr) {
					if(a.indexOf("$") == -1 && (typeof $scope.userSettings[a] != 'undefined')) {
						$scope.displayValue += $scope.userSettings[a] + " ";
					}
				}
			});

		}
	};
});