vireo.directive("fieldProfileReview",  function() {
	return {
		// template: '<div><b>Gloss: </b><b>{{profile.fieldGlosses[0].value}}:</b><span> {{submission.getFieldValuesByFieldPredicate(profile.fieldPredicate)[0].value}}</span></div>',
		templateUrl: 'views/directives/fieldProfileReview.html',
		restrict: 'E',
		replace: 'false',
		scope: {
			profile: "="
		},
		link: function($scope) {
		  $scope.submission = $scope.$parent.submission;
		}
	};
});
