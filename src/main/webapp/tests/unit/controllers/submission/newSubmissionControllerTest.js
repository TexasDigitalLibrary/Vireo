describe('controller: NewSubmissionController', function () {

    var controller, scope;

    var initializeController = function(settings) {
        inject(function ($controller, $location, _$q_, $rootScope, $window, SubmissionStates, _ManagedConfigurationRepo_, _ModalService_, _OrganizationRepo_, _RestApi_, _StorageService_, _StudentSubmissionRepo_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('NewSubmissionController', {
                $location: $location,
                $q: _$q_,
                $scope: scope,
                $window: $window,
                SubmissionStates: SubmissionStates,
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
                ModalService: _ModalService_,
                OrganizationRepo: _OrganizationRepo_,
                StorageService: _StorageService_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
                RestApi: _RestApi_,
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
        module('mock.managedConfiguration');
        module('mock.managedConfigurationRepo');
        module('mock.modalService');
        module('mock.organization');
        module('mock.organizationRepo');
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
