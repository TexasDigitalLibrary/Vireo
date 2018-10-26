describe('controller: SubmissionViewController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.customActionDefinitionRepo');
        module('mock.fieldPredicateRepo');
        module('mock.fileUploadService');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.studentSubmissionRepo');
        module('mock.submissionStates');

        inject(function ($controller, $q, $rootScope, $routeParams, $window, _CustomActionDefinitionRepo_, _FieldPredicateRepo_, _FileUploadService_, _ModalService_, _RestApi_, _StudentSubmissionRepo_, _SubmissionStates_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('SubmissionViewController', {
                $q: $q,
                $routeParams: $routeParams,
                $scope: scope,
                $window: $window,
                CustomActionDefinitionRepo: _CustomActionDefinitionRepo_,
                FieldPredicateRepo: _FieldPredicateRepo_,
                FileUploadService: _FileUploadService_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
                SubmissionStates: _SubmissionStates_
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
