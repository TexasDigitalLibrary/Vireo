vireo.directive("info",  function($controller) {
	return {
		templateUrl: 'views/directives/submissionInfo.html',
		restrict: 'E',
		replace: true,
		transclude: true,
		scope: {
			data: "=",
			label: "@",
			phone: "@"
		},
		link: function($scope, element, attr) {			
			angular.extend(this, $controller('AbstractController', {$scope: $scope}));
			var edit = attr.edit !== undefined ? attr.edit : 'text';
			$scope.edit = "views/admin/info/edit/" + edit + ".html";
		}
	};
});
