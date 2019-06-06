describe('controller: ControlledVocabularyRepoController', function () {

    var controller, compile, q, scope, timeout, ControlledVocabularyRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $compile, $q, $rootScope, $timeout, _ControlledVocabularyRepo_, _DragAndDropListenerFactory_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            compile = $compile;
            q = $q;
            scope = $rootScope.$new();
            timeout = $timeout;

            ControlledVocabularyRepo = _ControlledVocabularyRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('ControlledVocabularyRepoController', {
                $q: q,
                $scope: scope,
                $timeout: timeout,
                $window: mockWindow(),
                ControlledVocabularyRepo: _ControlledVocabularyRepo_,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                ModalService: _ModalService_,
                NgTableParams: mockNgTableParams,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                WsApi: _WsApi_
            });

            // ensure that the isReady() is called.
            if (!scope.$$phase) {
                scope.$digest();
            }
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.controlledVocabulary');
        module('mock.controlledVocabularyRepo');
        module('mock.dragAndDropListenerFactory');
        module('mock.modalService');
        module('mock.ngTableParams');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.vocabularyWord');
        module('mock.wsApi');

        installPromiseMatchers();
        initializeController();
    });

    describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });

    describe('Are the scope methods defined', function () {
        it('addVocabularyWord should be defined', function () {
            expect(scope.addVocabularyWord).toBeDefined();
            expect(typeof scope.addVocabularyWord).toEqual("function");
        });
        it('beginImport should be defined', function () {
            expect(scope.beginImport).toBeDefined();
            expect(typeof scope.beginImport).toEqual("function");
        });
        it('cancelAdding should be defined', function () {
            expect(scope.cancelAdding).toBeDefined();
            expect(typeof scope.cancelAdding).toEqual("function");
        });
        it('createControlledVocabulary should be defined', function () {
            expect(scope.createControlledVocabulary).toBeDefined();
            expect(typeof scope.createControlledVocabulary).toEqual("function");
        });
        it('cancelCvEdits should be defined', function () {
            expect(scope.cancelCvEdits).toBeDefined();
            expect(typeof scope.cancelCvEdits).toEqual("function");
        });
        it('createHotKeys should be defined', function () {
            expect(scope.createHotKeys).toBeDefined();
            expect(typeof scope.createHotKeys).toEqual("function");
        });
        it('editControlledVocabulary should be defined', function () {
            expect(scope.editControlledVocabulary).toBeDefined();
            expect(typeof scope.editControlledVocabulary).toEqual("function");
        });
        it('editMode should be defined', function () {
            expect(scope.editMode).toBeDefined();
            expect(typeof scope.editMode).toEqual("function");
        });
        it('exportControlledVocabulary should be defined', function () {
            expect(scope.exportControlledVocabulary).toBeDefined();
            expect(typeof scope.exportControlledVocabulary).toEqual("function");
        });
        it('filterWord should be defined', function () {
            expect(scope.filterWord).toBeDefined();
            expect(typeof scope.filterWord).toEqual("function");
        });
        it('filterWordArray should be defined', function () {
            expect(scope.filterWordArray).toBeDefined();
            expect(typeof scope.filterWordArray).toEqual("function");
        });
        it('refreshControlledVocabulary should be defined', function () {
            expect(scope.refreshControlledVocabulary).toBeDefined();
            expect(typeof scope.refreshControlledVocabulary).toEqual("function");
        });
        it('removeControlledVocabulary should be defined', function () {
            expect(scope.removeControlledVocabulary).toBeDefined();
            expect(typeof scope.removeControlledVocabulary).toEqual("function");
        });
        it('removeVocabularyWord should be defined', function () {
            expect(scope.removeVocabularyWord).toBeDefined();
            expect(typeof scope.removeVocabularyWord).toEqual("function");
        });
        it('reorderControlledVocabulary should be defined', function () {
            expect(scope.reorderControlledVocabulary).toBeDefined();
            expect(typeof scope.reorderControlledVocabulary).toEqual("function");
        });
        it('resetControlledVocabulary should be defined', function () {
            expect(scope.resetControlledVocabulary).toBeDefined();
            expect(typeof scope.resetControlledVocabulary).toEqual("function");
        });
        it('selectControlledVocabulary should be defined', function () {
            expect(scope.selectControlledVocabulary).toBeDefined();
            expect(typeof scope.selectControlledVocabulary).toEqual("function");
        });
        it('setSelectedCv should be defined', function () {
            expect(scope.setSelectedCv).toBeDefined();
            expect(typeof scope.setSelectedCv).toEqual("function");
        });
        it('sortControlledVocabulary should be defined', function () {
            expect(scope.sortControlledVocabulary).toBeDefined();
            expect(typeof scope.sortControlledVocabulary).toEqual("function");
        });
        it('startEditVWMode should be defined', function () {
            expect(scope.startEditVWMode).toBeDefined();
            expect(typeof scope.startEditVWMode).toEqual("function");
        });
        it('updateControlledVocabulary should be defined', function () {
            expect(scope.updateControlledVocabulary).toBeDefined();
            expect(typeof scope.updateControlledVocabulary).toEqual("function");
        });
        it('uploadControlledVocabulary should be defined', function () {
            expect(scope.uploadControlledVocabulary).toBeDefined();
            expect(typeof scope.uploadControlledVocabulary).toEqual("function");
        });
        it('updateHotKeys should be defined', function () {
            expect(scope.updateHotKeys).toBeDefined();
            expect(typeof scope.updateHotKeys).toEqual("function");
        });
        it('updateVocabularyWord should be defined', function () {
            expect(scope.updateVocabularyWord).toBeDefined();
            expect(typeof scope.updateVocabularyWord).toEqual("function");
        });
        it('uploadStatus should be defined', function () {
            expect(scope.uploadStatus).toBeDefined();
            expect(typeof scope.uploadStatus).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('addVocabularyWord should add a vocabulary word', function () {
            var vw = new mockVocabularyWord(q);
            scope.lastCreatedVocabularyWordId = null;
            scope.cvTableParams = new mockNgTableParams(q);

            spyOn(scope, "cancelAdding");

            scope.addVocabularyWord(vw);
            scope.$digest();

            expect(scope.lastCreatedVocabularyWordId).toBe(vw.id);
            expect(vw.adding).toBe(true);
            expect(typeof vw.contacts).toBe("object");
            expect(scope.cancelAdding).toHaveBeenCalled();

            vw.contacts = [];
            scope.addVocabularyWord(vw);
            scope.$digest();
        });
        it('beginImport should open a modal', function () {
            var originalMethod = scope.openModal;

            spyOn(scope, "openModal");

            scope.beginImport(false);
            expect(scope.openModal).not.toHaveBeenCalled();

            scope.openModal = originalMethod;
            spyOn(scope, "openModal");

            scope.beginImport(["test.file"]);
            expect(scope.uploadModalData.file).toBe("test.file");
            expect(scope.openModal).toHaveBeenCalled();
        });
        it('cancelAdding should cancel adding a vocabulary word', function () {
            var vw = new mockVocabularyWord(q);
            vw.adding = true;

            scope.cancelAdding(vw);
            expect(vw.adding).toBe(false);
        });
        it('createControlledVocabulary to create a controlled vocabulary', function () {
            scope.modalData = new mockControlledVocabulary(q);

            spyOn(scope, "resetControlledVocabulary");

            scope.createControlledVocabulary();
            scope.$digest();
            expect(scope.resetControlledVocabulary).toHaveBeenCalled();

            ControlledVocabularyRepo.create = function (model) {
                return payloadPromise(q.defer(), model, "OTHER");
            };

            scope.createControlledVocabulary();
            scope.$digest();
        });
        it('cancelCvEdits to cancel editing', function () {
            var vw = new mockVocabularyWord(q);
            scope.editableVW = new mockVocabularyWord(q);
            scope.editableVW.editing = true;

            scope.cancelCvEdits(vw);
            expect(scope.editableVW.editing).toBe(false);
        });
        it('createHotKeys to operate hotkeys', function () {
            var vw = new mockVocabularyWord(q);
            var vw2 = new mockVocabularyWord(q);
            var e = {
                currentTarget: {
                    selectionStart: 0
                },
                preventDefault: function() {}
            };
            scope.cvTableParams = new mockNgTableParams(q);

            vw2.mock(dataVocabularyWord2);

            spyOn(scope, "startEditVWMode");
            spyOn(scope, "cancelAdding");
            spyOn(scope, "addVocabularyWord");

            e.keyCode = 40;
            scope.createHotKeys(e, vw);

            e.keyCode = 39;
            vw.clickedCell = "name";
            e.currentTarget.selectionStart = vw.name.length;
            scope.createHotKeys(e, vw);
            expect(vw.clickedCell).toEqual("definition");

            e.currentTarget.selectionStart = vw.definition.length;
            scope.createHotKeys(e, vw);
            expect(vw.clickedCell).toEqual("identifier");

            e.currentTarget.selectionStart = vw.identifier.length;
            scope.createHotKeys(e, vw);
            expect(vw.clickedCell).toEqual("contacts");

            e.currentTarget.selectionStart = 1000;
            vw2.clickedCell = null;
            scope.createHotKeys(e, vw2);

            e.currentTarget.selectionStart = 0;
            vw2.clickedCell = "unknown";
            scope.createHotKeys(e, vw2);
            timeout.flush();

            e.keyCode = 37;
            e.currentTarget.selectionStart = 0;
            scope.createHotKeys(e, vw);
            expect(vw.clickedCell).toEqual("identifier");

            scope.createHotKeys(e, vw);
            expect(vw.clickedCell).toEqual("definition");

            scope.createHotKeys(e, vw);
            expect(vw.clickedCell).toEqual("name");

            e.currentTarget.selectionStart = 1000;
            vw2.clickedCell = null;
            scope.createHotKeys(e, vw2);

            e.currentTarget.selectionStart = 0;
            vw2.clickedCell = "unknown";
            scope.createHotKeys(e, vw2);
            timeout.flush();

            e.keyCode = 27;
            scope.createHotKeys(e, vw);

            e.keyCode = 13;
            scope.createHotKeys(e, vw);
            scope.$digest();

            expect(scope.startEditVWMode).toHaveBeenCalled();
            expect(scope.cancelAdding).toHaveBeenCalled();
            expect(scope.addVocabularyWord).toHaveBeenCalled();
        });
        it('editControlledVocabulary to open a modal', function () {
            spyOn(scope, "openModal");
            spyOn(scope, "selectControlledVocabulary");

            scope.editControlledVocabulary(1);

            expect(scope.openModal).toHaveBeenCalled();
            expect(scope.selectControlledVocabulary).toHaveBeenCalled();
        });
        it('editMode to return a boolean', function () {
            var result;
            var vw = new mockVocabularyWord(q);
            scope.editableVW = new mockVocabularyWord(q);
            scope.editableVW.editing = true;

            result = scope.editMode(vw);
            expect(result).toBe(true);

            scope.editableVW.editing = false;
            result = scope.editMode(vw);
            expect(result).toBe(false);

            vw.mock(dataVocabularyWord2);
            scope.editableVW.editing = true;
            result = scope.editMode(vw);
            expect(result).toBe(false);

            scope.editableVW.editing = false;
            result = scope.editMode(vw);
            expect(result).toBe(false);
        });
        it('exportControlledVocabulary should export a controlled vocabulary', function () {
            var result;
            scope.headers = [];
            scope.uploadModalData = {
                cv: new mockControlledVocabulary(q)
            };

            scope.exportControlledVocabulary().then(function (data) {
                result = data;
            });
            scope.$digest();

            expect(result).toBe(0);

            // TODO: mock this ControlledVocabularyRepo.downloadCSV() to get csvMap.headers of size > 1
            ControlledVocabularyRepo.downloadCSV = function (controlledVocabulary) {
                var payload = {
                    HashMap: {
                        headers: {a: "a", b: "b"},
                        rows: 1
                    }
                };

                return payloadPromise(q.defer(), payload);
            };

            scope.headers = ["a", "b"];
            scope.exportControlledVocabulary();
            scope.$digest();
        });
        it('filterWord should filter a vocabulary word', function () {
            var result;
            var vw = new mockVocabularyWord(q);

            result = scope.filterWord(vw);

            expect(result.name).toBe(vw.name);
            expect(result.definition).toBe(vw.definition);
            expect(result.identifier).toBe(vw.identifier);
            expect(result.contacts).toBe(vw.contacts);
        });
        it('filterWordArray should filter multiple vocabulary words', function () {
            var result;
            var vw1 = new mockVocabularyWord(q);
            var vw2 = new mockVocabularyWord(q);

            vw1.definition = "identical definition";
            vw1.identifier = "identical identifier";
            vw1.contacts = "identical contacts";

            vw2.definition = "identical definition";
            vw2.identifier = "identical identifier";
            vw2.contacts = "identical contacts";

            result = scope.filterWordArray([vw1, vw2]);

            expect(result.name).toBe(vw1.name);
            expect(result.definition).toEqual("<span>" + vw1.definition + "</span>");
            expect(result.identifier).toEqual("<span>" + vw1.identifier + "</span>");
            expect(result.contacts).toEqual("<span>" + vw1.contacts + "</span>");

            vw2.definition = "different definition";
            vw2.identifier = "different identifier";
            vw2.contacts = "different contacts";
            result = scope.filterWordArray([vw1, vw2]);

            expect(result.definition).not.toEqual("<span>" + vw1.definition + "</span>");
            expect(result.identifier).not.toEqual("<span>" + vw1.identifier + "</span>");
            expect(result.contacts).not.toEqual("<span>" + vw1.contacts + "</span>");

            vw1.definition = "";
            vw1.identifier = "";
            vw1.contacts = "";
            scope.filterWordArray([vw1, vw2]);
        });
        it('refreshControlledVocabulary refresh a controlled vocabulary', function () {
            var modalDataRefresh;
            var vw1 = new mockVocabularyWord(q);

            scope.forms = [];
            scope.uploadAction = "process";
            scope.modalData = new mockControlledVocabulary(q);

            spyOn(scope, "setSelectedCv");
            spyOn(scope, "uploadStatus");
            spyOn(scope.modalData, "refresh");
            spyOn(ControlledVocabularyRepo, "cancel");
            modalDataRefresh = scope.modalData.refresh;

            scope.refreshControlledVocabulary();

            expect(scope.setSelectedCv).toHaveBeenCalled();
            expect(scope.uploadStatus).toHaveBeenCalled();
            expect(ControlledVocabularyRepo.cancel).toHaveBeenCalled();
            expect(modalDataRefresh).toHaveBeenCalled();
            expect(scope.modalData).toEqual({});

            scope.forms.myForm = mockForms();
            scope.refreshControlledVocabulary();

            scope.forms.myForm.$pristine = false;
            scope.refreshControlledVocabulary();
        });
        it('removeControlledVocabulary should reset a controlled vocabulary', function () {
            scope.modalData = new mockControlledVocabulary(q);

            spyOn(scope, "resetControlledVocabulary");

            scope.removeControlledVocabulary();
            scope.$digest();

            expect(scope.resetControlledVocabulary).toHaveBeenCalled();
            expect(scope.modalData.updateRequested).toBe(true);

            scope.modalData.delete = function () {
                return payloadPromise(q.defer(), {}, "OTHER");
            };

            scope.removeControlledVocabulary();
            scope.$digest();
        });
        it('removeVocabularyWord should remove a controlled vocabulary', function () {
            scope.selectedCv = new mockControlledVocabulary(q);

            spyOn(ControlledVocabularyRepo, "removeVocabularyWord").and.callThrough();

            scope.removeVocabularyWord(new mockVocabularyWord(q));
            scope.$digest();

            expect(ControlledVocabularyRepo.removeVocabularyWord).toHaveBeenCalled();
            expect(scope.selectedCv.deleting).toBe(false);
        });
        it('reorderControlledVocabulary should reorder the controlled vocabulary', function () {
            spyOn(ControlledVocabularyRepo, "reorder");

            scope.reorderControlledVocabulary("a", "b");

            expect(ControlledVocabularyRepo.reorder).toHaveBeenCalled();
        });
        it('resetControlledVocabulary should refresh the controlled vocabulary', function () {
            spyOn(scope, "refreshControlledVocabulary");
            spyOn(scope, "closeModal");

            scope.resetControlledVocabulary(true);

            expect(scope.refreshControlledVocabulary).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();

            scope.closeModal = function() {};
            spyOn(scope, "closeModal");

            scope.resetControlledVocabulary(false);

            expect(scope.closeModal).not.toHaveBeenCalled();
        });
        it('selectControlledVocabulary should select the controlled vocabulary', function () {
            scope.modalData = null;
            scope.controlledVocabularies = [
                new mockControlledVocabulary(q)
            ];

            scope.selectControlledVocabulary(0);

            expect(scope.modalData).toBe(scope.controlledVocabularies[0]);

            scope.selectControlledVocabulary(1);

            expect(scope.modalData).not.toBeDefined();
        });
        it('setSelectedCv should select the controlled vocabulary', function () {
            var cv1 = new mockControlledVocabulary(q);
            var cv2 = new mockControlledVocabulary(q);
            var cv4 = new mockControlledVocabulary(q);
            scope.selectedCv = cv1;

            cv2.mock(dataControlledVocabulary2);
            cv4.mock(dataControlledVocabulary4);
            cv4.dictionary.push(cv4.dictionary[0]);
            cv4.dictionary[cv4.dictionary.length - 1].name = cv4.dictionary[0].name;

            scope.setSelectedCv(cv2, true);
            expect(scope.selectedCv.id).toBe(cv2.id);

            scope.setSelectedCv(null, false);

            scope.lastCreatedVocabularyWordId = cv2.id;
            scope.setSelectedCv(cv1, true);

            cv4.listen = function(func) {
                func();
            };
            scope.setSelectedCv(cv4, true);
            timeout.flush();
        });
        it('sortControlledVocabulary should sort the controlled vocabulary', function () {
            testUtility.repoSorting(scope, ControlledVocabularyRepo, scope.sortControlledVocabulary);
        });
        it('startEditVWMode should assign the editable vocabulary word', function () {
            var vw1 = new mockVocabularyWord(q);
            var vw2 = new mockVocabularyWord(q);
            vw2.mock(dataVocabularyWord2);
            scope.editableVW = null;

            scope.startEditVWMode(vw1, true);

            expect(scope.editableVW.editing).toBe(true);
            expect(scope.editableVW.id).toBe(vw1.id);
            expect(scope.editableVW.clickedCell).toBe(true);

            scope.startEditVWMode(vw2, false);

            expect(scope.editableVW.id).toBe(vw2.id);
            expect(scope.editableVW.clickedCell).toBe(false);
        });
        it('updateControlledVocabulary should update the controlled vocabulary', function () {
            scope.modalData = new mockControlledVocabulary(q);

            spyOn(scope, "resetControlledVocabulary");

            scope.updateControlledVocabulary();
            scope.$digest();

            expect(scope.resetControlledVocabulary).toHaveBeenCalled();

            scope.modalData.save = function () {
                return payloadPromise(q.defer(), {}, "OTHER");
            };

            scope.updateControlledVocabulary();
            scope.$digest();
        });
        it('uploadControlledVocabulary should upload the controlled vocabulary', function () {
            scope.modalData = new mockControlledVocabulary(q);
            scope.uploadModalData = {
                cv: scope.modalData
            };
            scope.uploadWordMap = null;

            scope.uploadAction = "confirm";
            scope.uploadControlledVocabulary();
            scope.$digest();

            expect(scope.uploadAction).toEqual("process");

            spyOn(scope, "closeModal");

            scope.uploadAction = "process";
            scope.uploadControlledVocabulary();
            scope.$digest();

            expect(scope.closeModal).toHaveBeenCalled();
            expect(scope.uploadAction).toEqual("confirm");

            scope.uploadAction = "unknown";
            scope.uploadControlledVocabulary();
            scope.$digest();
        });
        it('updateHotKeys should operate hotkeys', function () {
            var vw1 = new mockVocabularyWord(q);
            var vw2 = new mockVocabularyWord(q);
            var e = {
                currentTarget: {
                    selectionStart: 0
                },
                preventDefault: function() {}
            };

            vw2.mock(dataVocabularyWord2);

            scope.cvTableParams = new mockNgTableParams(q);
            scope.cvTableParams.data.push(vw1);
            scope.cvTableParams.data.push(vw2);
            scope.editableVW = vw1;
            scope.editableVW.editing = true;

            spyOn(scope, "startEditVWMode");
            spyOn(scope, "updateControlledVocabulary");
            spyOn(scope, "cancelCvEdits");

            e.keyCode = 40;
            scope.updateHotKeys(e, vw1);
            scope.updateHotKeys(e, vw2);

            e.keyCode = 39;
            vw1.clickedCell = "name";
            e.currentTarget.selectionStart = vw1.name.length;
            scope.updateHotKeys(e, vw1);
            expect(scope.editableVW.clickedCell).toEqual("definition");

            e.currentTarget.selectionStart = vw1.definition.length;
            scope.updateHotKeys(e, vw1);
            expect(scope.editableVW.clickedCell).toEqual("identifier");

            e.currentTarget.selectionStart = vw1.identifier.length;
            scope.updateHotKeys(e, vw1);
            expect(scope.editableVW.clickedCell).toEqual("contacts");

            scope.editableVW = vw2;
            e.currentTarget.selectionStart = 1000;
            vw2.clickedCell = null;
            scope.updateHotKeys(e, vw2);

            e.currentTarget.selectionStart = 0;
            vw2.clickedCell = "unknown";
            scope.updateHotKeys(e, vw2);
            timeout.flush();

            e.keyCode = 38;
            scope.editableVW = vw1;
            scope.updateHotKeys(e, vw1);
            scope.updateHotKeys(e, vw2);

            e.keyCode = 37;
            e.currentTarget.selectionStart = 0;
            scope.updateHotKeys(e, vw1);
            expect(scope.editableVW.clickedCell).toEqual("identifier");

            scope.updateHotKeys(e, vw1);
            expect(scope.editableVW.clickedCell).toEqual("definition");

            scope.updateHotKeys(e, vw1);
            expect(scope.editableVW.clickedCell).toEqual("name");

            scope.updateHotKeys(e, vw2);
            timeout.flush();

            e.currentTarget.selectionStart = 1;
            scope.updateHotKeys(e, vw2);

            e.keyCode = 27;
            scope.updateHotKeys(e, vw1);
            scope.updateHotKeys(e, vw2);

            e.keyCode = 13;
            scope.updateHotKeys(e, vw1);
            scope.updateHotKeys(e, vw2);

            expect(scope.startEditVWMode).toHaveBeenCalled();
            expect(scope.updateControlledVocabulary).toHaveBeenCalled();
            expect(scope.cancelCvEdits).toHaveBeenCalled();
            expect(scope.editableVW.editing).toBe(false);
        });
        it('updateVocabularyWord should update the vocabulary word', function () {
            var vw = new mockVocabularyWord(q);
            scope.selectedCv = vw;
            scope.editableVW = vw;
            scope.editableVW.editing = true;

            scope.updateVocabularyWord(vw);
            scope.$digest();

            expect(scope.selectedCv.updating).toBe(false);
            expect(scope.editableVW.editing).toBe(false);
            expect(typeof vw.contacts).toBe("object");

            vw.contacts = [];
            scope.updateVocabularyWord(vw);
            scope.$digest();
        });
        it('uploadStatus should update in progress and sync', function () {
            scope.uploadModalData = {
                cv: new mockControlledVocabulary(q)
            };

            scope.uploadModalData.cv.inProgress = null;

            spyOn(scope.uploadModalData.cv, "_syncShadow");

            scope.uploadStatus();
            scope.$digest();

            expect(scope.uploadModalData.cv._syncShadow).toHaveBeenCalled();
            expect(typeof scope.uploadModalData.cv.inProgress).toBe("boolean");

            scope.uploadModalData.cv = null;
            scope.uploadStatus();
            scope.$digest();
        });
    });

});
