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
                toolbar1: "formatselect bold italic underline | bullist numlist undo redo | link unlink | image | code",
                theme: "silver",
                plugins: "autoresize lists advlist link autolink image code",
                menubar: false,
                statusbar: false,
                image_advtab: true,
                height: "100%",
                width: "100%",
                file_picker_callback: function (callback, value, meta) {
                    if (meta.filetype === 'image') {
                        var input = document.createElement('input');
                        input.setAttribute('type', 'file');
                        input.setAttribute('accept', 'image/*');

                        input.onchange = function () {
                            var file = this.files[0];
                            var reader = new FileReader();

                            reader.onload = function () {
                                var id = 'blobid' + (new Date()).getTime();
                                var blobCache = tinymce.activeEditor.editorUpload.blobCache;
                                var base64 = reader.result.split(',')[1];
                                var blobInfo = blobCache.create(id, file, base64);
                                blobCache.add(blobInfo);

                                callback(blobInfo.blobUri(), { title: file.name });
                            };
                            reader.readAsDataURL(file);
                        };

                        input.click();
                    }
                }
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
