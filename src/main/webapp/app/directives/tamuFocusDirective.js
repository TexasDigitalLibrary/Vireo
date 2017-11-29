vireo.directive('tamuForceFocus', function ($timeout) {
	return {
		restrict: 'A',
		scope:true,
		link: function ($scope, element, attr) { 
			$scope.$watch(attr.tamuForceFocus, function() {
				if($scope[attr.tamuForceFocus]) {
					$timeout(function() {
			        	element[0].focus(); 
			        });
				}
			});
	
		}
	};
});