describe('controller: FieldPredicatesController', function () {

    var controller, q, scope, FieldPredicateRepo;

    var initializeController = function(settings) {
        inject(function ($filter, $q, $controller, $rootScope, $timeout, _DragAndDropListenerFactory_, _FieldPredicateRepo_, _ModalService_, _RestApi_, _SidebarService_, _StorageService_, _WsApi_) {
            q = $q;
            scope = $rootScope.$new();

            FieldPredicateRepo = _FieldPredicateRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('FieldPredicatesController', {
                $filter: $filter,
                $q: q,
                $scope: scope,
                $timeout: $timeout,
                $window: mockWindow(),
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                FieldPredicateRepo: _FieldPredicateRepo_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                SidebarService: _SidebarService_,
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
        module('mock.fieldPredicate');
        module('mock.fieldPredicateRepo');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.sidebarService');
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
        it('createNewFieldPredicate should be defined', function () {
            expect(scope.createNewFieldPredicate).toBeDefined();
            expect(typeof scope.createNewFieldPredicate).toEqual("function");
        });
        it('launchEditModal should be defined', function () {
            expect(scope.launchEditModal).toBeDefined();
            expect(typeof scope.launchEditModal).toEqual("function");
        });
        it('removeFieldPredicate should be defined', function () {
            expect(scope.removeFieldPredicate).toBeDefined();
            expect(typeof scope.removeFieldPredicate).toEqual("function");
        });
        it('resetFieldPredicates should be defined', function () {
            expect(scope.resetFieldPredicates).toBeDefined();
            expect(typeof scope.resetFieldPredicates).toEqual("function");
        });
        it('selectFieldPredicate should be defined', function () {
            expect(scope.selectFieldPredicate).toBeDefined();
            expect(typeof scope.selectFieldPredicate).toEqual("function");
        });
        it('updateFieldPredicate should be defined', function () {
            expect(scope.updateFieldPredicate).toBeDefined();
            expect(typeof scope.updateFieldPredicate).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('createNewFieldPredicate should create a new fieldPredicate', function () {
            scope.modalData = new mockFieldPredicate(q);
            scope.modalData.documentTypePredicate = null;

            spyOn(FieldPredicateRepo, "create").and.callThrough();

            scope.createNewFieldPredicate();

            expect(FieldPredicateRepo.create).toHaveBeenCalled();
            expect(scope.modalData.documentTypePredicate).toBe(false);
        });
        it('launchEditModal should open a modal', function () {
            scope.modalData = null;

            spyOn(scope, "resetFieldPredicates");
            spyOn(scope, "openModal");

            scope.launchEditModal(new mockFieldPredicate(q));

            expect(scope.resetFieldPredicates).toHaveBeenCalled();
            expect(scope.openModal).toHaveBeenCalled();
            expect(scope.modalData).toBeDefined();
        });
        it('removeFieldPredicate should delete a fieldPredicate', function () {
            scope.modalData = new mockFieldPredicate(q);

            spyOn(scope.modalData, "delete");

            scope.removeFieldPredicate();

            expect(scope.modalData.delete).toHaveBeenCalled();
        });
        it('resetFieldPredicates should reset the fieldPredicate', function () {
            var fieldPredicate = new mockFieldPredicate(q);
            scope.forms = [];
            scope.modalData = fieldPredicate;

            spyOn(scope.fieldPredicateRepo, "clearValidationResults");
            spyOn(fieldPredicate, "refresh");
            spyOn(scope, "closeModal");

            scope.resetFieldPredicates();

            expect(scope.fieldPredicateRepo.clearValidationResults).toHaveBeenCalled();
            expect(fieldPredicate.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
            expect(scope.modalData).toBeDefined();
        });
        it('selectFieldPredicate should select a fieldPredicate', function () {
            scope.modalData = null;
            scope.fieldPredicates = [
                new mockFieldPredicate(q),
                new mockFieldPredicate(q)
            ];
            scope.fieldPredicates[1].mock(dataFieldPredicate2);
            scope.dragAndDropSelectedFilter = 0;
            scope.dragAndDropTextFilterValue = [{}];

            spyOn(scope, "resetFieldPredicates");

            scope.selectFieldPredicate(1);

            expect(scope.modalData.id).toBe(scope.fieldPredicates[1].id);
            expect(scope.resetFieldPredicates).toHaveBeenCalled();
        });
        it('updateFieldPredicate should should save a fieldPredicate', function () {
            scope.modalData = new mockFieldPredicate(q);

            spyOn(scope.modalData, "save");

            scope.updateFieldPredicate();

            expect(scope.modalData.save).toHaveBeenCalled();
        });
    });

    // FIXME: there are two methods not on the scope that are added in the controller that may need to be tested.
    // $scope.dragControlListeners.accept()
    // $scope.dragControlListeners.orderChanged()

});
