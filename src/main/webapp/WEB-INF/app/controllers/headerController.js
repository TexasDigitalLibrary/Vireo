vireo.controller("HeaderController", function($scope, $controller, $location) {

	angular.extend($scope, $controller("AbstractController", {$scope: $scope}));

	$scope.logoImage = function() {
		var logoPath = 	"resources/images/logo-sm.png";
		if($scope.activeAdminSection()) logoPath = "resources/images/logo.gif";
		return logoPath;
	}

	$scope.activeTab = function(tab) {
		return $location.url().indexOf("/admin/"+tab) != -1;
	}

	$scope.activeAdminSection = function() {
		return $location.url().indexOf("/admin") != -1;
	}

});