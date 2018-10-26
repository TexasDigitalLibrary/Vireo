describe('controller: NewSubmissionController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.managedConfigurationRepo');
        module('mock.modalService');
        module('mock.organizationRepo');
        module('mock.restApi');
        module('mock.studentSubmissionRepo');
        module('mock.submissionStates');

        inject(function ($controller, $location, $q, $rootScope, $window, _ManagedConfigurationRepo_, _ModalService_, _OrganizationRepo_, _RestApi_, _StudentSubmissionRepo_, _SubmissionStates_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('NewSubmissionController', {
                $location: $location,
                $q: $q,
                $scope: scope,
                $window: $window,
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
                ModalService: _ModalService_,
                OrganizationRepo: _OrganizationRepo_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
                SubmissionStates: _SubmissionStates_,
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
