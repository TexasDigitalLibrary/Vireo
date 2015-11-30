vireo.directive("tooltip", function() {
	return {
		template: '<a href="#" class="tooltip-icon glyphicon glyphicon-info-sign" title="{{title}}" data-toggle="tooltip" data-placement="right" rel="tooltip" ng-transclude></a>',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: true,
		link: function ($scope, element, attr) {	    	
			$scope.title = attr.title;
	    }
	};
});