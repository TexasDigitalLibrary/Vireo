describe('controller: EmbargoRepoController', function () {

    var controller, q, scope, EmbargoRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $filter, $q, $rootScope, $window, _DragAndDropListenerFactory_, _EmbargoRepo_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            q = $q;
            scope = $rootScope.$new();

            EmbargoRepo = _EmbargoRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('EmbargoRepoController', {
                $filter: $filter,
                $q: q,
                $scope: scope,
                $window: $window,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                EmbargoRepo: _EmbargoRepo_,
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
        module('mock.dragAndDropListenerFactory');
        module('mock.embargo');
        module('mock.embargoRepo');
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
        it('createEmbargo should be defined', function () {
            expect(scope.createEmbargo).toBeDefined();
            expect(typeof scope.createEmbargo).toEqual("function");
        });
        it('editEmbargo should be defined', function () {
            expect(scope.editEmbargo).toBeDefined();
            expect(typeof scope.editEmbargo).toEqual("function");
        });
        it('removeEmbargo should be defined', function () {
            expect(scope.removeEmbargo).toBeDefined();
            expect(typeof scope.removeEmbargo).toEqual("function");
        });
        it('reorderEmbargoDefault should be defined', function () {
            expect(scope.reorderEmbargoDefault).toBeDefined();
            expect(typeof scope.reorderEmbargoDefault).toEqual("function");
        });
        it('reorderEmbargoProquest should be defined', function () {
            expect(scope.reorderEmbargoProquest).toBeDefined();
            expect(typeof scope.reorderEmbargoProquest).toEqual("function");
        });
        it('resetEmbargo should be defined', function () {
            expect(scope.resetEmbargo).toBeDefined();
            expect(typeof scope.resetEmbargo).toEqual("function");
        });
        it('selectEmbargo should be defined', function () {
            expect(scope.selectEmbargo).toBeDefined();
            expect(typeof scope.selectEmbargo).toEqual("function");
        });
        it('sortEmbargoesDefault should be defined', function () {
            expect(scope.sortEmbargoesDefault).toBeDefined();
            expect(typeof scope.sortEmbargoesDefault).toEqual("function");
        });
        it('sortEmbargoesProquest should be defined', function () {
            expect(scope.sortEmbargoesProquest).toBeDefined();
            expect(typeof scope.sortEmbargoesProquest).toEqual("function");
        });
        it('updateEmbargo should be defined', function () {
            expect(scope.updateEmbargo).toBeDefined();
            expect(typeof scope.updateEmbargo).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('createEmbargo should create a new embargo', function () {
            scope.modalData = new mockEmbargo(q);

            spyOn(EmbargoRepo, "create").and.callThrough();

            scope.createEmbargo();

            expect(EmbargoRepo.create).toHaveBeenCalled();
        });
        it('editEmbargo should open a modal', function () {
            spyOn(scope, "selectEmbargo");
            spyOn(scope, "openModal");

            scope.editEmbargo(1);

            expect(scope.selectEmbargo).toHaveBeenCalled();
            expect(scope.openModal).toHaveBeenCalled();
        });
        it('removeEmbargo should delete a embargo', function () {
            scope.modalData = new mockEmbargo(q);

            spyOn(scope.modalData, "delete");

            scope.removeEmbargo();

            expect(scope.modalData.delete).toHaveBeenCalled();
        });
        it('reorderEmbargoDefault should reorder a defult embargo', function () {
            spyOn(EmbargoRepo, "reorder");

            scope.reorderEmbargoDefault("a", "b");

            expect(EmbargoRepo.reorder).toHaveBeenCalled();
        });
        it('reorderEmbargoProquest should reorder a proquest embargo', function () {
            spyOn(EmbargoRepo, "reorder");

            scope.reorderEmbargoProquest("a", "b");

            expect(EmbargoRepo.reorder).toHaveBeenCalled();
        });
        it('resetEmbargo should reset the embargo', function () {
            var embargo = new mockEmbargo(q);
            scope.forms = [];
            scope.modalData = embargo;
            scope.modalData.level = null;
            scope.proquestEmbargoes = null;
            scope.defaultEmbargoes = null;

            spyOn(scope.embargoRepo, "clearValidationResults");
            spyOn(embargo, "refresh");
            spyOn(scope, "closeModal");

            scope.resetEmbargo();

            expect(scope.embargoRepo.clearValidationResults).toHaveBeenCalled();
            expect(embargo.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
            expect(scope.modalData).toBeDefined();
            expect(scope.proquestEmbargoes).toBeDefined();
            expect(scope.defaultEmbargoes).toBeDefined();
        });
        it('selectEmbargo should select a embargo', function () {
            scope.modalData = null;
            scope.embargos = [
                new mockEmbargo(q),
                new mockEmbargo(q)
            ];
            scope.embargos[1].mock(dataEmbargo2);

            // FIXME: the implementation of this is different from other similar implementations in that this selects an ID instead of an index.
            scope.selectEmbargo(2);

            expect(scope.modalData.id).toBe(scope.embargos[1].id);
        });
        it('sortEmbargoesDefault should select a sort action', function () {
            scope.sortAction = "neither";

            spyOn(EmbargoRepo, "sort");

            scope.sortEmbargoesDefault("column");

            expect(scope.sortAction).toEqual("sortDefaultEmbargoes");
            expect(EmbargoRepo.sort).not.toHaveBeenCalled();

            scope.sortEmbargoesDefault("column");

            expect(scope.sortAction).toEqual("confirm");
            expect(EmbargoRepo.sort).toHaveBeenCalled();
        });
        it('sortEmbargoesProquest should select a sort action', function () {
            scope.sortAction = "neither";

            spyOn(EmbargoRepo, "sort");

            scope.sortEmbargoesProquest("column");

            expect(scope.sortAction).toEqual("sortProquestEmbargoes");
            expect(EmbargoRepo.sort).not.toHaveBeenCalled();

            scope.sortEmbargoesProquest("column");

            expect(scope.sortAction).toEqual("confirm");
            expect(EmbargoRepo.sort).toHaveBeenCalled();
        });
        it('updateEmbargo should should save a embargo', function () {
            scope.modalData = new mockEmbargo(q);

            spyOn(scope.modalData, "save");

            scope.updateEmbargo();

            expect(scope.modalData.save).toHaveBeenCalled();
        });
    });

});
