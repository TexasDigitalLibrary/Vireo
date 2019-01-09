describe('controller: DocumentTypesController', function () {

    var controller, q, scope, DocumentTypeRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $q, $rootScope, $window, _DocumentTypeRepo_, _DragAndDropListenerFactory_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            q = $q;

            DocumentTypeRepo = _DocumentTypeRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('DocumentTypesController', {
                $scope: scope,
                $window: $window,
                DocumentTypeRepo: _DocumentTypeRepo_,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                ModalService: _ModalService_,
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
        module('mock.documentType');
        module('mock.documentTypeRepo');
        module('mock.dragAndDropListenerFactory');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.storageService');
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
        it('createNewDocumentType should be defined', function () {
            expect(scope.createNewDocumentType).toBeDefined();
            expect(typeof scope.createNewDocumentType).toEqual("function");
        });
        it('launchEditModal should be defined', function () {
            expect(scope.launchEditModal).toBeDefined();
            expect(typeof scope.launchEditModal).toEqual("function");
        });
        it('removeDocumentType should be defined', function () {
            expect(scope.removeDocumentType).toBeDefined();
            expect(typeof scope.removeDocumentType).toEqual("function");
        });
        it('reorderDocumentTypes should be defined', function () {
            expect(scope.reorderDocumentTypes).toBeDefined();
            expect(typeof scope.reorderDocumentTypes).toEqual("function");
        });
        it('resetDocumentTypes should be defined', function () {
            expect(scope.resetDocumentTypes).toBeDefined();
            expect(typeof scope.resetDocumentTypes).toEqual("function");
        });
        it('selectDocumentType should be defined', function () {
            expect(scope.selectDocumentType).toBeDefined();
            expect(typeof scope.selectDocumentType).toEqual("function");
        });
        it('sortDocumentTypes should be defined', function () {
            expect(scope.sortDocumentTypes).toBeDefined();
            expect(typeof scope.sortDocumentTypes).toEqual("function");
        });
        it('updateDocumentType should be defined', function () {
            expect(scope.updateDocumentType).toBeDefined();
            expect(typeof scope.updateDocumentType).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('createNewDocumentType should create a new custom action', function () {
            scope.modalData = new mockDocumentType(q);

            spyOn(DocumentTypeRepo, "create");

            scope.createNewDocumentType();

            expect(DocumentTypeRepo.create).toHaveBeenCalled();
        });
        it('launchEditModal should open a modal', function () {
            var documentType = new mockDocumentType(q);
            scope.documentTypes = [
                documentType
            ];

            spyOn(scope, "openModal");

            scope.launchEditModal(1);

            expect(scope.modalData).toBe(documentType);
            expect(scope.openModal).toHaveBeenCalled();
        });
        it('removeDocumentType should delete a custom action', function () {
            scope.modalData = new mockDocumentType(q);

            spyOn(scope.modalData, "delete");

            scope.removeDocumentType();

            expect(scope.modalData.delete).toHaveBeenCalled();
        });
        it('reorderDocumentTypes should reorder a custom action', function () {
            spyOn(DocumentTypeRepo, "reorder");

            scope.reorderDocumentTypes("a", "b");

            expect(DocumentTypeRepo.reorder).toHaveBeenCalled();
        });
        it('resetDocumentTypes should reset the custom action', function () {
            var documentType = new mockDocumentType(q);
            scope.forms = [];
            scope.modalData = documentType;
            scope.modalData.degreeLevel = null;

            spyOn(scope.documentTypeRepo, "clearValidationResults");
            spyOn(documentType, "refresh");
            spyOn(scope, "closeModal");

            scope.resetDocumentTypes();

            expect(scope.documentTypeRepo.clearValidationResults).toHaveBeenCalled();
            expect(documentType.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
            expect(typeof scope.modalData.degreeLevel).toBe("string");
        });
        it('selectDocumentType should select a custom action', function () {
            scope.modalData = null;
            scope.documentTypes = [
                new mockDocumentType(q),
                new mockDocumentType(q)
            ];
            scope.documentTypes[1].mock(dataDocumentType2);

            scope.selectDocumentType(1);

            expect(scope.modalData).toBe(scope.documentTypes[1]);
        });
        it('sortDocumentTypes should select a sort action', function () {
            scope.sortAction = "confirm";

            spyOn(DocumentTypeRepo, "sort");

            scope.sortDocumentTypes("column");

            expect(scope.sortAction).toEqual("sort");
            expect(DocumentTypeRepo.sort).not.toHaveBeenCalled();

            scope.sortDocumentTypes("column");

            expect(scope.sortAction).toEqual("confirm");
            expect(DocumentTypeRepo.sort).toHaveBeenCalled();
        });
        it('updateDocumentType should should save a custom action', function () {
            scope.modalData = new mockDocumentType(q);

            spyOn(scope.modalData, "save");

            scope.updateDocumentType();

            expect(scope.modalData.save).toHaveBeenCalled();
        });
    });

});
