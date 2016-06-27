vireo.controller('SidebarController', function ($controller, $scope, SidebarService, TestRepo) {
	
    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    TestRepo.setOnCache("foo");

	$scope.sidebarBoxes = SidebarService.getBoxes();
});