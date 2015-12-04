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

			this.closeAll = function() {
				if($scope.singleExpand == "true") AccordionService.closeAll();
			}

		},
		link: function($scope, element, attr) {
			
			$scope.singleExpand = typeof attr.singleExpand != "undefined" ? attr.singleExpand.toLowerCase() == "true" : false;	
			
		}
	};
});

vireo.directive("pane", function($location, $timeout, $anchorScroll, AccordionService) {
	var paneID = 0;
	return {
		templateUrl: 'views/directives/accordionPane.html',
		restrict: 'E',
		replace: false,
		transclude: true,
		require: "^accordion",
		scope: true,
		link: function ($scope, element, attr, parent) {


			$anchorScroll.yOffset = 55;

			paneID++;

			angular.extend($scope, parent);
			
			$scope.query = typeof attr.query != "undefined" ? attr.query : "pane"+paneID;

			
			$timeout(function() {
				var panelSearch = $location.search()["panel"];
				panelSearch == $scope.query ? $scope.open() : $scope.close();
				$location.hash(panelSearch).replace()
				$anchorScroll();
			});

			$scope.toggleExpanded = function() {
				$scope.closeAll();
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
	};

	AccordionService.closeAll = function() {
		for(var i in openPanes) {
			openPanes[i]();
		}
	};

	return AccordionService;

});