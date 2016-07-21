vireo.directive("field",  function() {
	return {
		templateUrl: 'views/directives/fieldProfile.html',
		restrict: 'E',
		replace: 'false',
		scope: {
			profile: "="
		},
		link: function($scope) {

			$scope.saving = 0;

			$scope.submission = $scope.$parent.submission;

			$scope.values = [];

			$scope.getValues = function() {
				angular.extend($scope.values, $scope.submission.findFieldValuesByPredicate($scope.profile.predicate));
				if ($scope.values.length === 0) {
					$scope.values.push({
						id: null,
						value: "",
						predicate: $scope.profile.predicate
					});
				}
				return $scope.values;
			};

			$scope.save = function(value) {

				if ($scope.fieldProfileForm.$dirty) {
					$scope.saving = value.id;
					$scope.submission.saveFieldValue(value).then(function() {
						$scope.saving = 0;
						$scope.fieldProfileForm.$setPristine();
					});
				}

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
				return $scope.submission.findFieldValuesByPredicate($scope.profile.predicate).indexOf(value) === 0;
			};

			$scope.includeTemplateUrl = "views/inputtype/"+$scope.profile.inputType.name.toLowerCase().replace("_", "-")+".html";
		}
	};
});