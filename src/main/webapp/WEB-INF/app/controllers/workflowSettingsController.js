vireo.controller('WorkflowSettingsController', function ($controller, $scope, $location, $routeParams, User, UserSettings, SidebarService) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	SidebarService.addBox({"title":"Add Workflow Step","viewUrl":"views/sideboxes/newworkflowstep.html"});
});