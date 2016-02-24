vireo.directive("draganddroplist", function() {
	return {
		templateUrl: 'views/directives/dragAndDropList.html',
		restrict: 'E',
		scope: {
			'dragging': '=',
			'scopeValue': '=',
			'reorder': '&',
			'remove': '&',
			'itemView': '@'
		},
		controller: function($scope) {

			$scope.dragging = false;

			if(typeof $scope.itemView == 'undefined') {
				$scope.itemView = 'views/directives/dragAndDropItem.html'
			}

			$scope.dragControlListeners = {
				dragStart: function(event) {
					$scope.dragging = true;
				},
				dragMove: function(event) {
					
				},
				dragEnd: function(event) {
					$scope.dragging = false;					
				},
				dragCancel: function(event) {
					$scope.dragging = false;
				},
			    accept: function (sourceItemHandleScope, destSortableScope) {
			     	return sourceItemHandleScope.itemScope.sortableScope.$id === destSortableScope.$id;
			    },
			    itemMoved: function (event) {

			    },
			    orderChanged: function(event) {
			    	var src = event.source.index + 1;
			    	var dest = event.dest.index + 1;
			    	$scope.reorder({'src': src, 'dest': dest});
			    }
			};
		},
		link: function($scope, elem, attr) {
			$scope.properties = angular.fromJson(attr.properties);
		}	
	};
});
