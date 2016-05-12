vireo.directive("validationresponse", function() {
	return {
		templateUrl: 'views/directives/validationResponse.html',
		restrict: 'E',
		scope: {
			"validationResponse": "="
		}
	};
});
