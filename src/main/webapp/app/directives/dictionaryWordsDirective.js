vireo.directive('dictionaryWords', function ($location, InputTypes, FieldValue, AdvisorSubmissionRepo) {
    return {
        templateUrl: 'views/directives/dictionaryWords.html',
        restrict: 'E',
        scope: {
            fieldValues: "=",
            fieldProfile: "=",
            showVocabularyWord: "&?"
        },
        link: function ($scope) {

            $scope.predicateMatch = function (fv) {
                return function (fieldProfile) {
                    return fieldProfile.fieldPredicate.id == fv.fieldPredicate.id;
                };
            };

            $scope.displayVocabularyWord = function(value, index, array) {
                if (angular.isDefined($scope.showVocabularyWord)) {
                    return $scope.showVocabularyWord()(value, $scope.fieldProfile);
                }

                return true;
            };

        }
    };
});
