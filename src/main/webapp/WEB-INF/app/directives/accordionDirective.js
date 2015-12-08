vireo.directive("accordion", function(AccordionService) {
	return {
		template: '<div class="accordion" ng-transclude></div>',
		restrict: 'E',
		replace: true,
		transclude: true,
		scope: {
			singleExpand: "@singleExpand"
		},
		controller: function($scope)  {

			this.closeAll = function(id) {
				if($scope.singleExpand == "true") AccordionService.closeAll(id);
			}

		},
		link: function($scope, element, attr) {
			
			$scope.singleExpand = typeof attr.singleExpand != "undefined" ? attr.singleExpand.toLowerCase() == "true" : false;	
			
		}
	};
});

vireo.directive("pane", function($location, $timeout, $anchorScroll, AccordionService) {
	var count = 0;
	return {
		templateUrl: 'views/directives/accordionPane.html',
		restrict: 'E',
		replace: false,
		transclude: true,
		require: "^accordion",
		scope: true,
		link: function ($scope, element, attr, parent) {

			var paneID = count++;

			$anchorScroll.yOffset = 55;
			
			angular.extend($scope, parent);
			
			$scope.query = typeof attr.query != "undefined" ? attr.query : "pane"+paneID;

			
			$timeout(function() {
				var panelSearch = $location.search()["panel"];
				if(panelSearch == $scope.query) $scope.open();
				$location.hash(panelSearch).replace()
				$anchorScroll();
			});

			$scope.toggleExpanded = function() {
				$scope.closeAll(paneID);
				$scope.expanded ? $scope.close() : $scope.open();
			}

			$scope.open = function(pageLoad) {
				if(typeof $scope.html == "undefined") {
					$scope.loading = true;
					$scope.html = attr.html;
				}
				$scope.expanded = true;
				AccordionService.add(paneID, $scope.close);
				$location.search("panel", $scope.query, false).replace();	
				
			}

			$scope.close = function() {
				$scope.expanded = false;
				//AccordionService.remove(paneID)
				console.log(paneID + " is closed");
			}

			$scope.loaded = function() {
				$timeout(function(){
					$scope.loading = false;	
				}, 500);
			}

	    }
	};
});

vireo.service("AccordionService", function() {

	var AccordionService = this;

	var openPanes = {};

	AccordionService.add = function(id, close) {
		openPanes[id] = close;
		console.log(openPanes);
	};

	AccordionService.remove = function(id) {
		if(openPanes[id]) delete openPanes[id];
	}

	AccordionService.closeAll = function(id) {
		for(var i in openPanes) {
			if(id != i)  {
				console.log("closing " + id);
				openPanes[i]();
			}
		}
	};

	return AccordionService;

});