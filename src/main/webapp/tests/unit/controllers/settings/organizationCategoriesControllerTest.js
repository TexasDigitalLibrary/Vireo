describe('controller: OrganizationCategoriesController', function () {

    var controller, q, scope, OrganizationCategoryRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $q, $rootScope, $window, _DragAndDropListenerFactory_, _ModalService_, _OrganizationCategoryRepo_, _RestApi_, _StorageService_, _WsApi_) {
            q = $q;
            scope = $rootScope.$new();

            OrganizationCategoryRepo = _OrganizationCategoryRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('OrganizationCategoriesController', {
                $q: q,
                $scope: scope,
                $window: $window,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                ModalService: _ModalService_,
                OrganizationCategoryRepo: _OrganizationCategoryRepo_,
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
        module('mock.modalService');
        module('mock.organizationCategory');
        module('mock.organizationCategoryRepo');
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
        it('createOrganizationCategory should be defined', function () {
            expect(scope.createOrganizationCategory).toBeDefined();
            expect(typeof scope.createOrganizationCategory).toEqual("function");
        });
        it('launchEditModal should be defined', function () {
            expect(scope.launchEditModal).toBeDefined();
            expect(typeof scope.launchEditModal).toEqual("function");
        });
        it('removeOrganizationCategory should be defined', function () {
            expect(scope.removeOrganizationCategory).toBeDefined();
            expect(typeof scope.removeOrganizationCategory).toEqual("function");
        });
        it('resetOrganizationCategories should be defined', function () {
            expect(scope.resetOrganizationCategories).toBeDefined();
            expect(typeof scope.resetOrganizationCategories).toEqual("function");
        });
        it('selectOrganizationCategory should be defined', function () {
            expect(scope.selectOrganizationCategory).toBeDefined();
            expect(typeof scope.selectOrganizationCategory).toEqual("function");
        });
        it('updateOrganizationCategory should be defined', function () {
            expect(scope.updateOrganizationCategory).toBeDefined();
            expect(typeof scope.updateOrganizationCategory).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('createOrganizationCategory should create a new organizationCategory', function () {
            spyOn(OrganizationCategoryRepo, "create");

            scope.createOrganizationCategory();

            expect(OrganizationCategoryRepo.create).toHaveBeenCalled();
        });
        it('launchEditModal should open a modal', function () {
            scope.modalData = null;

            spyOn(scope, "openModal");

            scope.launchEditModal(new mockOrganizationCategory(q));

            expect(scope.openModal).toHaveBeenCalled();
            expect(scope.modalData).toBeDefined();
        });
        it('removeOrganizationCategory should delete a organizationCategory', function () {
            scope.modalData = new mockOrganizationCategory(q);

            spyOn(scope.modalData, "delete");

            scope.removeOrganizationCategory();

            expect(scope.modalData.delete).toHaveBeenCalled();
        });
        it('resetOrganizationCategories should reset the organizationCategory', function () {
            var organizationCategory = new mockOrganizationCategory(q);
            scope.forms = [];
            scope.modalData = organizationCategory;

            spyOn(scope.organizationCategoryRepo, "clearValidationResults");
            spyOn(organizationCategory, "refresh");
            spyOn(scope, "closeModal");

            scope.resetOrganizationCategories();

            expect(scope.organizationCategoryRepo.clearValidationResults).toHaveBeenCalled();
            expect(organizationCategory.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
            expect(scope.modalData).toBeDefined();
        });
        it('selectOrganizationCategory should select a organizationCategory', function () {
            scope.modalData = null;
            scope.organizationCategorys = [
                new mockOrganizationCategory(q),
                new mockOrganizationCategory(q)
            ];
            scope.organizationCategorys[1].mock(dataOrganizationCategory2);
            scope.dragAndDropSelectedFilter = 0;
            scope.dragAndDropTextFilterValue = [{}];

            spyOn(scope, "resetOrganizationCategories");

            scope.selectOrganizationCategory(1);

            expect(scope.modalData.id).toBe(scope.organizationCategorys[1].id);
            expect(scope.resetOrganizationCategories).toHaveBeenCalled();
        });
        it('updateOrganizationCategory should should save a organizationCategory', function () {
            scope.modalData = new mockOrganizationCategory(q);

            spyOn(scope.modalData, "save");

            scope.updateOrganizationCategory();

            expect(scope.modalData.save).toHaveBeenCalled();
        });
    });

    // FIXME: there are methods not on the scope that are added in the controller that may need to be tested.
    // scope.dragControlListeners.accept()
    // scope.dragControlListeners.orderChanged()

});
