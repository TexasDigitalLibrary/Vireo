vireo.controller('WorkflowManagementController', function ($controller, $scope, SidebarService) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    SidebarService.addBoxes([{
        "title": "Workflow Management",
        "viewUrl": "views/sideboxes/workflowManagement.html"
    }]);

});
