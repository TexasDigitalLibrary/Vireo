vireo.service("SidebarService", function($rootScope) {

	var SidebarService = this;

	SidebarService.boxes = [];

	SidebarService.getBox = function(target) {
		return SidebarService.boxes[target];
	};

	SidebarService.getBoxes = function() {
		return SidebarService.boxes;
	};

	SidebarService.addBox = function(box) {
		SidebarService.boxes.push(box);
	};

	SidebarService.addBoxes = function(newBoxes) {
	    angular.extend(SidebarService.boxes, newBoxes);
	};		

	SidebarService.remove = function(box) {
		SidebarService.boxes.splice(box,1);
	};	

	SidebarService.clear = function() {
		SidebarService.boxes.length = 0;
	}

	$rootScope.$on("$routeChangeSuccess",function() {
		SidebarService.clear();
	});

	return SidebarService;
	
});