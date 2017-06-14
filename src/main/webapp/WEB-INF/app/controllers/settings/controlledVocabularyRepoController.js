vireo.controller("ControlledVocabularyRepoController", function ($controller, $q, $scope, $timeout, ControlledVocabularyRepo, DragAndDropListenerFactory, LanguageRepo, NgTableParams) {

    angular.extend(this, $controller("AbstractController", {
        $scope: $scope
    }));

    $scope.controlledVocabularyRepo = ControlledVocabularyRepo;

    $scope.controlledVocabulary = ControlledVocabularyRepo.getAll();

    ControlledVocabularyRepo.listen(function (data) {
        $scope.resetControlledVocabulary();
    });

    $scope.languages = LanguageRepo.getAll();

    $scope.ready = $q.all([ControlledVocabularyRepo.ready(), LanguageRepo.ready()]);

    $scope.dragging = false;

    $scope.trashCanId = 'controlled-vocabulary-trash';

    $scope.sortAction = "confirm";

    $scope.uploadAction = "confirm";

    $scope.forms = {};

    $scope.newVW = {};

    var firstEditableCv = function() {

        var cv = null

        $scope.controlledVocabulary.some(function(pcv) {
            var check = !pcv.isEntityProperty;
            if(check) cv = pcv;
            return check;
        });

        return cv;

    }

    $scope.addVocabularyWord = function(newVW) {
        newVW.adding = true;
        ControlledVocabularyRepo.addVocabularyWord($scope.selectedCv, newVW).then(function(res) {
            $scope.cancelAdding(newVW)
        });
    }

    $scope.cancelAdding = function(newVW) {
        Object.keys(newVW).forEach(function(key) {
            delete newVW[key];
        });
        newVW.adding = false;
    }

    $scope.isEditing = function(editableVW, $first) {
        return editableVW.editing || ($first && $scope.editFirst)
    }

    $scope.removeVocabularyWord = function(vw) {
        $scope.selectedCv.deleting = true;
        ControlledVocabularyRepo.removeVocabularyWord($scope.selectedCv, vw).then(function(res) {
            $scope.selectedCv.deleting = true;
        });
    }

    $scope.updateCv = function(vw) {
        $scope.selectedCv.updating = true;
        ControlledVocabularyRepo.updateVocabularyWord($scope.selectedCv, vw).then(function(res) {
            $scope.selectedCv.updating = false;
        });
    }

    $scope.cancelCvEdits = function(editableVW,definition) {

        Object.keys(definition).forEach(function(key) {
            editableVW[key] = definition[key];
        }); 

        $scope.editFirst = false;
        editableVW.editing = false;
    };

    var reloadTable = function() {
        $scope.cvTableParams = new NgTableParams({
            sorting: {
                name: "asc"
            }
        }, {
            counts: [],
            dataset: $scope.selectedCv.dictionary
        });
    }

    $scope.setSelectedCv = function(cv) {
        
        if($scope.selectedCv) $scope.selectedCv.clearListens();
        
        $scope.selectedCv = cv;

        reloadTable();

        $scope.selectedCv.listen(function() {
            reloadTable();
        });
    }

    $scope.ready.then(function () {

        var getDefaultIndex = function () {
            var defaultIndex = 0;
            for (var i in $scope.controlledVocabulary) {
                var cv = $scope.controlledVocabulary[i];
                if (cv.isEntityProperty === false) {
                    defaultIndex = i;
                    break;
                }
            }
            return defaultIndex;
        };

        $scope.setSelectedCv(firstEditableCv());
        
        reloadTable();

        $scope.resetControlledVocabulary = function (closeModal) {

            $scope.controlledVocabularyRepo.clearValidationResults();
            for (var key in $scope.forms) {
                if ($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
                    $scope.forms[key].$setPristine();
                }
            }

            if ($scope.uploadAction == 'process') {
                ControlledVocabularyRepo.cancel($scope.uploadModalData.cv.name);
                $scope.uploadAction = 'confirm';
                $scope.uploadStatus();
            }

            $scope.uploadModalData = {
                cv: $scope.controlledVocabulary[getDefaultIndex()]
            };

            $scope.columnHeaders = "";

            $scope.uploadWordMap = {};

            if ($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
                $scope.modalData.refresh();
            }
            $scope.modalData = {
                language: $scope.languages[0]
            };
            if (closeModal) {
                $scope.closeModal();
            }
        };

        $scope.resetControlledVocabulary();

        ControlledVocabularyRepo.change.then(null, null, function (data) {
            if ($scope.uploadAction != "process") {
                $scope.uploadStatus();
                $scope.uploadModalData = {
                    cv: $scope.controlledVocabulary[getDefaultIndex()]
                };
            }
        });

        $scope.createControlledVocabulary = function () {
            ControlledVocabularyRepo.create($scope.modalData).then(function (res) {
                if (angular.fromJson(res.body).meta.type === 'SUCCESS') {
                    $scope.resetControlledVocabulary(true);
                }
            });
        };

        $scope.uploadStatus = function () {
            if ($scope.uploadModalData.cv !== undefined) {
                ControlledVocabularyRepo.status($scope.uploadModalData.cv.name).then(function (data) {
                    $scope.uploadModalData.cv.inProgress = angular.fromJson(data.body).payload.Boolean;
                });
            }
        };

        $scope.selectControlledVocabulary = function (index) {
            $scope.modalData = $scope.controlledVocabulary[index];
        };

        $scope.editControlledVocabulary = function (index) {
            $scope.selectControlledVocabulary(index - 1);
            $scope.openModal('#controlledVocabularyEditModal');
        };

        $scope.updateControlledVocabulary = function () {
            $scope.modalData.save();
        };

        $scope.removeControlledVocabulary = function () {
            $scope.modalData.delete().then(function (res) {
                if (angular.fromJson(res.body).meta.type === 'SUCCESS') {
                    $scope.resetControlledVocabulary(true);
                }
            });
        };

        $scope.reorderControlledVocabulary = function (src, dest) {
            ControlledVocabularyRepo.reorder(src, dest);
        };

        $scope.sortControlledVocabulary = function (column) {
            if ($scope.sortAction == 'confirm') {
                $scope.sortAction = 'sort';
            } else if ($scope.sortAction == 'sort') {
                ControlledVocabularyRepo.sort(column);
                $scope.sortAction = 'confirm';
            }
        };

        $scope.uploadControlledVocabulary = function () {
            if ($scope.uploadAction == 'confirm') {
                var reader = new FileReader();
                reader.onload = function () {
                    ControlledVocabularyRepo.confirmCSV(reader.result, $scope.uploadModalData.cv.name).then(function (data) {
                        $scope.uploadWordMap = data.payload.HashMap;
                    });
                };
                reader.readAsDataURL($scope.uploadModalData.file);
                $scope.uploadAction = 'process';
            } else if ($scope.uploadAction == 'process') {
                ControlledVocabularyRepo.uploadCSV($scope.uploadModalData.cv.name).then(function (data) {
                    $scope.closeModal();
                });
                $scope.uploadAction = 'confirm';
            }
        };


        $scope.exportControlledVocabulary = function () {
            $scope.headers = [];
            return ControlledVocabularyRepo.downloadCSV($scope.uploadModalData.cv.name).then(function (data) {

                var csvMap = angular.fromJson(data.body).payload.HashMap;
                for (var key in csvMap.headers) {
                    $scope.headers.push(csvMap.headers[key]);
                }
                return csvMap.rows;
            });
        };

        $scope.filterWord = function (word) {
            return {
                name: word.name,
                definition: word.definition,
                identifier: word.identifier
            };
        };

        $scope.filterWordArray = function (words) {

            var definition = "";

            if (words[0].definition.length > 0) {
                definition += '<span class="red">' + words[0].definition + '</span>';
            }

            if (definition.length > 0 && words[1].definition.length > 0) {
                definition += '<span class="glyphicon glyphicon-arrow-right cv-change"></span><span>' + words[1].definition + '</span>';
            }

            var identifier = "";

            if (words[0].identifier.length > 0) {
                identifier += '<span class="red">' + words[0].identifier + '</span>';
            }

            if (identifier.length > 0 && words[1].identifier.length > 0) {
                identifier += '<span class="glyphicon glyphicon-arrow-right cv-change"></span><span>' + words[1].identifier + '</span>';
            }

            return {
                name: words[0].name,
                definition: definition,
                identifier: identifier
            };
        };

        $scope.beginImport = function (file) {
            if (file) {
                $scope.uploadModalData.file = file;
                $scope.openModal('#controlledVocabularyUploadModal');
            }
        };

        $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
            trashId: $scope.trashCanId,
            dragging: $scope.dragging,
            select: $scope.selectControlledVocabulary,
            model: $scope.controlledVocabulary,
            confirm: '#controlledVocabularyConfirmRemoveModal',
            reorder: $scope.reorderControlledVocabulary,
            container: '#controlled-vocabulary'
        });

    });

});
