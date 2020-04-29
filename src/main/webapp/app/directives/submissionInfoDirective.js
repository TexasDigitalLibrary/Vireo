vireo.directive("submissionInfo", function () {
    return {
        templateUrl: 'views/directives/submissionInfo.html',
        restrict: 'E',
        replace: true,
        transclude: true,
        scope: {
            submission: '=',
            fieldProfile: '=',
            fields: '=',
            label: '@',
            showVocabularyWord: "&?",
            stacked: '=?',
            type: '@'
        },
        link: function ($scope, element, attr) {
            if (attr.type == 'view') {
                $scope.edit = "views/admin/info/edit/" + $scope.fieldProfile.inputType.name.replace('_', '-').toLowerCase() + ".html";
            }
        },
        controller: function ($scope, $element, $timeout) {

            $scope.refreshFieldValue = function (fieldValue) {
                fieldValue.refresh();
                fieldValue.setIsValid(true);
                fieldValue.setValidationMessages([]);
            };

            $scope.fieldPredicateFilter = function (fieldValue) {
                return fieldValue.fieldPredicate.value === $scope.fieldProfile.fieldPredicate.value;
            };

            $scope.addFieldValue = function () {
                $scope.submission.addFieldValue($scope.fieldProfile.fieldPredicate);
            };

            $scope.removeFieldValue = function (fieldValue) {
                if (fieldValue.id === undefined || fieldValue.id === null) {
                    $scope.submission.removeUnsavedFieldValue(fieldValue);
                } else {
                    fieldValue.updating = true;
                    $scope.submission.removeFieldValue(fieldValue).then(function () {
                        fieldValue.updating = false;
                    });
                }
            };

            $scope.editFieldValue = function ($event, fieldValue) {
                fieldValue.editing = true;
                $timeout(function() {
                    var infoForm = $element.find("input");
                    if(!infoForm.hasClass("form-control")) {
                        infoForm = $element.find("textarea");
                    }
                    if(infoForm.hasClass("form-control")) {
                        if(infoForm.length > 1) {
                            infoForm[Number($event.currentTarget.id) * 2].focus();
                        } else {
                            infoForm.focus();
                        }
                    }
                });
            };

            var save = function (fieldValue) {
                $scope.submission.saveFieldValue(fieldValue, $scope.fieldProfile).then(function (response) {
                    delete fieldValue.updating;
                });
            };

            $scope.save = function (fieldValue) {
                if (!angular.isDefined(fieldValue.updating) || !fieldValue.updating) {
                    fieldValue.editing = false;
                    fieldValue.updating = true;
                    save(fieldValue);
                }
            };

            $scope.saveContacts = function (fieldValue) {
                fieldValue.editing = false;
                fieldValue.updating = true;
                if (typeof fieldValue.contacts === 'string') {
                    fieldValue.contacts = fieldValue.contacts.split(",");
                }
                save(fieldValue);
            };

            $scope.saveWithCV = function (fieldValue, item) {
                fieldValue.editing = false;
                fieldValue.updating = true;
                fieldValue.identifier = item.identifier;
                fieldValue.definition = item.definition;
                fieldValue.contacts = item.contacts;
                fieldValue.value = item.name;
                save(fieldValue);
            };

            $scope.stopEditing = function (fieldValue) {
                angular.forEach($scope.submission.fieldValues, function(fv) {
                    if (fv.id === fieldValue.id) {
                        if (fv.value === fieldValue.value) {
                            fieldValue.editing = false;
                        }

                        return;
                    }
                });
            };

            $scope.toggleFieldValue = function (field, value) {
                if (field.value === value) {
                    $scope.stopEditing(field);
                } else {
                    field.value = value;
                    $scope.save(field);
                }
            };

            $scope.datepickerOptions = {};
            $scope.datepickerFormat = angular.isDefined($scope.fieldProfile.controlledVocabulary) ? "MMMM yyyy" : "MM/dd/yyyy";
            var checkDisabled = function (dateAndMode) {
                var disabled = true;
                if(angular.isDefined($scope.fieldProfile.controlledVocabulary)) {
                    for (var i in $scope.fieldProfile.controlledVocabulary.dictionary) {
                        var cvw = $scope.fieldProfile.controlledVocabulary.dictionary[i];
                        if (cvw.name == dateAndMode.date.getMonth()) {
                            disabled = false;
                            break;
                        }
                    }
                }
                return disabled;
            };

            if (angular.isDefined($scope.fieldProfile.controlledVocabulary) && $scope.fieldProfile.controlledVocabulary.name === "Graduation Months") {
                $scope.datepickerOptions.customClass = function (dateAndMode) {
                    if (checkDisabled(dateAndMode)) return "disabled";
                };
                $scope.datepickerOptions.dateDisabled = checkDisabled;
                $scope.datepickerOptions.minViewMode = "month";
                $scope.datepickerOptions.minMode = "month";
                $scope.datepickerOptions.maxViewMode = "month";
                $scope.datepickerOptions.maxMode = "month";
            }

            $scope.cancel = function (fieldValue) {
                fieldValue.refresh();
                fieldValue.editing = false;
                delete fieldValue.updating;
            };

            $scope.inputLicense = function () {
                return $scope.fieldProfile.inputType.name == 'INPUT_LICENSE';
            };

            $scope.inputProquest = function () {
                return $scope.fieldProfile.inputType.name == 'INPUT_PROQUEST';
            };

            $scope.inputTel = function () {
                return $scope.fieldProfile.inputType.name == 'INPUT_TEL';
            };

            $scope.inputUrl = function () {
                return $scope.fieldProfile.inputType.name == 'INPUT_URL';
            };

            $scope.inputDegreeDate = function () {
                return $scope.fieldProfile.inputType.name == 'INPUT_DEGREEDATE';
            };

            $scope.inputDateTime = function () {
                return $scope.fieldProfile.inputType.name == 'INPUT_DATETIME';
            };

            $scope.inputContactChair = function () {
                return $scope.fieldProfile.fieldPredicate.value == 'dc.contributor.advisor';
            };

            $scope.inputFile = function () {
                return $scope.fieldProfile.inputType.name == 'INPUT_FILE';
            };

            $scope.standardInput = function () {
                return !$scope.inputLicense() && !$scope.inputProquest() && !$scope.inputTel() && !$scope.inputUrl() && !$scope.inputDegreeDate() && !$scope.inputDateTime() && !$scope.inputContactChair() && !$scope.inputFile();
            };

            $scope.setConditionalTextArea = function (fieldValue, checked) {
                fieldValue.value = checked ? fieldValue.value : null;
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
