vireo.controller("HeaderController", function(AbstractRepo, AbstractAppRepo, $scope, $controller, $location, ConfigurationRepo) {

	angular.extend($scope, $controller("AbstractController", {$scope: $scope}));
		
	$scope.configurable = ConfigurationRepo.getAllMapByType();

	$scope.logoPath = "";

	ConfigurationRepo.ready().then(function() {
		
		$scope.logoPath = $scope.configurable.lookAndFeel.left_logo;

		$scope.logoImage = function() {	
		
			if($scope.configurable.lookAndFeel) {
				$scope.logoPath = $scope.configurable.lookAndFeel.left_logo.value;
			}
		
			if($scope.activeAdminSection()) {
				$scope.logoPath = "resources/images/logo.gif";
			}

			return $scope.logoPath;
		};

		$scope.activeTab = function(tab) {
			return $location.url().indexOf("/admin/"+tab) != -1;
		};

		$scope.activeAdminSection = function() {
			return $location.url().indexOf("/admin") != -1;
		};

	});

	

});