describe('controller: EmailWorkflowRulesController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.submissionStatusRepo');
        module('mock.emailTemplateRepo');
        module('mock.modalService');
        module('mock.organizationRepo');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.wsApi');

        inject(function ($controller, _$q_, $rootScope, $window, _SubmissionStatusRepo_, _EmailTemplateRepo_, _ModalService_, _OrganizationRepo_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('EmailWorkflowRulesController', {
                $q: _$q_,
                $scope: scope,
                $window: $window,
                EmailTemplateRepo: _EmailTemplateRepo_,
                SubmissionStatusRepo: _SubmissionStatusRepo_,
                ModalService: _ModalService_,
                OrganizationRepo: _OrganizationRepo_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                WsApi: _WsApi_
            });

            // ensure that the isReady() is called.
            scope.$digest();
        });
    });

    describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });

});
