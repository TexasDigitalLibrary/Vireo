vireo.directive("field", function($controller, $filter, $q, $timeout, FileApi) {
    return {
        templateUrl: 'views/directives/fieldProfile.html',
        restrict: 'E',
        replace: 'false',
        scope: {
            profile: "=",
            hfp: "="
        },
        link: function($scope) {

            angular.extend(this, $controller('AbstractController', {$scope: $scope}));

            $scope.includeTemplateUrl = "views/inputtype/" + $scope.profile.inputType.name.toLowerCase().replace("_", "-") + ".html";

            $scope.submission = $scope.$parent.submission;

            $scope.progress = 0;

            $scope.image = undefined;

            $scope.save = function(fieldValue) {
                if ($scope.fieldProfileForm.$dirty) {
                    fieldValue.updating = true;
                    return $q(function(resolve) {
                        // give typeahead time to set the value
                        $timeout(function() {
                            $scope.submission.saveFieldValue(fieldValue, $scope.profile).then(function(res) {
                                delete fieldValue.updating;
                                if ($scope.fieldProfileForm !== undefined) {
                                    $scope.fieldProfileForm.$setPristine();
                                }
                                resolve();
                            });
                        }, 500);
                    });
                }
            };

            $scope.hasFile = function(fieldValue) {
                return fieldValue !== undefined && fieldValue.fieldPredicate.documentTypePredicate && fieldValue.value && fieldValue.value.length > 0;
            };

            $scope.addFieldValue = function() {
                return $scope.submission.addFieldValue($scope.profile.fieldPredicate);
            };

            $scope.removeFieldValue = function(fieldValue) {
                if (fieldValue.id === null) {
                    $scope.submission.removeUnsavedFieldValue(fieldValue);
                } else {
                    fieldValue.updating = true;
                    $scope.submission.removeFieldValue(fieldValue).then(function() {
                        fieldValue.updating = false;
                    });
                }
            };

            $scope.showAdd = function(isFirst) {
                return $scope.profile.repeatable && isFirst;
            };

            $scope.showRemove = function(isFirst) {
                return $scope.profile.repeatable && !isFirst;
            };

            $scope.getPattern = function() {
                var pattern = "*";
                var cv = $scope.profile.controlledVocabularies[0];
                if (typeof cv !== "undefined") {
                    pattern = "";
                    for (var i in cv.dictionary) {
                        var word = cv.dictionary[i];
                        pattern += pattern.length > 0
                            ? ", " + word.name
                            : word.name;
                    }
                }
                return pattern;
            };

            $scope.queueUpload = function(files) {
                if (files.length > 0) {
                    $scope.previewing = true;
                    var i = 1;
                    refreshFieldValues();
                    if (!$scope.profile.repeatable) {
                        $scope.fieldValue.file = files[0];
                    } else {
                        var firstEmptyFieldValue = $scope.fieldValues[$scope.fieldValues.length - 1];
                        if (firstEmptyFieldValue.file === undefined) {
                            firstEmptyFieldValue.file = files[0];
                        } else {
                            i = 0;
                        }
                    }
                    for (; i < files.length; i++) {
                        $scope.addFieldValue().file = files[i];
                    }
                }
            };

            $scope.beginUpload = function() {
                $scope.progress = 0;
                $scope.uploading = true;
                var promises = [];
                refreshFieldValues();
                for (var i in $scope.fieldValues) {
                    var fieldValue = $scope.fieldValues[i];
                    if (fieldValue.file.uploaded === undefined) {
                        fieldValue.progress = 0;
                        fieldValue.uploading = true;
                        promises.push(upload(fieldValue));
                    }
                }
                $q.all(promises).then(function() {
                    $scope.previewing = false;
                    $scope.uploading = false;
                    refreshFieldValues();
                });
            };

            var upload = function(fieldValue) {
                return $q(function(resolve) {
                    FileApi.upload({'endpoint': '', 'controller': 'submission', 'method': 'upload', 'file': fieldValue.file}).then(function(response) {
                        if ($scope.hasFile(fieldValue)) {
                            $scope.submission.removeFile(fieldValue);
                        }
                        fieldValue.value = response.data.meta.message;
                        fieldValue.file.uploaded = true;
                        $scope.submission.saveFieldValue(fieldValue, $scope.profile).then(function() {
                            fieldValue.uploading = false;
                            resolve();
                        });
                    }, function(response) {
                        console.log('Error status: ' + response.status);
                    }, function(progress) {
                        $scope.progress = progress;
                        fieldValue.progress = progress;
                    });
                })
            };

            $scope.cancelUpload = function() {
                $scope.submission.removeAllUnsavedFieldValuesByPredicate($scope.profile.fieldPredicate);
                $scope.previewing = false;
                refreshFieldValues();
            };

            $scope.cancel = function(fieldValue) {
                $scope.submission.removeUnsavedFieldValue(fieldValue);
                refreshFieldValues();
                if ($scope.fieldValues.length === 0) {
                    $scope.addFieldValue();
                    refreshFieldValues();
                    $scope.previewing = false;
                }
            };

            $scope.fetchFileInfo = function(fieldValue) {
                if ($scope.hasFile(fieldValue)) {
                    $scope.submission.fileInfo(fieldValue).then(function(data) {
                        fieldValue.file = angular.fromJson(data.body).payload.ObjectNode;
                    });
                }
            };

            $scope.getPreview = function(fieldValue) {
                var preview;
                if (fieldValue !== undefined && fieldValue.file !== undefined && fieldValue.file.type !== null) {
                    if (fieldValue.file.type.includes("image/png")) {
                        preview = "resources/images/png-logo.jpg";
                    } else if (fieldValue.file.type.includes("image/jpeg")) {
                        preview = "resources/images/jpg-logo.png";
                    } else if (fieldValue.file.type.includes("pdf")) {
                        preview = "resources/images/pdf-logo.gif";
                    }
                }
                return preview;
            };

            $scope.getFile = function(fieldValue) {
                if ($scope.hasFile(fieldValue)) {
                    $scope.submission.file(fieldValue.value).then(function(data) {
                        saveAs(new Blob([data], {type: fieldValue.file.type}), fieldValue.file.name);
                    });
                } else {
                    saveAs(fieldValue.file);
                }
            };

            $scope.getUriHash = function(fieldValue) {
                var hash = 0;
                if (fieldValue !== undefined) {
                    var uri = fieldValue.value;
                    if (uri !== undefined) {
                        for (i = 0; i < uri.length; i++) {
                            char = uri.charCodeAt(i);
                            hash = ((hash << 5) - hash) + char;
                            hash = hash & hash;
                        }
                    }
                }
                return hash;
            };

            $scope.removeFile = function(fieldValue) {
                $scope.deleting = true;
                $scope.submission.removeFile(fieldValue).then(function(res) {
                    $scope.submission.removeFieldValue(fieldValue).then(function() {
                        $scope.deleting = false;
                        $scope.previewing = false;
                        delete fieldValue.file;
                        delete fieldValue.value;
                        if (!$scope.profile.repeatable) {
                            $scope.addFieldValue();
                        }
                        refreshFieldValues();
                        $scope.closeModal();
                    });
                });
            };

            var refreshFieldValues = function() {
                $scope.fieldValues = $filter('fieldValuePerProfile')($scope.submission.fieldValues, $scope.profile.fieldPredicate);
                $scope.fieldValue = $scope.fieldValues[0];

                for (var i in $scope.fieldValues) {
                    if ($scope.hasFile($scope.fieldValues[i])) {
                        $scope.hasFiles = true;
                        break;
                    }
                }
            };

            refreshFieldValues();

        }
    };
});
