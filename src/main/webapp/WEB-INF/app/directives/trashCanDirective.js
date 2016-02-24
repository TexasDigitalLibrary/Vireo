vireo.directive("trashcan", function() {
	return {
		templateUrl: 'views/directives/trashCan.html',
		restrict: 'E',
		scope: {},
		controller: function($scope) {
			$scope.garbage = {};

			$scope.trashCanListener1 = {
				dragStart: function(event) {
					console.log('dragStart')
				},
				dragMove: function(event) {
					console.log('dragMove')
				},
				dragEnd: function(event) {
					console.log('dragEnd')
				},
				dragCancel: function(event) {
					console.log('dragCancel')
				},
			    accept: function (sourceItemHandleScope, destSortableScope) {
			    	console.log('accept')
			     	return sourceItemHandleScope.itemScope.sortableScope.$id === destSortableScope.$id;
			    },
			    itemMoved: function (event) {
					console.log('itemMoved')
			    },
			    orderChanged: function(event) {
			    	console.log('orderChanged')
			    }
			};
		},
		link: function($scope, element, attr) {
			
			element[0].addEventListener("drop", function(e) {
				e.preventDefault();
				console.log('yay');
				return false;
			}, false);
		}
	};
});