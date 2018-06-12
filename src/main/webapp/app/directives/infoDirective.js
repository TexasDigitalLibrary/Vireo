vireo.directive("info", function ($q,FieldValue) {
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
            stacked: '=?'
        },
        link: function ($scope, element, attr) {
            var edit = attr.edit !== undefined ? attr.edit : 'text';
            $scope.edit = "views/admin/info/edit/" + $scope.fieldProfile.inputType.name.replace('_', '-').toLowerCase() + ".html";
        },
        controller: function ($scope) {

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

            $scope.editFieldValue = function (fieldValue) {
                fieldValue.editing = true;
            };

            var save = function (fieldValue) {
                return $q(function (resolve) {
                    $scope.submission.saveFieldValue(fieldValue, $scope.fieldProfile).then(function (response) {
                        delete fieldValue.updating;
                    });
                });
            };

            $scope.save = function (fieldValue) {
                fieldValue.editing = false;
                fieldValue.updating = true;
                save(fieldValue);
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
                save(fieldValue);
            };

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

            $scope.inputDateTime = function () {
                return $scope.fieldProfile.inputType.name == 'INPUT_DATETIME';
            };

            $scope.standardInput = function () {
                return !$scope.inputLicense() && !$scope.inputProquest() && !$scope.inputTel() && !$scope.inputUrl() && !$scope.inputDateTime();
            };

            $scope.setConditionalTextArea = function (fieldValue, checked) {
                fieldValue.value = checked ? fieldValue.value : null;
            };
        }
    };
});
