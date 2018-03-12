vireo.directive('fieldProfileFocus', function ($timeout) {
	return {
		restrict: 'A',
		link: function ($scope, element) { 
			if ($scope.fpi === 0) {
				$timeout(function() {
					var input = element[0].querySelector('input');
					if(input){
						input.focus();
						angular.element(input).focus();
					}
				}, 500);
			}
		}
	};
});