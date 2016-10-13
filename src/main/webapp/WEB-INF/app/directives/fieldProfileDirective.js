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
				angular.extend($scope.values, $scope.submission.findFieldValuesByFieldPredicate($scope.profile.fieldPredicate));
				if ($scope.values.length === 0) {
					$scope.values.push({
						id: null,
						value: "",
						fieldPredicate: $scope.profile.fieldPredicate
					});
				}
				return $scope.values;
			};

			$scope.save = function(value) {

				if ($scope.fieldProfileForm.$dirty) {
					$scope.saving = value.id;
					$scope.submission.saveFieldValue(value).then(function() {
						$scope.saving = 0;
						if($scope.fieldProfileForm !== undefined) {
							$scope.fieldProfileForm.$setPristine();
						}
					});
				}

			};

			$scope.addFieldValue = function() {
				$scope.submission.addFieldValue($scope.profile.fieldPredicate);
			};

			$scope.removeFieldValue = function(value) {
				var indexOfValue = $scope.submission.fieldValues.indexOf(value);
				$scope.submission.fieldValues.splice(indexOfValue, 1);
			};

			$scope.filterValuesByFieldPredicate = function(value) {
				return $scope.profile.fieldPredicate.id === value.fieldPredicate.id;
			};

			$scope.showRemove = function(value) {
				return $scope.profile.repeatable && !$scope.first(value);
			};

			$scope.showAdd = function(value) {
				return $scope.profile.repeatable && $scope.first(value);
			};

			$scope.first = function(value) {
				return $scope.submission.findFieldValuesByPredicate($scope.profile.fieldPredicate).indexOf(value) === 0;
			};

			$scope.getPattern = function() {
				
				var patern = "*";
				var cv = $scope.profile.controlledVocabularies[0];
	
				if(typeof cv !== "undefined") {
					patern = "";
					for(var i in cv.dictionary) {
						var word = cv.dictionary[i];
						patern+=word.name;
						if(i+1!==cv.dictionary.length) patern+=",";
					}
				}

				return patern;
			};

			$scope.begineUpload = function(file) {
				$scope.file = file;
			};

			$scope.getPreview = function(index) {
				var preview = null;
				if($scope.file.type.includes("image/")) preview = $scope.values[index].value;
				if($scope.file.type.includes("pdf")) preview = "resources/images/pdf-logo.gif";
				return preview;
			};

			console.log($scope.profile.inputType.name)
			$scope.includeTemplateUrl = "views/inputtype/"+$scope.profile.inputType.name.toLowerCase().replace("_", "-")+".html";
		}
	};
});