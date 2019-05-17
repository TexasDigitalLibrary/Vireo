describe('controller: LanguagesController', function () {

    var controller, q, scope, LanguageRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $q, $rootScope, $timeout, _DragAndDropListenerFactory_, _LanguageRepo_, _StorageService_, _ModalService_, _RestApi_, _WsApi_) {
            q = $q;
            scope = $rootScope.$new();

            LanguageRepo = _LanguageRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('LanguagesController', {
                $q: q,
                $scope: scope,
                $timeout: $timeout,
                $window: mockWindow(),
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                LanguageRepo: _LanguageRepo_,
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
        module('mock.language');
        module('mock.languageRepo');
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
        it('createLanguage should be defined', function () {
            expect(scope.createLanguage).toBeDefined();
            expect(typeof scope.createLanguage).toEqual("function");
        });
        it('editLanguage should be defined', function () {
            expect(scope.editLanguage).toBeDefined();
            expect(typeof scope.editLanguage).toEqual("function");
        });
        it('removeLanguage should be defined', function () {
            expect(scope.removeLanguage).toBeDefined();
            expect(typeof scope.removeLanguage).toEqual("function");
        });
        it('reorderLanguages should be defined', function () {
            expect(scope.reorderLanguages).toBeDefined();
            expect(typeof scope.reorderLanguages).toEqual("function");
        });
        it('resetLanguages should be defined', function () {
            expect(scope.resetLanguages).toBeDefined();
            expect(typeof scope.resetLanguages).toEqual("function");
        });
        it('selectLanguage should be defined', function () {
            expect(scope.selectLanguage).toBeDefined();
            expect(typeof scope.selectLanguage).toEqual("function");
        });
        it('sortLanguages should be defined', function () {
            expect(scope.sortLanguages).toBeDefined();
            expect(typeof scope.sortLanguages).toEqual("function");
        });
        it('updateLanguage should be defined', function () {
            expect(scope.updateLanguage).toBeDefined();
            expect(typeof scope.updateLanguage).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('createLanguage should create a language', function () {
            scope.modalData = new mockLanguage(q);

            spyOn(LanguageRepo, "create");

            scope.createLanguage();

            expect(LanguageRepo.create).toHaveBeenCalled();
        });
        it('editLanguage should open a modal', function () {
            spyOn(scope, "selectLanguage");
            spyOn(scope, "openModal");

            scope.editLanguage(1);

            expect(scope.selectLanguage).toHaveBeenCalled();
            expect(scope.openModal).toHaveBeenCalled();
        });
        it('removeLanguage should delete a language', function () {
            scope.modalData = new mockLanguage(q);

            spyOn(scope.modalData, "delete");

            scope.removeLanguage();

            expect(scope.modalData.delete).toHaveBeenCalled();
        });
        it('reorderLanguages should reorder a language', function () {
            spyOn(LanguageRepo, "reorder");

            scope.reorderLanguages("a", "b");

            expect(LanguageRepo.reorder).toHaveBeenCalled();
        });
        it('resetLanguages should reset the language', function () {
            var language = new mockLanguage(q);
            scope.forms = [];
            scope.modalData = language;
            scope.uploadAction = "process";
            scope.uploadStatus = function() {};

            spyOn(scope.languageRepo, "clearValidationResults");
            spyOn(language, "refresh");
            spyOn(scope, "closeModal");
            spyOn(scope, "uploadStatus");

            scope.resetLanguages();

            expect(scope.languageRepo.clearValidationResults).toHaveBeenCalled();
            expect(language.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
            expect(scope.uploadStatus).toHaveBeenCalled();
            expect(scope.uploadAction).toEqual("confirm");
            expect(typeof scope.modalData.degreeLevel).not.toBe(language);
        });
        it('selectLanguage should select a language', function () {
            scope.modalData = null;
            scope.languages = [
                new mockLanguage(q),
                new mockLanguage(q)
            ];
            scope.languages[1].mock(dataLanguage2);

            scope.selectLanguage(1);

            expect(scope.modalData).toBe(scope.languages[1]);
        });
        it('sortLanguages should select a sort action', function () {
            scope.sortAction = "confirm";

            spyOn(LanguageRepo, "sort");

            scope.sortLanguages("column");

            expect(scope.sortAction).toEqual("sort");
            expect(LanguageRepo.sort).not.toHaveBeenCalled();

            scope.sortLanguages("column");

            expect(scope.sortAction).toEqual("confirm");
            expect(LanguageRepo.sort).toHaveBeenCalled();
        });
        it('updateLanguage should should save a language', function () {
            scope.modalData = new mockLanguage(q);

            spyOn(scope.modalData, "save");

            scope.updateLanguage();

            expect(scope.modalData.save).toHaveBeenCalled();
        });
    });

});
