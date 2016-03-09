vireo.directive("draganddroplist", function() {
	return {
		templateUrl: function(elem, attr) {
			if(attr.listView !== undefined) {
				return attr.listView;
			} else {
				return 'views/directives/dragAndDropList.html';
			}
		},
		restrict: 'E',
		scope: {
			'dragging': '=',
			'scopeValue': '=',
			'listeners': '=',
			'edit': '&',
			'toString': '&',
			'itemView': '@',
			'sortColumn': '@',
			'sortAction': '=',
			'sortMethod': '&'
		},
		controller: function($scope) {
			if(typeof $scope.itemView == 'undefined') {
				$scope.itemView = 'views/directives/dragAndDropItem.html';
			}
		},
		link: function($scope, elem, attr) {
			$scope.properties = angular.fromJson(attr.properties);
		}	
	};
});
