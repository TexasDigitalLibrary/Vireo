vireo.controller('SidebarController', function ($controller, $scope, SidebarService) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

  $scope.sidebarBoxes = SidebarService.getBoxes();

});
