vireo.directive("field", function ($controller, $filter, $q, $timeout, FileUploadService) {
    return {
        templateUrl: 'views/directives/fieldProfile.html',
        restrict: 'E',
        replace: 'false',
        scope: {
            profile: "=",
            configuration: "=",
            showVocabularyWord: "&?",
            fpi: "="
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

            $scope.dropzoneText = "Choose file here or drag and drop to upload";

            var save = function (fieldValue) {
                return $q(function (resolve) {
                    $scope.submission.saveFieldValue(fieldValue, $scope.profile).then(function (res) {
                        delete fieldValue.updating;
                        if ($scope.fieldProfileForm !== undefined) {
                            $scope.fieldProfileForm.$setPristine();
                            $scope.fieldProfileForm.$setUntouched();
                        }
                        resolve();
                    });
                });
            };

            $scope.save = function (fieldValue) {
                // give typeahead select time to save the value
                $timeout(function () {
                    // if the fieldProfileForm is undefined we have changed view, save the field value if not already updating
                    if (($scope.fieldProfileForm === undefined || $scope.fieldProfileForm.$dirty || !$scope.profile.optional) && !fieldValue.updating) {
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
                fieldValue.value = item.name;
                save(fieldValue);
            };

            $scope.saveContacts = function (fieldValue, noResults) {
                if (noResults) {
                    fieldValue.updating = true;
                    if (typeof fieldValue.contacts === 'string') {
                        fieldValue.contacts = fieldValue.contacts.split(",");
                    }
                    save(fieldValue);
                }
            };

            $scope.datepickerOptions = {};
            $scope.datepickerFormat = angular.isDefined($scope.profile.controlledVocabulary) ? "MMMM yyyy" : "MM/dd/yyyy";
            var checkDisabled = function (dateAndMode) {
                var disabled = true;
                if(angular.isDefined($scope.profile.controlledVocabulary)) {
                    for (var i in $scope.profile.controlledVocabulary.dictionary) {
                        var cvw = $scope.profile.controlledVocabulary.dictionary[i];
                        if (cvw.name == dateAndMode.date.getMonth()) {
                            disabled = false;
                            break;
                        }
                    }
                }
                return disabled;
            };

            if (angular.isDefined($scope.profile.controlledVocabulary) && $scope.profile.controlledVocabulary.name === "Graduation Months") {

                $scope.datepickerOptions.customClass = function (dateAndMode) {
                    if (checkDisabled(dateAndMode)) return "disabled";
                };
                $scope.datepickerOptions.dateDisabled = checkDisabled;

                $scope.datepickerOptions.minViewMode = "month";
                $scope.datepickerOptions.minMode = "month";
                $scope.datepickerOptions.maxViewMode = "month";
                $scope.datepickerOptions.maxMode = "month";
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
                if(angular.isDefined($scope.profile.controlledVocabulary)) {
                    var cv = $scope.profile.controlledVocabulary;
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

            var uploadFailed = function(fieldValue, reason) {
                fieldValue.uploading = false;
                fieldValue.setIsValid(false);
                if (fieldValue.fileInfo !== undefined && fieldValue.fileInfo.uploaded === true) {
                    delete fieldValue.fileInfo.uploaded;
                }
                $scope.errorMessage = "Upload Failed" + (reason ? ": " + reason : "") + ".";
                $scope.cancelUpload();
            };

            var upload = function (fieldValue) {
                return $q(function (resolve) {
                    FileUploadService.uploadFile($scope.submission, fieldValue).then(function (response) {
                        if (response.data.meta.status === 'SUCCESS') {
                            if ($scope.hasFile(fieldValue)) {
                                $scope.submission.removeFile(fieldValue);
                            }
                            fieldValue.value = response.data.meta.message;
                            fieldValue.fileInfo.uploaded = true;
                            $scope.submission.saveFieldValue(fieldValue, $scope.profile).then(function (response) {
                                var apiRes = angular.fromJson(response.body);
                                if (apiRes.meta.status === 'SUCCESS') {
                                    var newFieldValue = apiRes.payload.FieldValue;
                                    if(newFieldValue.fieldPredicate.value === "_doctype_primary") {
                                        $scope.submission.fetchDocumentTypeFileInfo();
                                    }
                                    fieldValue.uploading = false;
                                    resolve(true);
                                } else {
                                    if (apiRes.meta.message !== undefined) {
                                        uploadFailed(fieldValue, apiRes.meta.message);
                                    }
                                    resolve(false);
                                }
                            });
                        }
                        else {
                            if (response.payload !== undefined && typeof response.payload  == "object" && response.payload.meta.message !== undefined) {
                                uploadFailed(fieldValue, response.payload.meta.message);
                            }
                            else {
                                uploadFailed(fieldValue, false);
                            }
                            resolve(false);
                        }
                    }, function (response) {
                        var reason = false;
                        var status = response.meta.status;
                        if (response.payload !== undefined && typeof response.payload  == "object" && response.payload.meta.message !== undefined) {
                            reason = response.payload.meta.message;
                            status = response.payload.meta.status;
                        }
                        else if (response.status !== undefined) {
                            status = response.status;
                        }

                        console.log('Error status: ' + status);
                        uploadFailed(fieldValue, reason);
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
                    if (fieldValue.fileInfo.type.indexOf("image/png") >= 0) {
                        preview = "resources/images/png-logo.jpg";
                    } else if (fieldValue.fileInfo.type.indexOf("image/jpeg") >= 0) {
                        preview = "resources/images/jpg-logo.png";
                    } else if (fieldValue.fileInfo.type.indexOf("pdf") >= 0) {
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

            $scope.initConditionalTextarea = function (fieldValue) {
                $scope.confirm = false;
                $scope.checked = angular.isDefined(fieldValue) && fieldValue.value.length > 0;
            };

            $scope.setConditionalTextArea = function ($event, fieldValue) {
                $scope.confirm = false;
                if ($event && fieldValue.value) {
                    $event.preventDefault();
                    $scope.confirm = true;
                }
                fieldValue.value = $event ? fieldValue.value : "";
            };

            $scope.saveConditionalTextArea = function(fieldValue) {
                save(fieldValue).then(function() {
                    if(fieldValue.length === 0) {
                        $scope.checked = false;
                    }
                });
            };

            $scope.confirmRemove = function (fieldValue) {
                fieldValue.value = "";
                save(fieldValue).then(function() {
                    $scope.checked = false;
                    $scope.confirm = false;
                });
            };

            $scope.cancelRemove = function() {
                $scope.confirm = false;
            };

            $scope.showConfirm = function() {
                return $scope.confirm;
            };

            $scope.displayVocabularyWord = function(value, index, array) {
                if (angular.isDefined($scope.showVocabularyWord)) {
                    return $scope.showVocabularyWord()(value, $scope.profile);
                }

                return true;
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
