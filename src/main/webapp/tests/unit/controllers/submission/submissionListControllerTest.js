describe('controller: SubmissionListController', function () {

    var controller, q, scope;

    var initializeController = function(settings) {
        inject(function ($controller, $filter, $location, $q, $rootScope, $window, _ControlledVocabularyRepo_, _CustomActionDefinitionRepo_, _CustomActionValueRepo_, _DepositLocationRepo_, _DocumentTypeRepo_, _EmailTemplateRepo_, _EmbargoRepo_, _ManagerFilterColumnRepo_, _ManagerSubmissionListColumnRepo_, _ModalService_, _OrganizationCategory_, _OrganizationCategoryRepo_, _Organization_, _OrganizationRepo_, _Packager_, _PackagerRepo_, _RestApi_, _SavedFilterRepo_, _SidebarService_, _StorageService_, _SubmissionListColumnRepo_, _SubmissionRepo_, _SubmissionStatusRepo_, _UserRepo_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            q = $q;

            controller = $controller('SubmissionListController', {
                $filter: $filter,
                $location: $location,
                $q: q,
                $scope: scope,
                $window: $window,
                ControlledVocabularyRepo: _ControlledVocabularyRepo_,
                CustomActionDefinitionRepo: _CustomActionDefinitionRepo_,
                CustomActionValueRepo: _CustomActionValueRepo_,
                DepositLocationRepo: _DepositLocationRepo_,
                DocumentTypeRepo: _DocumentTypeRepo_,
                EmailTemplateRepo: _EmailTemplateRepo_,
                EmbargoRepo: _EmbargoRepo_,
                ManagerFilterColumnRepo: _ManagerFilterColumnRepo_,
                ManagerSubmissionListColumnRepo: _ManagerSubmissionListColumnRepo_,
                ModalService: _ModalService_,
                NamedSearchFilterGroup: mockParameterModel(q, mockNamedSearchFilterGroup),
                NgTableParams: mockNgTableParams,
                OrganizationCategoryRepo: _OrganizationCategoryRepo_,
                OrganizationRepo: _OrganizationRepo_,
                PackagerRepo: _PackagerRepo_,
                RestApi: _RestApi_,
                SavedFilterRepo: _SavedFilterRepo_,
                SidebarService: _SidebarService_,
                StorageService: _StorageService_,
                SubmissionListColumnRepo: _SubmissionListColumnRepo_,
                SubmissionRepo: _SubmissionRepo_,
                SubmissionStatusRepo: _SubmissionStatusRepo_,
                UserRepo: _UserRepo_,
                UserSettings: mockParameterModel(q, mockUserSettings),
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
        module('mock.controlledVocabulary');
        module('mock.controlledVocabularyRepo');
        module('mock.customActionDefinition');
        module('mock.customActionDefinitionRepo');
        module('mock.customActionValue');
        module('mock.customActionValueRepo');
        module('mock.depositLocation');
        module('mock.depositLocationRepo');
        module('mock.documentType');
        module('mock.documentTypeRepo');
        module('mock.emailTemplate');
        module('mock.emailTemplateRepo');
        module('mock.embargo');
        module('mock.embargoRepo');
        module('mock.managerFilterColumnRepo');
        module('mock.managerSubmissionListColumnRepo');
        module('mock.modalService');
        module('mock.namedSearchFilterGroup');
        module('mock.ngTableParams');
        module('mock.organizationCategory');
        module('mock.organizationCategoryRepo');
        module('mock.organization');
        module('mock.organizationRepo');
        module('mock.packager');
        module('mock.packagerRepo');
        module('mock.restApi');
        module('mock.savedFilter');
        module('mock.savedFilterRepo');
        module('mock.sidebarService');
        module('mock.storageService');
        module('mock.submissionListColumn');
        module('mock.submissionListColumnRepo');
        module('mock.submission');
        module('mock.submissionRepo');
        module('mock.submissionStatus');
        module('mock.submissionStatusRepo');
        module('mock.userRepo');
        module('mock.userSettings');
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
