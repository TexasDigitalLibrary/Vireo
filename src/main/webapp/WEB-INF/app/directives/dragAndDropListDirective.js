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

			// var trashCan = angular.element('.trash-drop-zone');

			// var trashCanOffset = trashCan.offset();

			// var trashCanOffsetHeight = trashCan[0].offsetHeight;

			// var trashCanOffsetWidth = trashCan[0].offsetWidth;

			// var overTrash = false;

			$scope.dragging = false;

			if(typeof $scope.itemView == 'undefined') {
				$scope.itemView = 'views/directives/dragAndDropItem.html'
			}

			$scope.dragControlListeners = {
				dragStart: function(event) {
					$scope.dragging = true;
				},
				dragMove: function(event) {
					// if(event.nowX > trashCanOffset.left && event.nowX < (trashCanOffset.left + trashCanOffsetWidth) &&
					//    event.nowY > trashCanOffset.top && event.nowY < (trashCanOffset.top + trashCanOffsetHeight)) {
					//    	overTrash = true;
					// }
					// else {
					// 	overTrash = false;
					// }
				},
				dragEnd: function(event) {
					$scope.dragging = false;
					// if(overTrash) {
					//    	var index = event.source.index + 1;
					//    	$scope.remove({'index': index});
					// }
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
