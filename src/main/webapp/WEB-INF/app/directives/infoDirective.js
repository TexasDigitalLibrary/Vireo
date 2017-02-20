vireo.directive("info", function(FieldValue) {
    return {
        templateUrl: 'views/directives/submissionInfo.html',
        restrict: 'E',
        replace: true,
        transclude: true,
        scope: {
            submission: '=',
            fieldProfile: '=',
            fields: '=',
            label: '@'
        },
        link: function($scope, element, attr) {
            var edit = attr.edit !== undefined ? attr.edit : 'text';
            $scope.edit = "views/admin/info/edit/" + $scope.fieldProfile.inputType.name.replace('_', '-').toLowerCase() + ".html";
        },
        controller: function($scope) {

            $scope.fieldPredicateFilter = function (fieldValue) {
                return fieldValue.fieldPredicate.value === $scope.fieldProfile.fieldPredicate.value;
            };

            $scope.addFieldValue = function() {
                $scope.submission.addFieldValue($scope.fieldProfile.fieldPredicate);
            };

            $scope.removeFieldValue = function(fieldValue) {
                fieldValue.updating = true;
                if (fieldValue.value !== undefined) {
                    $scope.submission.removeFieldValue(fieldValue).then(function(response) {
                        fieldValue.updating = false;
                    });
                } else {
                    $scope.submission.fieldValues.splice($scope.submission.fieldValues.indexOf(fieldValue), 1);
                }
            };

            $scope.editFieldValue = function(fieldValue) {
                fieldValue.editing = true;
            };

            $scope.save = function(fieldValue) {
                fieldValue.editing = false;
                fieldValue.updating = true;
                $scope.submission.saveFieldValue(fieldValue, $scope.fieldProfile).then(function(response) {
                    delete fieldValue.updating;
                });
            };

            $scope.cancel = function(fieldValue) {
                fieldValue.refresh();
                fieldValue.editing = false;
                delete fieldValue.updating;
            };

            $scope.inputTel = function() {
                return $scope.fieldProfile.inputType.name == 'INPUT_TEL';
            };

            $scope.inputUrl = function() {
                return $scope.fieldProfile.inputType.name == 'INPUT_URL';
            };

            $scope.inputDateTime = function() {
                return $scope.fieldProfile.inputType.name == 'INPUT_DATETIME';
            };

            $scope.standardInput = function() {
                return !$scope.inputTel() && !$scope.inputUrl() && !$scope.inputDateTime();
            };

        }
    };
});
