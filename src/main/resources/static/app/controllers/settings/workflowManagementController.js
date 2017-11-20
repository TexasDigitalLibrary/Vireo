vireo.controller('WorkflowManagementController', function ($controller, $scope, SidebarService) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    SidebarService.addBoxes([]);

});
