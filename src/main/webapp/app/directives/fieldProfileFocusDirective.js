vireo.directive('fieldProfileFocus', function ($timeout) {
	return {
		restrict: 'A',
		link: function ($scope, element) { 
			if ($scope.fpi === 0) {
				$timeout(function() {
					var input = element[0].querySelector('input');
					if($scope.profile.inputType.name !== "INPUT_LICENSE" || $scope.profile.inputType.name === "INPUT_PROQUEST"){
						input.focus();
						angular.element(input).focus();
					}
				}, 500);
			}
		}
	};
});