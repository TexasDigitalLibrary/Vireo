describe('controller: DegreeRepoController', function () {

    var controller, q, scope, DegreeRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $q, $rootScope, $window, _DegreeRepo_, _DegreeLevelRepo_, _DragAndDropListenerFactory_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            q = $q;

            DegreeRepo = _DegreeRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('DegreeRepoController', {
                $q: $q,
                $scope: scope,
                $window: $window,
                DegreeRepo: _DegreeRepo_,
                DegreeLevelRepo: _DegreeLevelRepo_,
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
        module('mock.degree');
        module('mock.degreeRepo');
        module('mock.degreeLevel');
        module('mock.degreeLevelRepo');
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
        it('createDegree should be defined', function () {
            expect(scope.createDegree).toBeDefined();
            expect(typeof scope.createDegree).toEqual("function");
        });
        it('editDegree should be defined', function () {
            expect(scope.editDegree).toBeDefined();
            expect(typeof scope.editDegree).toEqual("function");
        });
        it('removeDegree should be defined', function () {
            expect(scope.removeDegree).toBeDefined();
            expect(typeof scope.removeDegree).toEqual("function");
        });
        it('reorderDegree should be defined', function () {
            expect(scope.reorderDegree).toBeDefined();
            expect(typeof scope.reorderDegree).toEqual("function");
        });
        it('resetDegree should be defined', function () {
            expect(scope.resetDegree).toBeDefined();
            expect(typeof scope.resetDegree).toEqual("function");
        });
        it('selectDegree should be defined', function () {
            expect(scope.selectDegree).toBeDefined();
            expect(typeof scope.selectDegree).toEqual("function");
        });
        it('updateDegree should be defined', function () {
            expect(scope.updateDegree).toBeDefined();
            expect(typeof scope.updateDegree).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('createDegree should create a new custom action', function () {
            scope.modalData = new mockDegree(q);

            spyOn(DegreeRepo, "create");

            scope.createDegree();

            expect(DegreeRepo.create).toHaveBeenCalled();
        });
        it('editDegree should open a modal', function () {
            spyOn(scope, "selectDegree");
            spyOn(scope, "openModal");

            scope.editDegree(1);

            expect(scope.selectDegree).toHaveBeenCalled();
            expect(scope.openModal).toHaveBeenCalled();
        });
        it('removeDegree should delete a custom action', function () {
            scope.modalData = new mockDegree(q);

            spyOn(scope.modalData, "delete");

            scope.removeDegree();

            expect(scope.modalData.delete).toHaveBeenCalled();
        });
        it('reorderDegree should reorder a custom action', function () {
            spyOn(DegreeRepo, "reorder");

            scope.reorderDegree("a", "b");

            expect(DegreeRepo.reorder).toHaveBeenCalled();
        });
        it('resetDegree should reset the custom action', function () {
            var degree = new mockDegree(q);
            scope.forms = [];
            scope.modalData = degree;
            scope.modalData.level = null;

            spyOn(scope.degreeRepo, "clearValidationResults");
            spyOn(degree, "refresh");
            spyOn(scope, "closeModal");

            scope.resetDegree();

            expect(scope.degreeRepo.clearValidationResults).toHaveBeenCalled();
            expect(degree.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
            expect(scope.modalData.level).toBeDefined();
        });
        it('selectDegree should select a custom action', function () {
            scope.modalData = null;
            scope.degrees = [
                new mockDegree(q),
                new mockDegree(q)
            ];
            scope.degrees[1].mock(dataDegree2);

            scope.selectDegree(1);

            expect(scope.modalData).toBe(scope.degrees[1]);
        });
        it('updateDegree should should save a custom action', function () {
            scope.modalData = new mockDegree(q);

            spyOn(scope.modalData, "save");

            scope.updateDegree();

            expect(scope.modalData.save).toHaveBeenCalled();
        });
    });
});
