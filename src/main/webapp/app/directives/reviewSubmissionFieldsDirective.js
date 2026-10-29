vireo.directive('reviewsubmissionsfields', function ($location, InputTypes, FieldValue, AdvisorSubmissionRepo) {
    return {
        templateUrl: 'views/directives/reviewSubmissionFields.html',
        restrict: 'E',
        scope: {
            submission: "=",
            filterOptional: "=?",
            hideLinks: "=?",
            setActiveStep: "&",
            showVocabularyWord: "&?",
            sortEmbargos: "&?",
            isEmbargo: "&?",
            validate: "=?",
            headingLevel: "=?"
        },
        controller: function ($scope) {
            // The workflow step names are section headings. Their level depends on how
            // deeply this directive is embedded in the page that uses it, so callers may
            // override it to avoid skipping a level. Only 2 and 3 are supported.
            if ($scope.headingLevel !== 3) {
                $scope.headingLevel = 2;
            }
            if ($scope.validate) {
                $scope.submission.ready().then(function () {
                    $scope.submission.validate();
                });
            }
        },
        link: function ($scope) {

            $scope.InputTypes = InputTypes;

            $scope.required = function (aggregateFieldProfile) {
                return !$scope.filterOptional || !aggregateFieldProfile.optional;
            };

            $scope.predicateMatch = function (fv) {
                return function (aggregateFieldProfile) {
                    return aggregateFieldProfile.fieldPredicate.id == fv.fieldPredicate.id;
                };
            };

            $scope.hasValidationViolation = function (predicate) {

                var fieldValues = $scope.submission.getFieldValuesByFieldPredicate(predicate);

                for (var i in fieldValues) {
                    var fieldValue = fieldValues[i];

                    if (fieldValue.isValid && !fieldValue.isValid()) {
                        return true;
                    } else {
                        var fieldProfile = $scope.submission.getFieldProfileByPredicate(predicate);
                        if ((fieldProfile.inputType.name === 'INPUT_PROQUEST' || fieldProfile.inputType.name === 'INPUT_LICENSE') && fieldValue.value === 'false' && !fieldProfile.optional) {
                            fieldValue.setIsValid(false);
                            fieldValue.addValidationMessage('You must accept the license agreement to continue');
                            return true;
                        }
                    }
                }

                return false;
            };

            $scope.getFile = function (fieldValue) {
                $scope.submission.fileInfo(fieldValue).then(function (data) {
                    fieldValue.fileInfo = angular.fromJson(data.body).payload.ObjectNode;
                    $scope.submission.file(fieldValue.value).then(function (data) {
                        saveAs(new Blob([data], {
                            type: fieldValue.fileInfo.type
                        }), fieldValue.fileInfo.name);
                    });

                });
            };

            $scope.jumpToStep = function (wfs, hash) {
                $scope.$parent.setActiveStep(wfs, hash);
            };

            $scope.isEmbargoWrap = function(fieldValue) {
                return $scope.isEmbargo(fieldValue);
            };

            $scope.sortEmbargosWrap = function(word) {
                return $scope.sortEmbargos(word);
            };

        }
    };
});
