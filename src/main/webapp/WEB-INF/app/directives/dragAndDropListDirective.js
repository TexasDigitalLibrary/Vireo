vireo.directive("draganddroplist", function($filter) {
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
			'sortLabel': '@',
			'sortColumn': '@',
			'sortAction': '=',
			'sortActionSort': '=',
			'sortMethod': '&',
            'textFilter': '=?',
			'isEditable': '&'
		},
		controller: function($scope) {

            $scope.textFilterValue = {};

            $scope.setSelectedFilter = function(filter) {
                $scope.selectedFilter = filter;
            };

			if(typeof $scope.itemView == 'undefined') {
				$scope.itemView = 'views/directives/dragAndDropItem.html';
			}
		},
		link: function($scope, elem, attr) {

            $scope.activeFilter = attr.filter?$filter(attr.filter):null;

            if($scope.activeFilter) {
                $scope.scopeValue = $scope.activeFilter($scope.scopeValue, "value");
            }

			$scope.properties = angular.fromJson(attr.properties);
            $scope.selectedFilter = $scope.properties.length==1?$scope.properties[0]:"";
		}
	};
});
