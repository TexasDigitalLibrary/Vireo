vireo.directive('displayname', function (UserSettings) {
	return {
		template: '<span>{{userSettings.displayName}}</span>',
		restrict: 'E',
		scope:true,
		link: function($scope, elem, attr) {
			$scope.userSettings = UserSettings.get();
		}
	};
});

vireo.directive('usersettings', function (UserSettings) {
	return {
		template: '<span>{{displayValue}}</span>',
		restrict: 'E',
		scope:true,
		link: function($scope, elem, attr) {

			$scope.userSettings = UserSettings.get();

			$scope.displayValue = "";

			UserSettings.ready().then(function() {
				for(var a in attr) {
					if(a.indexOf("$") == -1 && (typeof $scope.userSettings[a] != 'undefined')) {
						$scope.displayValue += $scope.userSettings[a] + " ";
					}
				}
			});

		}
	};
});