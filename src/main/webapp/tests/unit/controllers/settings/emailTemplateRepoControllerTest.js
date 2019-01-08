describe('controller: EmailTemplateRepoController', function () {

    var controller, q, scope, EmailTemplateRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $q, $rootScope, $window, _DragAndDropListenerFactory_, _EmailTemplateRepo_, _FieldPredicateRepo_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            q = $q;

            EmailTemplateRepo = _EmailTemplateRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('EmailTemplateRepoController', {
                $q: $q,
                $scope: scope,
                $window: $window,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                EmailTemplateRepo: _EmailTemplateRepo_,
                FieldPredicateRepo: _FieldPredicateRepo_,
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
        module('mock.emailTemplate');
        module('mock.emailTemplateRepo');
        module('mock.fieldPredicate');
        module('mock.fieldPredicateRepo');
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
        it('createEmailTemplate should be defined', function () {
            expect(scope.createEmailTemplate).toBeDefined();
            expect(typeof scope.createEmailTemplate).toEqual("function");
        });
        it('insertText should be defined', function () {
            expect(scope.insertText).toBeDefined();
            expect(typeof scope.insertText).toEqual("function");
        });
        it('launchEditModal should be defined', function () {
            expect(scope.launchEditModal).toBeDefined();
            expect(typeof scope.launchEditModal).toEqual("function");
        });
        it('removeEmailTemplate should be defined', function () {
            expect(scope.removeEmailTemplate).toBeDefined();
            expect(typeof scope.removeEmailTemplate).toEqual("function");
        });
        it('reorderEmailTemplates should be defined', function () {
            expect(scope.reorderEmailTemplates).toBeDefined();
            expect(typeof scope.reorderEmailTemplates).toEqual("function");
        });
        it('resetEmailTemplates should be defined', function () {
            expect(scope.resetEmailTemplates).toBeDefined();
            expect(typeof scope.resetEmailTemplates).toEqual("function");
        });
        it('selectEmailTemplate should be defined', function () {
            expect(scope.selectEmailTemplate).toBeDefined();
            expect(typeof scope.selectEmailTemplate).toEqual("function");
        });
        it('setCursorLocation should be defined', function () {
            expect(scope.setCursorLocation).toBeDefined();
            expect(typeof scope.setCursorLocation).toEqual("function");
        });
        it('updateEmailTemplate should be defined', function () {
            expect(scope.updateEmailTemplate).toBeDefined();
            expect(typeof scope.updateEmailTemplate).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('createEmailTemplate should create a new custom action', function () {
            scope.modalData = new mockEmailTemplate(q);
            scope.modalData.mockedList = []; // FIXME: this is somehow not being defined when created above.

            spyOn(EmailTemplateRepo, "create").and.callThrough();

            scope.createEmailTemplate();

            expect(EmailTemplateRepo.create).toHaveBeenCalled();
        });
        it('insertText should should add text to the modal data', function () {
            scope.modalData = new mockEmailTemplate(q);
            scope.modalData.message = "";
            scope.cursorLocation = 0;

            spyOn(scope.modalData, "save");

            scope.insertText("test");

            expect(scope.modalData.message).toEqual(" {test} ");
            expect(scope.cursorLocation).toBe(" {test} ".length);
        });
        it('launchEditModal should open a modal', function () {
            var emailTemplate = new mockEmailTemplate(q);
            scope.emailTemplates = [
                emailTemplate
            ];

            spyOn(scope, "openModal");

            scope.launchEditModal(1);

            expect(scope.modalData).toBe(emailTemplate);
            expect(scope.openModal).toHaveBeenCalled();
        });
        it('removeEmailTemplate should delete a custom action', function () {
            scope.modalData = new mockEmailTemplate(q);

            spyOn(scope.modalData, "delete");

            scope.removeEmailTemplate();

            expect(scope.modalData.delete).toHaveBeenCalled();
        });
        it('reorderEmailTemplates should reorder a custom action', function () {
            spyOn(EmailTemplateRepo, "reorder");

            scope.reorderEmailTemplates("a", "b");

            expect(EmailTemplateRepo.reorder).toHaveBeenCalled();
        });
        it('resetEmailTemplates should reset the custom action', function () {
            var emailTemplate = new mockEmailTemplate(q);
            scope.forms = [];
            scope.modalData = emailTemplate;
            scope.modalData.level = null;

            spyOn(scope.emailTemplateRepo, "clearValidationResults");
            spyOn(emailTemplate, "refresh");
            spyOn(scope, "closeModal");

            scope.resetEmailTemplates();

            expect(scope.emailTemplateRepo.clearValidationResults).toHaveBeenCalled();
            expect(emailTemplate.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
            expect(scope.modalData).toBeDefined();
        });
        it('selectEmailTemplate should select a custom action', function () {
            scope.modalData = null;
            scope.emailTemplates = [
                new mockEmailTemplate(q),
                new mockEmailTemplate(q)
            ];
            scope.emailTemplates[1].mock(mockEmailTemplate2);

            scope.selectEmailTemplate(1);

            expect(scope.modalData).toBe(scope.emailTemplates[1]);
        });
        it('setCursorLocation should assign the cursor location', function () {
            // TODO: improve this test.
            var event = {
                target: {}
            }

            spyOn(angular, "element").and.callThrough();

            scope.setCursorLocation(event);

            expect(angular.element).toHaveBeenCalled();
        });
        it('updateEmailTemplate should should save a custom action', function () {
            scope.modalData = new mockEmailTemplate(q);

            spyOn(scope.modalData, "save");

            scope.updateEmailTemplate();

            expect(scope.modalData.save).toHaveBeenCalled();
        });
    });

});
