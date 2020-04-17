vireo.directive("lockingtextarea", function ($timeout) {
    return {
        templateUrl: 'views/directives/lockingTextArea.html',
        restrict: 'E',
        scope: {
            "label": "@",
            "scopeValue": "=",
            "toolTip": "@",
            "hint": "@",
            "onBlur": "&",
            "keyDown": "&",
            "name": "@",
            "timer": "@",
            "wysiwyg": "@"
        },
        controller: function ($scope) {

            $scope.locked = true;

            $scope.toggleLock = function () {
                if ($scope.locked) {
                    reset();
                }
                $scope.locked = !$scope.locked;
            };

            if ($scope.timer === undefined) {
                $scope.timer = 100;
            }

            var timer;

            var reset = function () {
                if (timer !== undefined) {
                    $timeout.cancel(timer);
                }
                timer = $timeout(function () {
                    $scope.onBlur();
                    reset();
                }, $scope.timer * 1000);
            };

            var save = function () {
                $scope.onBlur();
                $scope.toggleLock();
                $timeout.cancel(timer);
            };

            $scope.tinymceOptions = {
                name: $scope.name,
                setup: function (editor) {
                    editor.on('KeyUp', function (e) {
                        reset();
                    });
                    editor.on('Blur', function (e) {
                        save();
                    });
                },
                toolbar1: "formatselect bold italic underline | bullist numlist undo redo",
                theme: "silver",
                plugins: "autoresize",
                menubar: false,
                statusbar: false,
                image_advtab: true,
                height: "100%",
                width: "100%"
            };

            $scope.nonWysiwygTyping = function ($event) {
                reset();
            };

            $scope.nonWysiwygBlur = function ($event) {
                save();
            };
        }
    };
});
