describe('controller: EmailWorkflowRulesController', function () {

    var controller, scope;

    var initializeController = function(settings) {
        inject(function ($controller, _$q_, $rootScope, $window, _EmailTemplateRepo_, _ModalService_, _OrganizationRepo_, _RestApi_, _StorageService_, _SubmissionStatusRepo_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('EmailWorkflowRulesController', {
                $q: _$q_,
                $scope: scope,
                $window: $window,
                EmailTemplateRepo: _EmailTemplateRepo_,
                ModalService: _ModalService_,
                OrganizationRepo: _OrganizationRepo_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                SubmissionStatusRepo: _SubmissionStatusRepo_,
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
        module('mock.submissionStatus');
        module('mock.submissionStatusRepo');
        module('mock.emailTemplateRepo');
        module('mock.modalService');
        module('mock.organization');
        module('mock.organizationRepo');
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

});
