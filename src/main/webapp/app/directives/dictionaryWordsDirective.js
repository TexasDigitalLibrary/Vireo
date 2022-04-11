vireo.directive('dictionaryWords', function ($location, InputTypes, FieldValue, AdvisorSubmissionRepo) {
    return {
        templateUrl: 'views/directives/dictionaryWords.html',
        restrict: 'E',
        scope: {
            fieldValues: "=",
            fieldProfile: "=",
            showVocabularyWord: "&?",
            sortEmbargosWrap: "&?",
            isEmbargoWrap: "&?"
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

            $scope.orderVocabularyWords = function(word) {
                if (angular.isDefined($scope.isEmbargoWrap) && angular.isDefined($scope.sortEmbargosWrap) && $scope.isEmbargoWrap($scope.fieldProfile)) {
                    return $scope.sortEmbargosWrap(word);
                }
                return word.identifier;
            };

        }
    };
});
