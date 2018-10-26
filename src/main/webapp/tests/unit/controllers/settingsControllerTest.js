describe('controller: SettingsController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.modalService');
        module('mock.managedConfigurationRepo');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.studentSubmissionRepo');
        module('mock.submissionStates');
        module('mock.user');
        module('mock.userService');
        module('mock.userSettings');

        inject(function ($controller, $injector, $rootScope, $timeout, $window, _ManagedConfigurationRepo_, _ModalService_, _RestApi_, _StudentSubmissionRepo_, _SubmissionStates_, _User_, _UserService_, UserSettings) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('SettingsController', {
                $scope: scope,
                $injector: $injector,
                $timeout: $timeout,
                $window: $window,
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
                SubmissionStates: _SubmissionStates_,
                User: _User_,
                UserService: _UserService_,
                UserSettings: _UserSettings_
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
