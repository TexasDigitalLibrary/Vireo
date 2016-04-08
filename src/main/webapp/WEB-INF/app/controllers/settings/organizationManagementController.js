vireo.controller("OrganizationManagementController", function ($controller, $scope, $q, OrganizationRepo) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.ready = $q.all([OrganizationRepo.ready()]);

	$scope.ready.then(function() {
/*      
		$scope.resetPanels = function() {
        	$scope.activePanel;
	        $scope.panelHistory = [];
	        $scope.openPanels = [new PanelEntry($scope.organizations.list[0])];
        }
*/
	});
});
