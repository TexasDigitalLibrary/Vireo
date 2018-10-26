describe('controller: EmailWorkflowRulesController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.submissionStatusRepo');
        module('mock.emailRecipientType');
        module('mock.emailTemplateRepo');
        module('mock.inputTypes');
        module('mock.modalService');
        module('mock.organizationRepo');
        module('mock.restApi');

        inject(function ($controller, $q, $rootScope, $window, _SubmissionStatusRepo_, _EmailRecipientType_, _EmailTemplateRepo_, _InputTypes_, _ModalService_, _OrganizationRepo_, _RestApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('EmailWorkflowRulesController', {
                $q: $q,
                $scope: scope,
                $window: $window,
                SubmissionStatusRepo: _SubmissionStatusRepo_,
                EmailRecipientType: _EmailRecipientType_,
                EmailTemplateRepo: _EmailTemplateRepo_,
                InputTypes: _InputTypes_,
                ModalService: _ModalService_,
                OrganizationRepo: _OrganizationRepo_,
                RestApi: _RestApi_
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
