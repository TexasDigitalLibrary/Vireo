vireo.directive("field",  function() {
	return {
		templateUrl: 'views/directives/fieldProfile.html',
		restrict: 'E',
		replace: 'false',
		scope: {
			profile: "="
		},
		link: function($scope, element, attr) {

			$scope.includeTemplateUrl = "views/inputtype/"+$scope.profile.inputType.name+".html"
			
		}
	};
});