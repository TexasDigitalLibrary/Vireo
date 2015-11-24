vireo.directive('displayname', function () {
	return {
		template: '<span>{{user.displayName || (user.firstName+" "+user.lastName)}}</span>',
		restrict: 'E',
		scope:true,
		controller: 'UserController'
	};
});

vireo.directive('user', function (User) {
	return {
		template: '<span>{{displayValue}}</span>',
		restrict: 'E',
		scope:true,
		link: function($scope, elem, attr) {

			$scope.user = User.get();

			$scope.displayValue = "";

			User.ready().then(function() {
				for(var a in attr) {
					if(a.indexOf("$") == -1 && (typeof $scope.user[a] != 'undefined')) {
						$scope.displayValue += $scope.user[a] + " ";
					}
				}
			});

		}
	};
});