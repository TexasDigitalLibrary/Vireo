describe('controller: DepositLocationRepoController', function () {

    var controller, q, scope, DepositLocationRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $q, $rootScope, $window, _DepositLocationRepo_, _DragAndDropListenerFactory_, _ModalService_, _PackagerRepo_, _RestApi_, _StorageService_, _WsApi_) {
            q = $q;
            scope = $rootScope.$new();

            DepositLocationRepo = _DepositLocationRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('DepositLocationRepoController', {
                $q: q,
                $scope: scope,
                $window: $window,
                DepositLocationRepo: _DepositLocationRepo_,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                ModalService: _ModalService_,
                PackagerRepo: _PackagerRepo_,
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
        module('mock.depositLocation');
        module('mock.depositLocationRepo');
        module('mock.dragAndDropListenerFactory');
        module('mock.modalService');
        module('mock.packager');
        module('mock.packagerRepo');
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
        it('createDepositLocation should be defined', function () {
            expect(scope.createDepositLocation).toBeDefined();
            expect(typeof scope.createDepositLocation).toEqual("function");
        });
        it('editDepositLocation should be defined', function () {
            expect(scope.editDepositLocation).toBeDefined();
            expect(typeof scope.editDepositLocation).toEqual("function");
        });
        it('removeDepositLocation should be defined', function () {
            expect(scope.removeDepositLocation).toBeDefined();
            expect(typeof scope.removeDepositLocation).toEqual("function");
        });
        it('reorderDepositLocation should be defined', function () {
            expect(scope.reorderDepositLocation).toBeDefined();
            expect(typeof scope.reorderDepositLocation).toEqual("function");
        });
        it('resetDepositLocation should be defined', function () {
            expect(scope.resetDepositLocation).toBeDefined();
            expect(typeof scope.resetDepositLocation).toEqual("function");
        });
        it('selectDepositLocation should be defined', function () {
            expect(scope.selectDepositLocation).toBeDefined();
            expect(typeof scope.selectDepositLocation).toEqual("function");
        });
        it('updateDepositLocation should be defined', function () {
            expect(scope.updateDepositLocation).toBeDefined();
            expect(typeof scope.updateDepositLocation).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('createDepositLocation should create a new custom action', function () {
            scope.modalData = new mockDepositLocation(q);

            spyOn(DepositLocationRepo, "create");

            scope.createDepositLocation();

            expect(DepositLocationRepo.create).toHaveBeenCalled();
        });
        it('editDepositLocation should open a modal', function () {
            spyOn(scope, "selectDepositLocation");
            spyOn(scope, "openModal");

            scope.editDepositLocation(1);

            expect(scope.selectDepositLocation).toHaveBeenCalled();
            expect(scope.openModal).toHaveBeenCalled();
        });
        it('removeDepositLocation should delete a custom action', function () {
            scope.modalData = new mockDepositLocation(q);

            spyOn(scope.modalData, "delete");

            scope.removeDepositLocation();

            expect(scope.modalData.delete).toHaveBeenCalled();
        });
        it('reorderDepositLocation should reorder a custom action', function () {
            spyOn(DepositLocationRepo, "reorder");

            scope.reorderDepositLocation("a", "b");

            expect(DepositLocationRepo.reorder).toHaveBeenCalled();
        });
        it('resetDepositLocation should reset the custom action', function () {
            var depositLocation = new mockDepositLocation(q);
            scope.forms = [];
            scope.modalData = depositLocation;

            spyOn(scope.depositLocationRepo, "clearValidationResults");
            spyOn(depositLocation, "refresh");
            spyOn(scope, "closeModal");

            scope.resetDepositLocation();

            expect(scope.depositLocationRepo.clearValidationResults).toHaveBeenCalled();
            expect(depositLocation.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
            expect(scope.modalData.level).not.toBe(depositLocation);
        });
        it('selectDepositLocation should select a custom action', function () {
            scope.modalData = null;
            scope.depositLocations = [
                new mockDepositLocation(q),
                new mockDepositLocation(q)
            ];
            scope.depositLocations[1].mock(dataDepositLocation2);

            scope.selectDepositLocation(1);

            expect(scope.modalData).toBe(scope.depositLocations[1]);
        });
        it('updateDepositLocation should should save a custom action', function () {
            scope.modalData = new mockDepositLocation(q);

            spyOn(scope.modalData, "save");

            scope.updateDepositLocation();

            expect(scope.modalData.save).toHaveBeenCalled();
        });
    });

});
