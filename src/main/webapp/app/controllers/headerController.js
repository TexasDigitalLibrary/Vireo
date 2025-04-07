vireo.controller("HeaderController", function ($scope, $controller, $location, $timeout, AbstractRepo, AbstractAppRepo, AlertService, ManagedConfigurationRepo) {

	angular.extend($scope, $controller("AbstractController", {$scope: $scope}));

    console.log('HeaderController');

	$scope.configurable = ManagedConfigurationRepo.getAll();

	$scope.logoPath = "";

	ManagedConfigurationRepo.ready().then(function() {
		if ($scope.configurable.lookAndFeel) {
			$scope.logoPath = $scope.configurable.lookAndFeel.left_logo.value;
		}

		$scope.logoImage = function() {

			if($scope.configurable.lookAndFeel) {
				$scope.logoPath = $scope.configurable.lookAndFeel.left_logo.value;
			}

			if($scope.activeAdminSection()) {
				$scope.logoPath = "resources/images/logo.png";
			}

			return $scope.logoPath;
		};

		$scope.activeTab = function(path) {
			return $location.url().indexOf(path) >= 0;
		};

		$scope.activeAdminSection = function() {
			return $location.url().indexOf("/admin") >= 0;
		};

		$scope.viewSelect = function() {
			if(!$scope.activeTab('/admin/view')) {
				$location.path('/admin/list');
				$timeout(function() {
					AlertService.add({status: 'WARNING', message: 'Select a submission to view'}, 'submission/select');
				});
			}
		};

	});

});
