describe('controller: SubmissionViewController', function () {

    var controller, q, scope;

    var initializeController = function(settings) {
        inject(function ($controller, $q, $rootScope, $routeParams, $window, _CustomActionDefinitionRepo_, _FieldPredicateRepo_, _FileUploadService_, _ModalService_, _StorageService_, _RestApi_, _StudentSubmission_, _StudentSubmissionRepo_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            q = $q;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('SubmissionViewController', {
                $q: q,
                $routeParams: $routeParams,
                $scope: scope,
                $window: $window,
                CustomActionDefinitionRepo: _CustomActionDefinitionRepo_,
                FieldPredicateRepo: _FieldPredicateRepo_,
                FileUploadService: _FileUploadService_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
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
        module('mock.fieldPredicate');
        module('mock.fieldPredicateRepo');
        module('mock.fileUploadService');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.studentSubmission');
        module('mock.studentSubmissionRepo');
        module('mock.wsApi');

        installPromiseMatchers();
        initializeController();
    });

    describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });
});
