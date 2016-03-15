vireo.controller("LookAndFeelController", function($scope, $controller) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.dropMethod = function(file) {
		console.log(file);
	};

	$scope.logoSml = "resources/images/logo-sm.png";
	$scope.logoRight = "resources/images/vireo-right.gif";

});