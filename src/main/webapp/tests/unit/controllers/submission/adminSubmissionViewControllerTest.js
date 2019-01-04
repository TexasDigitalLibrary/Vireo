describe('controller: AdminSubmissionViewController', function () {

    var controller, scope;

    var initializeController = function(settings) {
        inject(function ($anchorScroll, $controller, $location, $q, $route, $routeParams, $rootScope, $window, _DepositLocationRepo_, _EmailTemplateRepo_, _FieldPredicateRepo_, _FieldValue_, _FileUploadService_, _ModalService_, _RestApi_, _SidebarService_, _StorageService_, _SubmissionRepo_, _SubmissionStatusRepo_, _UserRepo_, _UserService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('AdminSubmissionViewController', {
                $anchorScroll: $anchorScroll,
                $location: $location,
                $route: $route,
                $routeParams: $routeParams,
                $scope: scope,
                $window: $window,
                DepositLocationRepo: _DepositLocationRepo_,
                EmailTemplateRepo: _EmailTemplateRepo_,
                FieldPredicateRepo: _FieldPredicateRepo_,
                FieldValue: _FieldValue_,
                FileUploadService: _FileUploadService_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                SidebarService: _SidebarService_,
                StorageService: _StorageService_,
                SubmissionRepo: _SubmissionRepo_,
                SubmissionStatus: mockSubmissionStatus,
                SubmissionStatusRepo: _SubmissionStatusRepo_,
                UserRepo: _UserRepo_,
                UserService: _UserService_,
                WsApi: _WsApi_
            });

            // ensure that the isReady() is called.
            scope.$digest();
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.depositLocationRepo');
        module('mock.emailTemplateRepo');
        module('mock.fieldPredicateRepo');
        module('mock.fieldValue');
        module('mock.fileUploadService');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.sidebarService');
        module('mock.storageService');
        module('mock.submissionRepo');
        module('mock.submissionStatus');
        module('mock.submissionStatusRepo');
        module('mock.user');
        module('mock.userRepo');
        module('mock.userService');
        module('mock.wsApi');

        installPromiseMatchers();
        initializeController();
    });

    describe('Is the controller defined', function () {
        it('should be defined for admin', function () {
            expect(controller).toBeDefined();
        });
        it('should be defined for manager', function () {
            initializeController({role: "ROLE_MANAGER"});
            expect(controller).toBeDefined();
        });
        it('should be defined for reviewer', function () {
            initializeController({role: "ROLE_REVIEWER"});
            expect(controller).toBeDefined();
        });
        it('should be defined for student', function () {
            initializeController({role: "ROLE_STUDENT"});
            expect(controller).toBeDefined();
        });
        it('should be defined for anonymous', function () {
            initializeController({role: "ROLE_ANONYMOUS"});
            expect(controller).toBeDefined();
        });
    });

});
