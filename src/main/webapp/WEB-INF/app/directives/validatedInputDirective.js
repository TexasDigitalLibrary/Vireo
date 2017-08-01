vireo.directive("validatedinput", function ($timeout) {
    return {
        template: '<span ng-include src="view"></span>',
        restrict: 'E',
        scope: {
            "type": "@",
            "model": "=",
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
            "repeatable": "=?"
        },
        link: function ($scope, element, attr) {

            $scope.inProgress = false;

            if ($scope.formView) {
                $scope.view = 'bower_components/core/app/views/directives/validatedInputForm.html';
            } else {
                $scope.view = 'views/directives/validatedInput.html';
            }

            if ($scope.form === undefined) {
                $scope.forms = {
                    dynamic: {}
                };
            }

            var getForm = function () {
                return $scope.form !== undefined ? $scope.form : $scope.forms.dynamic;
            };

            var update = function () {
                if ($scope.confirm !== undefined) {
                    $scope.inProgress = true;
                    $scope.confirm().then(function () {
                        $timeout(function () {
                            $scope.inProgress = false;
                        }, 500);
                    });
                }
            };

            $scope.keydown = function ($event) {
                // enter(13): submit value to be persisted
                if ($event.which == 13 && $scope.formView && getForm().$valid) {
                    if ($scope.confirm !== undefined) {
                        update();
                    }
                }
                // escape(27): reset value using shadow
                if ($event.which == 27) {
                    $scope.model.refresh();
                }
            };

            $scope.blur = function ($event) {
                if ($scope.formView && getForm().$valid) {
                    if ($scope.confirm !== undefined) {
                        update();
                    }
                }
            };

            $scope.change = function ($event) {
                if (getForm().$valid) {
                    if ($scope.confirm !== undefined) {
                        update();
                    }
                }
            };

            $scope.addMember = function (member) {
                $scope.model[$scope.property].push(member ? member : "");
            };

            $scope.removeMember = function (i) {
                $scope.model[$scope.property].splice(i, 1);
            };

        }
    };
});
