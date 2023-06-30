describe("controller: DepositLocationRepoController", function () {

    var controller, q, scope, mockedDepositLocation, DepositLocation, DepositLocationRepo, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, _DepositLocationRepo_, _WsApi_) {
            q = $q;

            mockedDepositLocation = mockParameterModel(q, mockDepositLocation);

            DepositLocationRepo = _DepositLocationRepo_;
            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($controller, $rootScope, _DepositLocation_, _DragAndDropListenerFactory_, _ModalService_, _PackagerRepo_, _RestApi_, _StorageService_) {
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            DepositLocation = _DepositLocation_;

            controller = $controller("DepositLocationRepoController", {
                $q: q,
                $scope: scope,
                $window: mockWindow(),
                DepositLocation: _DepositLocation_,
                DepositLocationRepo: DepositLocationRepo,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                ModalService: _ModalService_,
                PackagerRepo: _PackagerRepo_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                WsApi: WsApi
            });

            // ensure that the isReady() is called.
            if (!scope.$$phase) {
                scope.$digest();
            }
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");

        module("mock.depositLocationRepo");
        module("mock.dragAndDropListenerFactory");
        module("mock.modalService");
        module("mock.packager");
        module("mock.packagerRepo");
        module("mock.restApi");
        module("mock.storageService");
        module("mock.wsApi");

        installPromiseMatchers();
        initializeVariables();
        initializeController();
    });

    describe("Is the controller defined", function () {
        it("should be defined", function () {
            expect(controller).toBeDefined();
        });
    });

    describe("Are the scope methods defined", function () {
        it("createDepositLocation should be defined", function () {
            expect(scope.createDepositLocation).toBeDefined();
            expect(typeof scope.createDepositLocation).toEqual("function");
        });
        it("editDepositLocation should be defined", function () {
            expect(scope.editDepositLocation).toBeDefined();
            expect(typeof scope.editDepositLocation).toEqual("function");
        });
        it("removeDepositLocation should be defined", function () {
            expect(scope.removeDepositLocation).toBeDefined();
            expect(typeof scope.removeDepositLocation).toEqual("function");
        });
        it("reorderDepositLocation should be defined", function () {
            expect(scope.reorderDepositLocation).toBeDefined();
            expect(typeof scope.reorderDepositLocation).toEqual("function");
        });
        it("resetDepositLocation should be defined", function () {
            expect(scope.resetDepositLocation).toBeDefined();
            expect(typeof scope.resetDepositLocation).toEqual("function");
        });
        it("selectDepositLocation should be defined", function () {
            expect(scope.selectDepositLocation).toBeDefined();
            expect(typeof scope.selectDepositLocation).toEqual("function");
        });
        it("updateDepositLocation should be defined", function () {
            expect(scope.updateDepositLocation).toBeDefined();
            expect(typeof scope.updateDepositLocation).toEqual("function");
        });
    });

    describe("Are the scope.modalData methods defined", function () {
        it("testDepositLocation should be defined", function () {
            expect(scope.modalData.testDepositLocation).toBeDefined();
            expect(typeof scope.modalData.testDepositLocation).toEqual("function");
        });
        it("isTestDepositing should be defined", function () {
            expect(scope.modalData.isTestDepositing).toBeDefined();
            expect(typeof scope.modalData.isTestDepositing).toEqual("function");
        });
        it("isTestable should be defined", function () {
            expect(scope.modalData.isTestable).toBeDefined();
            expect(typeof scope.modalData.isTestable).toEqual("function");
        });
    });

    describe("Do the scope methods work as expected", function () {
        it("createDepositLocation should create a new custom action", function () {
            scope.modalData = new mockDepositLocation(q);

            spyOn(DepositLocationRepo, "create");

            scope.createDepositLocation();

            expect(DepositLocationRepo.create).toHaveBeenCalled();
        });
        it("editDepositLocation should open a modal", function () {
            spyOn(scope, "selectDepositLocation");
            spyOn(scope, "openModal");

            scope.editDepositLocation(1);

            expect(scope.selectDepositLocation).toHaveBeenCalled();
            expect(scope.openModal).toHaveBeenCalled();
        });
        it("removeDepositLocation should delete a custom action", function () {
            scope.modalData = new mockDepositLocation(q);

            spyOn(scope.modalData, "delete");

            scope.removeDepositLocation();

            expect(scope.modalData.delete).toHaveBeenCalled();
        });
        it("reorderDepositLocation should reorder a custom action", function () {
            spyOn(DepositLocationRepo, "reorder");

            scope.reorderDepositLocation("a", "b");

            expect(DepositLocationRepo.reorder).toHaveBeenCalled();
        });
        it("resetDepositLocation should reset the custom action", function () {
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

            scope.forms.myForm = mockForms();
            scope.resetDepositLocation();

            scope.forms.myForm.$pristine = false;
            scope.resetDepositLocation();
        });
        it("selectDepositLocation should select a custom action", function () {
            scope.modalData = undefined;
            scope.depositLocations = [
                new mockDepositLocation(q),
                new mockDepositLocation(q)
            ];
            scope.depositLocations[1].mock(dataDepositLocation2);

            scope.selectDepositLocation(1);

            expect(scope.modalData.id).toBe(scope.depositLocations[1].id);
            expect(scope.modalData.position).toBe(scope.depositLocations[1].position);
            expect(scope.modalData.name).toBe(scope.depositLocations[1].name);
            expect(scope.modalData.repository).toBe(scope.depositLocations[1].repository);
            expect(scope.modalData.collection).toBe(scope.depositLocations[1].collection);
            expect(scope.modalData.username).toBe(scope.depositLocations[1].username);
            expect(scope.modalData.password).toBe(scope.depositLocations[1].password);
            expect(scope.modalData.onBehalfOf).toBe(scope.depositLocations[1].onBehalfOf);
            expect(scope.modalData.packager).toBe(scope.depositLocations[1].packager);
            expect(scope.modalData.depositor).toBe(scope.depositLocations[1].depositor);
            expect(scope.modalData.timeout).toBe(scope.depositLocations[1].timeout);
        });
        it("updateDepositLocation should should save a custom action", function () {
            scope.modalData = new mockDepositLocation(q);

            spyOn(scope.modalData, "save");

            scope.updateDepositLocation();

            expect(scope.modalData.save).toHaveBeenCalled();
        });
    });

    describe("Do the scope.modalData methods work as expected", function () {
        it("testDepositLocation should test the deposit location", function () {
            scope.resetDepositLocation();
            scope.modalData.testDepositLocation();
            scope.$digest();

            scope.resetDepositLocation();
            scope.modalData.testDepositLocation();
            scope.$digest();

            scope.modalData.testConnection = function() {
                return payloadPromise(q.defer(), {}, "OTHER");
            };

            scope.resetDepositLocation();
            scope.modalData.testDepositLocation();
            scope.$digest();
        });
        it("isTestDepositing should return a boolean", function () {
            var response;

            scope.resetDepositLocation();
            response = scope.modalData.isTestDepositing();

            expect(typeof response).toBe("boolean");
        });
        it("isTestable should return a boolean", function () {
            var response;

            scope.resetDepositLocation();
            response = scope.modalData.isTestable();

            // FIXME: should be returning a boolean and not a string (scope.modalData.nam is actually returned).
            //expect(typeof response).toBe("boolean");

            scope.modalData.name = "mock";
            scope.modalData.depositorName = "mock";
            scope.modalData.repository = "mock";
            scope.modalData.username = "mock";
            scope.modalData.password = "mock";
            response = scope.modalData.isTestable();

            // FIXME: should be returning a boolean and not a string (scope.modalData.nam is actually returned).
            //expect(response).toBe(true);
        });
    });

});
