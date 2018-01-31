vireo.controller("ControlledVocabularyRepoController", function ($controller, $q, $scope, $timeout, ApiResponseActions, ControlledVocabularyRepo, DragAndDropListenerFactory, LanguageRepo, NgTableParams) {

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

    $scope.editableVW = {};

    $scope.addVocabularyWord = function (newVW) {
        newVW.adding = true;
        if(typeof newVW.contacts === 'string') {
            newVW.contacts = newVW.contacts.split(",");
        }
        ControlledVocabularyRepo.addVocabularyWord($scope.selectedCv, newVW).then(function (res) {
            $scope.lastCreatedVocabularyWordId = angular.fromJson(res.body).payload.VocabularyWord.id;
            reloadTable();
            $scope.cancelAdding(newVW);
        });
    };

    $scope.cancelAdding = function (newVW) {
        Object.keys(newVW).forEach(function (key) {
            delete newVW[key];
        });
        newVW.adding = false;
    };

    $scope.removeVocabularyWord = function (vw) {
        $scope.selectedCv.deleting = true;
        ControlledVocabularyRepo.removeVocabularyWord($scope.selectedCv, vw).then(function (res) {
            $scope.selectedCv.deleting = false;
        });
    };

    $scope.updateVocabularyWord = function (vw) {
        if(typeof vw.contacts === 'string') {
          vw.contacts = vw.contacts.split(",");
        }
        $scope.selectedCv.updating = true;
        ControlledVocabularyRepo.updateVocabularyWord($scope.selectedCv, vw).then(function (res) {
            $scope.selectedCv.updating = false;
            $scope.editableVW.editing = false;
        });
    };

    $scope.cancelCvEdits = function (vocabularyWord) {
        Object.keys(vocabularyWord).forEach(function (key) {
            $scope.editableVW[key] = vocabularyWord[key];
        });

        $scope.editableVW.editing = false;
    };

    var reloadTable = function () {
        $scope.cvTableParams = new NgTableParams({
            sorting: {
                name: "asc"
            }
        }, {
            counts: [],
            dataset: $scope.selectedCv !== undefined && $scope.selectedCv !== null ? $scope.selectedCv.dictionary : []
        });

        if ($scope.lastCreatedVocabularyWordId) {

            var rowsPerPage = $scope.cvTableParams.count();
            var alphabatizedVWs = $scope.selectedCv.dictionary.sort(function (a, b) {
                var nameA = a.name.toUpperCase();
                var nameB = b.name.toUpperCase();
                if (nameA < nameB) {
                    return -1;
                }
                if (nameA > nameB) {
                    return 1;
                }
                return 0;
            });
            $scope.selectedCv._syncShadow();

            var indexOfLastCreated = -1;
            alphabatizedVWs.some(function (pvw, i) {
                var check = pvw.id === $scope.lastCreatedVocabularyWordId;
                if (check) indexOfLastCreated = i;
                return check;
            });

            var pageOfOccurence = Math.ceil((indexOfLastCreated / rowsPerPage));

            $scope.cvTableParams.page(pageOfOccurence === 0 ? 1 : pageOfOccurence);

            $timeout(function () {
                $scope.lastCreatedVocabularyWordId = null;
            }, 5000);

        }

    };

    $scope.startEditVWMode = function (vocabularyWord, editing) {

        $scope.editableVW = angular.copy(vocabularyWord);

        $scope.editableVW.editing = true;
        $scope.editableVW.clickedCell = editing;
    };

    $scope.editMode = function (vocabularyWord) {
        return $scope.editableVW.id === vocabularyWord.id && $scope.editableVW.editing;
    };

    $scope.setSelectedCv = function (cv) {

        if ($scope.selectedCv) {
            $scope.selectedCv.clearListens();
        }

        $scope.selectedCv = cv;

        reloadTable();

        $scope.selectedCv.listen(function () {
            reloadTable();
        });
    };

    $scope.createHotKeys = function (e, newVW) {

        e.preventDefault();

        var caretLocation;

        if (e.keyCode === 40) {
            newVW.beginAdd = false;
            var nextRow = $scope.cvTableParams.data[0];
            $scope.startEditVWMode(nextRow, 'name');
        }

        if (e.keyCode === 39) {

            caretLocation = e.currentTarget.selectionStart;
            var valueLength = newVW[newVW.clickedCell] === undefined ? 0 : newVW[newVW.clickedCell].length;

            if (caretLocation === valueLength) {
                newVW.moving = true;
                if (newVW.clickedCell === "name") {
                    newVW.clickedCell = "definition";
                } else if (newVW.clickedCell === "definition") {
                    newVW.clickedCell = "identifier";
                } else if (newVW.clickedCell === "identifier") {
                    newVW.clickedCell = "contacts";
                }
                $timeout(function () {
                    newVW.moving = false;
                });
            }

        }

        if (e.keyCode === 37) {

            caretLocation = e.currentTarget.selectionStart;

            if (caretLocation === 0) {
                newVW.moving = true;
                if (newVW.clickedCell === "definition") {
                    newVW.clickedCell = "name";
                } else if (newVW.clickedCell === "identifier") {
                    newVW.clickedCell = "definition";
                } else if (newVW.clickedCell === "contacts") {
                    newVW.clickedCell = "identifier";
                }
                $timeout(function () {
                    newVW.moving = false;
                });
            }
        }

        if (e.keyCode === 27) {
            $scope.cancelAdding(newVW);
        }

        if (e.keyCode === 13) {
            $scope.addVocabularyWord(newVW);
        }

    };

    $scope.updateHotKeys = function (e, vw) {

        e.preventDefault();

        var caretLocation;
        var nextVWIndex;
        var nextVW;

        if (e.keyCode === 38) {
            $scope.editableVW.editing = false;
            nextVWIndex = -1;
            $scope.cvTableParams.data.some(function (pvw, i) {
                nextVWIndex = pvw.id === vw.id ? i - 1 : nextVWIndex;
                return pvw.id === vw.id;
            });
            nextVW = $scope.cvTableParams.data[nextVWIndex];
            if (nextVW) $scope.startEditVWMode(nextVW, 'name');
        }

        if (e.keyCode === 40) {
            $scope.editableVW.editing = false;
            nextVWIndex = -1;
            $scope.cvTableParams.data.some(function (pvw, i) {
                nextVWIndex = pvw.id === vw.id ? i + 1 : nextVWIndex;
                return pvw.id === vw.id;
            });
            nextVW = $scope.cvTableParams.data[nextVWIndex];
            if (nextVW) $scope.startEditVWMode(nextVW, 'name');
        }

        if (e.keyCode === 39) {

            caretLocation = e.currentTarget.selectionStart;
            var valueLength = !$scope.editableVW[$scope.editableVW.clickedCell] ? 0 : $scope.editableVW[$scope.editableVW.clickedCell].length;

            if (caretLocation === valueLength) {

                $scope.editableVW.moving = true;
                if ($scope.editableVW.clickedCell === "name") {
                    $scope.editableVW.clickedCell = "definition";
                } else if ($scope.editableVW.clickedCell === "definition") {
                    $scope.editableVW.clickedCell = "identifier";
                } else if ($scope.editableVW.clickedCell === "identifier") {
                    $scope.editableVW.clickedCell = "contacts";
                }
                $timeout(function () {
                    $scope.editableVW.moving = false;
                });

            }
        }

        if (e.keyCode === 37) {

            caretLocation = e.currentTarget.selectionStart;

            if (caretLocation === 0) {

                $scope.editableVW.moving = true;
                if ($scope.editableVW.clickedCell === "definition") {
                    $scope.editableVW.clickedCell = "name";
                } else if ($scope.editableVW.clickedCell === "identifier") {
                    $scope.editableVW.clickedCell = "definition";
                } else if ($scope.editableVW.clickedCell === "contacts") {
                    $scope.editableVW.clickedCell = "identifier";
                }
                $timeout(function () {
                    $scope.editableVW.moving = false;
                });
            }
        }

        if (e.keyCode === 13) {
            $scope.updateCv($scope.editableVW);
        }

        if (e.keyCode === 27) {
            $scope.cancelCvEdits(vw);
        }

    };

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

        ControlledVocabularyRepo.listen(ApiResponseActions.CHANGE, function () {
            if ($scope.uploadAction != "process") {
                $scope.uploadStatus();
                $scope.uploadModalData = {
                    cv: $scope.controlledVocabulary[getDefaultIndex()]
                };
            }
        });

        $scope.resetControlledVocabulary = function (closeModal) {

            $scope.setSelectedCv($scope.controlledVocabulary[getDefaultIndex()]);

            reloadTable();

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

        $scope.createControlledVocabulary = function () {
            ControlledVocabularyRepo.create($scope.modalData).then(function (res) {
                if (angular.fromJson(res.body).meta.status === 'SUCCESS') {
                    $scope.resetControlledVocabulary(true);
                }
            });
        };

        $scope.uploadStatus = function () {
            if ($scope.uploadModalData.cv !== undefined) {
                ControlledVocabularyRepo.status($scope.uploadModalData.cv.name).then(function (data) {
                    $scope.uploadModalData.cv.inProgress = angular.fromJson(data.body).payload.Boolean;
                    $scope.uploadModalData.cv._syncShadow();
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
            $scope.modalData.save().then(function (res) {
                if (angular.fromJson(res.body).meta.status === 'SUCCESS') {
                    $scope.resetControlledVocabulary(true);
                }
            });
        };

        $scope.removeControlledVocabulary = function () {
            $scope.modalData.delete().then(function (res) {
                if (angular.fromJson(res.body).meta.status === 'SUCCESS') {
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
                ControlledVocabularyRepo.confirmCSV($scope.uploadModalData.file, $scope.uploadModalData.cv.name).then(function (response) {
                    $scope.uploadWordMap = response.data.payload.HashMap;
                });
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
                identifier: word.identifier,
                contacts: word.contacts
            };
        };

        $scope.filterWordArray = function (words) {

            var definition = "";
            if(words[0].definition === words[1].definition) {
                definition += '<span>' + words[0].definition + '</span>';
            } else {
                if (words[0].definition.length > 0) {
                    definition += '<span class="red">' + words[0].definition + '</span>';
                }
                if (definition.length > 0 && words[1].definition.length > 0) {
                    definition += '<span class="glyphicon glyphicon-arrow-right cv-change"></span><span>' + words[1].definition + '</span>';
                }
            }


            var identifier = "";
            if(words[0].identifier === words[1].identifier) {
                identifier += '<span>' + words[0].identifier + '</span>';
            } else {
                if (words[0].identifier.length > 0 && words[0].identifier !== words[1].identifier) {
                    identifier += '<span class="red">' + words[0].identifier + '</span>';
                }
                if (identifier.length > 0 && words[1].identifier.length > 0 && words[0].identifier !== words[1].identifier) {
                    identifier += '<span class="glyphicon glyphicon-arrow-right cv-change"></span><span>' + words[1].identifier + '</span>';
                }
            }

            var contacts = "";
            if(words[0].contacts === words[1].contacts) {
                contacts += '<span>' + words[0].contacts + '</span>';
            } else {
                if (words[0].contacts.length > 0 && contacts !== words[1].contacts) {
                    contacts += '<span class="red">' + words[0].contacts + '</span>';
                }
                if (contacts.length > 0 && words[1].contacts.length > 0 && contacts !== words[1].contacts) {
                    contacts += '<span class="glyphicon glyphicon-arrow-right cv-change"></span><span>' + words[1].contacts + '</span>';
                }
            }

            return {
                name: words[0].name,
                definition: definition,
                identifier: identifier,
                contacts: contacts
            };
        };

        $scope.beginImport = function (files) {
            if (files) {
                $scope.uploadModalData.file = files[0];
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
            container: '#controlled-vocabularies'
        });

    });

});
