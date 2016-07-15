vireo.directive("field",  function() {
	return {
		templateUrl: 'views/directives/fieldProfile.html',
		restrict: 'E',
		replace: 'false',
		scope: {
			profile: "="
		},
		link: function($scope) {

			$scope.saving = false;

			$scope.submission = $scope.$parent.submission;

			$scope.values = $scope.submission.fieldValues;

			$scope.save = function(value) {
				$scope.saving = true;
				$scope.submission.saveFieldValue(value).then(function() {
					$scope.saving = false;
				});
			};

			$scope.addFieldValue = function() {
				$scope.submission.addFieldValue($scope.profile.predicate);
			};

			$scope.removeFieldValue = function(value) {
				var indexOfValue = $scope.submission.fieldValues.indexOf(value);
				$scope.submission.fieldValues.splice(indexOfValue, 1);
			};

			$scope.filterValuesByPredicate = function(value) {
				return $scope.profile.predicate.id === value.predicate.id;
			};

			$scope.showRemove = function(value) {
				return $scope.profile.repeatable && !$scope.first(value);
			};

			$scope.showAdd = function(value) {
				return $scope.profile.repeatable && $scope.first(value);
			};

			$scope.first = function(value) {
				return $scope.submission.findFieldValuesByPredicate($scope.profile.predicate).indexOf(value) === 0
			};

			if(!$scope.submission.findFieldValuesByPredicate($scope.profile.predicate).length) {
				$scope.addFieldValue();
			}
			$scope.includeTemplateUrl = "views/inputtype/"+$scope.profile.inputType.name.toLowerCase().replace("_", "-")+".html";
		}
	};
});