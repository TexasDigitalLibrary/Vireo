describe('controller: CustomActionSettingsController', function () {

    var controller, q, scope, CustomActionDefinitionRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $q, $rootScope, $timeout, $window, _CustomActionDefinitionRepo_, _DragAndDropListenerFactory_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();

            q = $q;
            scope = $rootScope.$new();

            CustomActionDefinitionRepo = _CustomActionDefinitionRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('CustomActionSettingsController', {
                $q: q,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                CustomActionDefinitionRepo: _CustomActionDefinitionRepo_,
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
        module('mock.customActionDefinition');
        module('mock.customActionDefinitionRepo');
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
        it('createCustomAction should be defined', function () {
            expect(scope.createCustomAction).toBeDefined();
            expect(typeof scope.createCustomAction).toEqual("function");
        });
        it('editCustomAction should be defined', function () {
            expect(scope.editCustomAction).toBeDefined();
            expect(typeof scope.editCustomAction).toEqual("function");
        });
        it('removeCustomAction should be defined', function () {
            expect(scope.removeCustomAction).toBeDefined();
            expect(typeof scope.removeCustomAction).toEqual("function");
        });
        it('reorderCustomAction should be defined', function () {
            expect(scope.reorderCustomAction).toBeDefined();
            expect(typeof scope.reorderCustomAction).toEqual("function");
        });
        it('resetCustomAction should be defined', function () {
            expect(scope.resetCustomAction).toBeDefined();
            expect(typeof scope.resetCustomAction).toEqual("function");
        });
        it('selectCustomAction should be defined', function () {
            expect(scope.selectCustomAction).toBeDefined();
            expect(typeof scope.selectCustomAction).toEqual("function");
        });
        it('updateCustomAction should be defined', function () {
            expect(scope.updateCustomAction).toBeDefined();
            expect(typeof scope.updateCustomAction).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('createCustomAction should create a new custom action', function () {
            scope.modalData = new mockCustomActionDefinition(q);

            spyOn(CustomActionDefinitionRepo, "create");

            scope.createCustomAction();

            expect(CustomActionDefinitionRepo.create).toHaveBeenCalled();
        });
        it('editCustomAction should open a modal', function () {
            spyOn(scope, "selectCustomAction");
            spyOn(scope, "openModal");

            scope.editCustomAction(1);

            expect(scope.selectCustomAction).toHaveBeenCalled();
            expect(scope.openModal).toHaveBeenCalled();
        });
        it('removeCustomAction should delete a custom action', function () {
            scope.modalData = new mockCustomActionDefinition(q);

            spyOn(scope.modalData, "delete");

            scope.removeCustomAction();

            expect(scope.modalData.delete).toHaveBeenCalled();
        });
        it('reorderCustomAction should reorder a custom action', function () {
            spyOn(CustomActionDefinitionRepo, "reorder");

            scope.reorderCustomAction("a", "b");

            expect(CustomActionDefinitionRepo.reorder).toHaveBeenCalled();
        });
        it('resetCustomAction should reset the custom action', function () {
            var cad = new mockCustomActionDefinition(q);
            scope.forms = [];
            scope.modalData = cad;

            spyOn(scope.customActionRepo, "clearValidationResults");
            spyOn(cad, "refresh");
            spyOn(scope, "closeModal");

            scope.resetCustomAction();

            expect(scope.customActionRepo.clearValidationResults).toHaveBeenCalled();
            expect(cad.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
            expect(scope.modalData.isStudentVisible).toBe(false);
        });
        it('selectCustomAction should select a custom action', function () {
            scope.modalData = null;
            scope.customActions = [
                new mockCustomActionDefinition(q),
                new mockCustomActionDefinition(q)
            ];
            scope.customActions[1].mock(dataCustomActionDefinition2);

            scope.selectCustomAction(1);

            expect(scope.modalData).toBe(scope.customActions[1]);
        });
        it('updateCustomAction should should save a custom action', function () {
            scope.modalData = new mockCustomActionDefinition(q);

            spyOn(scope.modalData, "save");

            scope.updateCustomAction();

            expect(scope.modalData.save).toHaveBeenCalled();
        });
    });

});
