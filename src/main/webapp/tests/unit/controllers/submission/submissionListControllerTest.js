describe('controller: SubmissionListController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.controlledVocabularyRepo');
        module('mock.customActionDefinitionRepo');
        module('mock.customActionValueRepo');
        module('mock.depositLocationRepo');
        module('mock.documentTypeRepo');
        module('mock.emailTemplateRepo');
        module('mock.embargoRepo');
        module('mock.managerFilterColumnRepo');
        module('mock.managerSubmissionListColumnRepo');
        module('mock.modalService');
        module('mock.namedSearchFilterGroup');
        module('mock.ngTableParams');
        module('mock.organizationCategoryRepo');
        module('mock.organizationRepo');
        module('mock.packagerRepo');
        module('mock.restApi');
        module('mock.savedFilterRepo');
        module('mock.sidebarService');
        module('mock.storageService');
        module('mock.submissionListColumnRepo');
        module('mock.submissionRepo');
        module('mock.submissionStatusRepo');
        module('mock.userRepo');
        module('mock.userSettings');
        module('mock.wsApi');

        inject(function ($controller, $filter, $location, _$q_, $rootScope, $window, _ControlledVocabularyRepo_, _CustomActionDefinitionRepo_, _CustomActionValueRepo_, _DepositLocationRepo_, _DocumentTypeRepo_, _EmailTemplateRepo_, _EmbargoRepo_, _ManagerFilterColumnRepo_, _ManagerSubmissionListColumnRepo_, _ModalService_, _NamedSearchFilterGroup_, _NgTableParams_, _OrganizationCategoryRepo_, _OrganizationRepo_, _PackagerRepo_, _RestApi_, _SavedFilterRepo_, _SidebarService_, _StorageService_, _SubmissionListColumnRepo_, _SubmissionRepo_, _SubmissionStatusRepo_, _UserRepo_, _UserSettings_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('SubmissionListController', {
                $filter: $filter,
                $location: $location,
                $q: _$q_,
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
                NamedSearchFilterGroup: _NamedSearchFilterGroup_,
                NgTableParams: _NgTableParams_,
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
                UserSettings: _UserSettings_,
                WsApi: _WsApi_
            });

            // ensure that the isReady() is called.
            scope.$digest();
        });
    });

    /*describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });*/

});
