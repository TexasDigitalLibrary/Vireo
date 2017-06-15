vireo.controller('ApplicationSettingsController', function ($controller, $scope, SidebarService) {
	
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	SidebarService.addBoxes([
	    {
	        "title": "My Profile",
	        "viewUrl": "views/sideboxes/myprofile.html"
	    },
	    {
	        "title": "My Preferences",
	        "viewUrl": "views/sideboxes/mypreferences.html"
	    }
	]);

});