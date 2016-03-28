vireo.controller("HeaderController", function($scope, $controller, $location, ConfigurableSettings) {

	angular.extend($scope, $controller("AbstractController", {$scope: $scope}));
		
	$scope.configurable = ConfigurableSettings.get();

	$scope.logoPath = "";

	ConfigurableSettings.ready().then(function() {
		$scope.logoPath = $scope.configurable.lookAndFeel.left_logo;
	});

	$scope.logoImage = function() {	
		
		if($scope.configurable.lookAndFeel) $scope.logoPath = $scope.configurable.lookAndFeel.left_logo
	
		if($scope.activeAdminSection()) $scope.logoPath = "resources/images/logo.gif";	
		return $scope.logoPath;
	}

	$scope.activeTab = function(tab) {
		return $location.url().indexOf("/admin/"+tab) != -1;
	}

	$scope.activeAdminSection = function() {
		return $location.url().indexOf("/admin") != -1;
	}

});