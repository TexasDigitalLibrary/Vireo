vireo.directive("field", function ($controller, $filter, $q, $timeout, FileUploadService) {
    return {
        templateUrl: 'views/directives/fieldProfile.html',
        restrict: 'E',
        replace: 'false',
        scope: {
            profile: "=",
            configuration: "="
        },
        link: function ($scope) {
            angular.extend(this, $controller('AbstractController', {
                $scope: $scope
            }));

            $scope.includeTemplateUrl = "views/inputtype/" + $scope.profile.inputType.name.toLowerCase().replace("_", "-") + ".html";

            $scope.submission = $scope.$parent.submission;

            $scope.progress = 0;

            $scope.image = undefined;

            $scope.errorMessage = "";

            var save = function (fieldValue) {
                return $q(function (resolve) {
                    $scope.submission.saveFieldValue(fieldValue, $scope.profile).then(function (res) {
                        delete fieldValue.updating;
                        if ($scope.fieldProfileForm !== undefined) {
                            $scope.fieldProfileForm.$setPristine();
                        }
                        resolve();
                    });
                });
            };

            $scope.save = function (fieldValue) {
                // give typeahead select time to save the value
                $timeout(function () {
                    // if the fieldProfileForm is undefined we have changed view, save the field value if not already updating
                    if (($scope.fieldProfileForm === undefined || $scope.fieldProfileForm.$dirty) && !fieldValue.updating) {
                        fieldValue.updating = true;
                        return save(fieldValue);
                    }
                }, 250);
            };

            $scope.saveWithCV = function (fieldValue, item) {
                fieldValue.updating = true;
                fieldValue.identifier = item.identifier;
                fieldValue.definition = item.definition;
                fieldValue.contacts = item.contacts;
                save(fieldValue);
            };

            $scope.saveContacts = function (fieldValue) {
                fieldValue.updating = true;
                if (typeof fieldValue.contacts === 'string') {
                    fieldValue.contacts = fieldValue.contacts.split(",");
                }
                save(fieldValue);
            };

            $scope.datepickerOptions = {};
            $scope.datepickerFormat = $scope.profile.controlledVocabularies.length ? "MMMM yyyy" : "MM/dd/yyyy";
            var checkDissabled = function (dateAndMode) {
                var dissabled = true;

                for (var i in $scope.profile.controlledVocabularies[0].dictionary) {
                    var cvw = $scope.profile.controlledVocabularies[0].dictionary[i];
                    if (cvw.name == dateAndMode.date.getMonth()) {
                        dissabled = false;
                        break;
                    }
                }
                return dissabled;
            };

            if ($scope.profile.controlledVocabularies.length && $scope.profile.controlledVocabularies[0].name === "Graduation Months") {

                $scope.datepickerOptions.customClass = function (dateAndMode) {
                    if (checkDissabled(dateAndMode)) return "dissabled";
                };
                $scope.datepickerOptions.dateDisabled = checkDissabled;

                $scope.datepickerOptions.minViewMode = "month";
                $scope.datepickerOptions.minMode = "month";

            }

            $scope.hasFile = function (fieldValue) {
                return fieldValue !== undefined && fieldValue.fieldPredicate.documentTypePredicate && fieldValue.value && fieldValue.value.length > 0;
            };

            $scope.addFieldValue = function () {
                return $scope.submission.addFieldValue($scope.profile.fieldPredicate);
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

            $scope.showAdd = function (isFirst) {
                return $scope.profile.repeatable && isFirst;
            };

            $scope.showRemove = function (isFirst) {
                return $scope.profile.repeatable && !isFirst;
            };

            $scope.getPattern = function () {
                var pattern = "*";
                var cv = $scope.profile.controlledVocabularies[0];
                if (typeof cv !== "undefined") {
                    pattern = "";
                    for (var i in cv.dictionary) {
                        var word = cv.dictionary[i];
                        pattern += pattern.length > 0 ? (",." + word.name) : ("." + word.name);
                    }
                }
                return pattern;
            };

            $scope.queueUpload = function (files) {
                if (files.length > 0) {
                    $scope.errorMessage = "";
                    $scope.previewing = true;
                    var i = 1;
                    refreshFieldValues();
                    if (!$scope.profile.repeatable) {
                        $scope.fieldValue.fileInfo = $scope.fieldValue.file = files[0];
                    } else {
                        var firstEmptyFieldValue = $scope.fieldValues[$scope.fieldValues.length - 1];
                        if (firstEmptyFieldValue.id) {
                            firstEmptyFieldValue = $scope.addFieldValue();
                        }
                        if (firstEmptyFieldValue.file === undefined) {
                            firstEmptyFieldValue.fileInfo = firstEmptyFieldValue.file = files[0];
                        } else {
                            i = 0;
                        }
                    }
                    for (; i < files.length; i++) {
                        var fieldValue = $scope.addFieldValue();
                        fieldValue.fileInfo = fieldValue.file = files[i];
                    }
                }
            };

            $scope.beginUpload = function () {
                $scope.progress = 0;
                $scope.uploading = true;
                var promises = [];
                refreshFieldValues();
                for (var i in $scope.fieldValues) {
                    var fieldValue = $scope.fieldValues[i];
                    if (fieldValue.fileInfo.uploaded === undefined) {
                        fieldValue.progress = 0;
                        fieldValue.uploading = true;
                        promises.push(upload(fieldValue));
                    }
                }
                $q.all(promises).then(function () {
                    $scope.previewing = false;
                    $scope.uploading = false;
                    refreshFieldValues();
                });
            };

            var upload = function (fieldValue) {
                return $q(function (resolve) {
                    FileUploadService.uploadFile($scope.submission, fieldValue).then(function (response) {
                        if ($scope.hasFile(fieldValue)) {
                            $scope.submission.removeFile(fieldValue);
                        }
                        fieldValue.value = response.data.meta.message;
                        fieldValue.fileInfo.uploaded = true;
                        $scope.submission.saveFieldValue(fieldValue, $scope.profile).then(function () {
                            fieldValue.uploading = false;
                            resolve();
                        });
                    }, function (response) {
                        console.log('Error status: ' + response.status);
                        $scope.errorMessage = response.data.meta.message;
                        $scope.cancelUpload();
                    }, function (progress) {
                        $scope.progress = progress;
                        fieldValue.progress = progress;
                    });
                });
            };

            $scope.cancelUpload = function () {
                $scope.submission.removeAllUnsavedFieldValuesByPredicate($scope.profile.fieldPredicate);
                $scope.previewing = false;
                $scope.uploading = false;
                refreshFieldValues();
            };

            $scope.cancel = function (fieldValue) {
                $scope.submission.removeUnsavedFieldValue(fieldValue);
                refreshFieldValues();
                if ($scope.fieldValues.length === 0) {
                    $scope.addFieldValue();
                    refreshFieldValues();
                    $scope.previewing = false;
                }
            };

            $scope.getPreview = function (fieldValue) {
                var preview;
                if (fieldValue !== undefined && fieldValue.fileInfo !== undefined && fieldValue.fileInfo.type !== null) {
                    if (fieldValue.fileInfo.type.includes("image/png")) {
                        preview = "resources/images/png-logo.jpg";
                    } else if (fieldValue.fileInfo.type.includes("image/jpeg")) {
                        preview = "resources/images/jpg-logo.png";
                    } else if (fieldValue.fileInfo.type.includes("pdf")) {
                        preview = "resources/images/pdf-logo.gif";
                    }
                }
                return preview;
            };

            $scope.getFile = function (fieldValue) {
                if ($scope.hasFile(fieldValue)) {
                    $scope.submission.file(fieldValue.value).then(function (data) {
                        saveAs(new Blob([data], {
                            type: fieldValue.fileInfo.type
                        }), fieldValue.fileInfo.name);
                    });
                } else {
                    saveAs(fieldValue.fileInfo);
                }
            };

            $scope.getUriHash = function (fieldValue) {
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

            $scope.removeFile = function (fieldValue) {
                $scope.deleting = true;
                FileUploadService.removeFile($scope.submission, fieldValue).then(function (removed) {
                    $scope.deleting = false;
                    $scope.previewing = false;
                    if(removed) {
                        delete fieldValue.file;
                        delete fieldValue.value;
                        if (!$scope.profile.repeatable) {
                            $scope.addFieldValue();
                        }
                    }
                    refreshFieldValues();
                    $scope.closeModal();
                });
            };

            $scope.setConditionalTextArea = function (fieldValue, checked) {
                fieldValue.value = checked ? fieldValue.value : null;
                //Only save if checked == true and value is a non-empty string OR if checked == false and value is not a string (which it won't have been anyway given the line above)
                if (!checked && !fieldValue.value) $scope.save(fieldValue);
            };

            var refreshFieldValues = function () {
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
