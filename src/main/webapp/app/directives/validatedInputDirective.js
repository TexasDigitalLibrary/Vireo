vireo.directive("validatedinput", function ($q, $timeout) {
    return {
        template: '<span ng-include src="view"></span>',
        restrict: 'E',
        scope: {
            "type": "@",
            "model": "=",
            "noId": "@",
            "id": "@",
            "property": "@",
            "label": "@",
            "placeholder": "@",
            "autocomplete": "@",
            "typeahead": "=",
            "typeaheadproperty": "@",
            "truevalue": "@",
            "falsevalue": "@",
            "hint": "@",
            "toolTip": "@",
            "form": "=",
            "confirm": "&",
            "validations": "=",
            "formView": "=",
            "repeatable": "=?",
            "disabled": "="
        },
        link: function ($scope, element, attr) {

            $scope.inProgress = false;

            if ($scope.formView) {
                $scope.view = 'node_modules/@wvr/core/app/views/directives/validatedInputForm.html';
            } else {
                $scope.view = 'views/directives/validatedInput.html';
            }

            if ($scope.form === undefined) {
                $scope.forms = {
                    dynamic: {}
                };
            }

            if ($scope.id === undefined && ($scope.noId === 'false' || $scope.noId === undefined)) {
                $scope.id = $scope.property;
            }

            var getForm = function () {
                return $scope.form !== undefined ? $scope.form : $scope.forms.dynamic;
            };

            var update = function () {
                if ($scope.confirm) {
                    $scope.inProgress = true;
                    $q.when($scope.confirm()).then(function () {
                        $timeout(function () {
                            $scope.inProgress = false;
                        }, 500);
                    });
                }
            };

            $scope.getValues = function (property) {
                if(property && property.length === 0) {
                    property.push('');
                }
                return property;
            };

            $scope.keydown = function ($event) {
                // enter(13): submit value to be persisted
                if ($event.which == 13 && $scope.formView && getForm().$valid) {
                    update();
                }
                // escape(27): reset value using shadow
                else if ($event.which == 27) {
                    if ($scope.model && typeof $scope.model.refresh === 'function') {
                        $scope.model.refresh();
                    }
                }
            };

            $scope.blur = function ($event) {
                if ($scope.formView && getForm().$valid) {
                    update();
                }
            };

            $scope.change = function ($event) {
                if (getForm().$valid) {
                    update();
                }
            };

            $scope.addMember = function (member) {
                $scope.model[$scope.property].push(member ? member : "");
                $scope.model.dirty(true);
            };

            $scope.removeMember = function (i) {
                $scope.model[$scope.property].splice(i, 1);
                $scope.model.dirty(true);
            };

            $scope.isDisabled = function () {
                if (angular.isDefined($scope.disabled)) {
                    return $scope.disabled($scope.model);
                }

                return false;
            };

        }
    };
});
