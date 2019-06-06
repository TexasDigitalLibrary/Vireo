describe('controller: GraduationMonthRepoController', function () {

    var controller, q, scope, GraduationMonthRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $q, $rootScope, _DragAndDropListenerFactory_, _GraduationMonthRepo_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            q = $q;
            scope = $rootScope.$new();

            GraduationMonthRepo = _GraduationMonthRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('GraduationMonthRepoController', {
                $scope: scope,
                $window: mockWindow(),
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                GraduationMonthRepo: _GraduationMonthRepo_,
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
        module('mock.graduationMonth');
        module('mock.graduationMonthRepo');
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
        it('createGraduationMonth should be defined', function () {
            expect(scope.createGraduationMonth).toBeDefined();
            expect(typeof scope.createGraduationMonth).toEqual("function");
        });
        it('editGraduationMonth should be defined', function () {
            expect(scope.editGraduationMonth).toBeDefined();
            expect(typeof scope.editGraduationMonth).toEqual("function");
        });
        it('removeGraduationMonth should be defined', function () {
            expect(scope.removeGraduationMonth).toBeDefined();
            expect(typeof scope.removeGraduationMonth).toEqual("function");
        });
        it('reorderGraduationMonth should be defined', function () {
            expect(scope.reorderGraduationMonth).toBeDefined();
            expect(typeof scope.reorderGraduationMonth).toEqual("function");
        });
        it('resetGraduationMonth should be defined', function () {
            expect(scope.resetGraduationMonth).toBeDefined();
            expect(typeof scope.resetGraduationMonth).toEqual("function");
        });
        it('selectGraduationMonth should be defined', function () {
            expect(scope.selectGraduationMonth).toBeDefined();
            expect(typeof scope.selectGraduationMonth).toEqual("function");
        });
        it('sortGraduationMonths should be defined', function () {
            expect(scope.sortGraduationMonths).toBeDefined();
            expect(typeof scope.sortGraduationMonths).toEqual("function");
        });
        it('toMonthString should be defined', function () {
            expect(scope.toMonthString).toBeDefined();
            expect(typeof scope.toMonthString).toEqual("function");
        });
        it('updateGraduationMonth should be defined', function () {
            expect(scope.updateGraduationMonth).toBeDefined();
            expect(typeof scope.updateGraduationMonth).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('createGraduationMonth should create a new graduation month', function () {
            scope.modalData = new mockGraduationMonth(q);

            spyOn(GraduationMonthRepo, "create");

            scope.createGraduationMonth();

            expect(GraduationMonthRepo.create).toHaveBeenCalled();
        });
        it('editGraduationMonth should open a modal', function () {
            spyOn(scope, "selectGraduationMonth");
            spyOn(scope, "openModal");

            scope.editGraduationMonth(1);

            expect(scope.selectGraduationMonth).toHaveBeenCalled();
            expect(scope.openModal).toHaveBeenCalled();
        });
        it('removeGraduationMonth should delete a graduation month', function () {
            scope.modalData = new mockGraduationMonth(q);

            spyOn(scope.modalData, "delete");

            scope.removeGraduationMonth();

            expect(scope.modalData.delete).toHaveBeenCalled();
        });
        it('reorderGraduationMonth should reorder a graduation month', function () {
            spyOn(GraduationMonthRepo, "reorder");

            scope.reorderGraduationMonth("a", "b");

            expect(GraduationMonthRepo.reorder).toHaveBeenCalled();
        });
        it('resetGraduationMonth should reset the graduation month', function () {
            var graduationMonth = new mockGraduationMonth(q);
            scope.forms = [];
            scope.modalData = graduationMonth;

            spyOn(scope.graduationMonthRepo, "clearValidationResults");
            spyOn(graduationMonth, "refresh");
            spyOn(scope, "closeModal");

            scope.resetGraduationMonth();

            expect(scope.graduationMonthRepo.clearValidationResults).toHaveBeenCalled();
            expect(graduationMonth.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
            expect(typeof scope.modalData.degreeLevel).not.toBe(graduationMonth);

            scope.forms.myForm = {
                $pristine: true,
                $untouched: true,
                $setPristine: function (value) { this.$pristine = value; },
                $setUntouched: function (value) { this.$untouched = value; }
            };
            scope.resetGraduationMonth();

            scope.forms.myForm.$pristine = false;
            scope.resetGraduationMonth();
        });
        it('selectGraduationMonth should select a graduation month', function () {
            scope.modalData = null;
            scope.graduationMonths = [
                new mockGraduationMonth(q),
                new mockGraduationMonth(q)
            ];
            scope.graduationMonths[1].mock(dataGraduationMonth2);

            scope.selectGraduationMonth(1);

            expect(scope.modalData).toBe(scope.graduationMonths[1]);
        });
        it('sortGraduationMonths should select a graduation month', function () {
            scope.sortAction = "confirm";

            spyOn(GraduationMonthRepo, "sort");

            scope.sortGraduationMonths("column");

            expect(scope.sortAction).toEqual("sort");
            expect(GraduationMonthRepo.sort).not.toHaveBeenCalled();

            scope.sortGraduationMonths("column");

            expect(scope.sortAction).toEqual("confirm");
            expect(GraduationMonthRepo.sort).toHaveBeenCalled();

            scope.sortAction = "unknown";
            scope.sortGraduationMonths("column");
        });
        it('toMonthString should return a month', function () {
            var response = scope.toMonthString(0);
            expect(response).toBe("January");
        });
        it('updateGraduationMonth should should save a graduation month', function () {
            scope.modalData = new mockGraduationMonth(q);

            spyOn(scope.modalData, "save");

            scope.updateGraduationMonth();

            expect(scope.modalData.save).toHaveBeenCalled();
        });
    });

});
