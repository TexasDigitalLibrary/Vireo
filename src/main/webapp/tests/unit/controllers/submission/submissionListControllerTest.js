describe('controller: SubmissionListController', function () {

    var controller, location, q, scope, ManagerFilterColumnRepo, SavedFilterRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $filter, $location, $q, $rootScope, $window, _ControlledVocabularyRepo_, _CustomActionDefinitionRepo_, _CustomActionValueRepo_, _DepositLocationRepo_, _DocumentTypeRepo_, _EmailTemplateRepo_, _EmbargoRepo_, _ManagerFilterColumnRepo_, _ManagerSubmissionListColumnRepo_, _ModalService_, _OrganizationCategory_, _OrganizationCategoryRepo_, _Organization_, _OrganizationRepo_, _Packager_, _PackagerRepo_, _RestApi_, _SavedFilterRepo_, _SidebarService_, _StorageService_, _SubmissionListColumnRepo_, _SubmissionRepo_, _SubmissionStatusRepo_, _UserRepo_, _WsApi_) {
            installPromiseMatchers();;

            location = $location;
            q = $q;
            scope = $rootScope.$new()

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            ManagerFilterColumnRepo = _ManagerFilterColumnRepo_;
            SavedFilterRepo = _SavedFilterRepo_;

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
        module('mock.fieldProfile');
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

    describe('Are the scope methods defined', function () {
        it('addRowFilter should be defined', function () {
            expect(scope.addRowFilter).toBeDefined();
            expect(typeof scope.addRowFilter).toEqual("function");
        });
        it('applyFilter should be defined', function () {
            expect(scope.applyFilter).toBeDefined();
            expect(typeof scope.applyFilter).toEqual("function");
        });
        it('clearFilters should be defined', function () {
            expect(scope.clearFilters).toBeDefined();
            expect(typeof scope.clearFilters).toEqual("function");
        });
        it('disableColumn should be defined', function () {
            expect(scope.disableColumn).toBeDefined();
            expect(typeof scope.disableColumn).toEqual("function");
        });
        it('displaySubmissionProperty should be defined', function () {
            expect(scope.displaySubmissionProperty).toBeDefined();
            expect(typeof scope.displaySubmissionProperty).toEqual("function");
        });
        it('enableColumn should be defined', function () {
            expect(scope.enableColumn).toBeDefined();
            expect(typeof scope.enableColumn).toEqual("function");
        });
        it('finished should be defined', function () {
            expect(scope.finished).toBeDefined();
            expect(typeof scope.finished).toEqual("function");
        });
        it('getCustomActionLabelById should be defined', function () {
            expect(scope.getCustomActionLabelById).toBeDefined();
            expect(typeof scope.getCustomActionLabelById).toEqual("function");
        });
        it('getFilterChange should be defined', function () {
            expect(scope.getFilterChange).toBeDefined();
            expect(typeof scope.getFilterChange).toEqual("function");
        });
        it('getSubmissionProperty should be defined', function () {
            expect(scope.getSubmissionProperty).toBeDefined();
            expect(typeof scope.getSubmissionProperty).toEqual("function");
        });
        it('getUserById should be defined', function () {
            expect(scope.getUserById).toBeDefined();
            expect(typeof scope.getUserById).toEqual("function");
        });
        it('isDateColumn should be defined', function () {
            expect(scope.isDateColumn).toBeDefined();
            expect(typeof scope.isDateColumn).toEqual("function");
        });
        it('removeFilter should be defined', function () {
            expect(scope.removeFilter).toBeDefined();
            expect(typeof scope.removeFilter).toEqual("function");
        });
        it('removeFilterValue should be defined', function () {
            expect(scope.removeFilterValue).toBeDefined();
            expect(typeof scope.removeFilterValue).toEqual("function");
        });
        it('resetColumns should be defined', function () {
            expect(scope.resetColumns).toBeDefined();
            expect(typeof scope.resetColumns).toEqual("function");
        });
        it('resetColumnsToDefault should be defined', function () {
            expect(scope.resetColumnsToDefault).toBeDefined();
            expect(typeof scope.resetColumnsToDefault).toEqual("function");
        });
        it('resetRemoveFilters should be defined', function () {
            expect(scope.resetRemoveFilters).toBeDefined();
            expect(typeof scope.resetRemoveFilters).toEqual("function");
        });
        it('resetSaveFilter should be defined', function () {
            expect(scope.resetSaveFilter).toBeDefined();
            expect(typeof scope.resetSaveFilter).toEqual("function");
        });
        it('saveColumns should be defined', function () {
            expect(scope.saveColumns).toBeDefined();
            expect(typeof scope.saveColumns).toEqual("function");
        });
        it('saveFilter should be defined', function () {
            expect(scope.saveFilter).toBeDefined();
            expect(typeof scope.saveFilter).toEqual("function");
        });
        it('saveUserFilters should be defined', function () {
            expect(scope.saveUserFilters).toBeDefined();
            expect(typeof scope.saveUserFilters).toEqual("function");
        });
        it('selectPage should be defined', function () {
            expect(scope.selectPage).toBeDefined();
            expect(typeof scope.selectPage).toEqual("function");
        });
        it('sortBy should be defined', function () {
            expect(scope.sortBy).toBeDefined();
            expect(typeof scope.sortBy).toEqual("function");
        });
        it('updatePagination should be defined', function () {
            expect(scope.updatePagination).toBeDefined();
            expect(typeof scope.updatePagination).toEqual("function");
        });
        it('viewSubmission should be defined', function () {
            expect(scope.viewSubmission).toBeDefined();
            expect(typeof scope.viewSubmission).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('addRowFilter should add a filter', function () {
            var index = 0;
            var row = new mockSubmission(q);
            scope.page = { content: [] };
            scope.activeFilters = {
                addFilter: function(a, b, c, d) {
                    return payloadPromise(q.defer());
                }
            };

            scope.addRowFilter(index, row);
            scope.$digest();
        });
        it('applyFilter should assign a filter', function () {
            var filter = { columnsFlag: true, savedColumns: true };

            scope.activeFilters = {
                set: function(filter) {
                    return payloadPromise(q.defer());
                }
            };

            scope.applyFilter(filter);
            scope.$digest();
        });
        it('clearFilters should clear filters', function () {
            scope.activeFilters = {
                clearFilters: function(filter) {
                    return payloadPromise(q.defer());
                }
            };

            scope.clearFilters();
            scope.$digest();
        });
        it('disableColumn should disable a column', function () {
            scope.userColumns = [{ id: 1, status: "Displayed" }];
            scope.columns = [];

            scope.disableColumn(0);

            expect(scope.columns.length).toBe(1);
        });
        it('displaySubmissionProperty should return a value', function () {
            var row = {};
            var column = { inputType: mockInputType(q) };

            scope.displaySubmissionProperty(row, column);

            column.inputType.name = "INPUT_DATE";

            scope.displaySubmissionProperty(row, column);

            column.predicate = "dc.date.created";

            scope.displaySubmissionProperty(row, column);
        });
        it('enableColumn should enable a column', function () {
            scope.columns = [{ id: 1, status: undefined }];
            scope.userColumns = [];

            scope.enableColumn(0);

            expect(scope.userColumns.length).toBe(1);
        });
        it('finished should send a log message', function () {
            spyOn(console, "log").and.callThrough();

            scope.finished();

            expect(console.log).toHaveBeenCalled();
        });
        it('getCustomActionLabelById should return a label', function () {
            var response;
            var customAction = mockCustomActionDefinition(q);

            response = scope.getCustomActionLabelById(1);
            expect(response).toEqual(customAction.label);

            response = scope.getCustomActionLabelById(2);
            expect(response).not.toEqual(customAction.label);
        });
        it('getFilterChange should return the filterChange value', function () {
            var response;
            scope.filterChange = "test";

            response = scope.getFilterChange();
            expect(response).toEqual(scope.filterChange);
        });
        it('getSubmissionProperty should return a property', function () {
            var response;
            var row = { assignee: mockUser(q) };
            var column = { valuePath: [ "assignee" ] };

            response = scope.getSubmissionProperty(row, column);
            expect(response).toEqual(row.assignee);

            column.valuePath.push("FIXME: this behavior?");

            response = scope.getSubmissionProperty(row, column);

            column.valuePath = [ "FIXME: valid?", "profile" ];
            row.profile = [ new mockFieldProfile(q) ];

            scope.getSubmissionProperty(row, column);
        });
        it('getUserById should return a user', function () {
            var response;
            var user = new mockUser(1);
            user.mock(dataUser2);

            response = scope.getUserById(dataUser2.id);
            expect(response.id).toBe(dataUser2.id);
        });
        it('isDateColumn should return a boolean', function () {
            var response;

            response = scope.isDateColumn({ inputType: { name: "test" } });
            expect(response).toBe(false);

            response = scope.isDateColumn({ inputType: { name: "INPUT_DATETIME" } });
            expect(response).toBe(true);
        });
        it('removeFilter should remove a filter', function () {
            var filter = {};

            spyOn(SavedFilterRepo, "reset");

            scope.removeFilter(filter);
            scope.$digest();

            expect(SavedFilterRepo.reset).toHaveBeenCalled();
        });
        it('removeFilterValue should remove a filte rvalue', function () {
            scope.activeFilters = {
                removeFilter: function(a, b) {
                    return payloadPromise(q.defer());
                }
            };

            scope.removeFilterValue("a", "b");
            scope.$digest();
        });
        it('resetColumns should close a modal', function () {
            spyOn(scope, "closeModal");

            scope.resetColumns();

            expect(scope.closeModal).toHaveBeenCalled();
        });
        it('resetColumnsToDefault should reset the columns', function () {
            spyOn(scope, "resetColumns");

            scope.resetColumnsToDefault();
            scope.$digest();

            expect(scope.resetColumns).toHaveBeenCalled();
        });
        it('resetRemoveFilters should close a modal', function () {
            scope.activeFilters = {
                refresh: function() {
                    return payloadPromise(q.defer());
                }
            };

            spyOn(scope, "closeModal");

            scope.resetRemoveFilters();

            expect(scope.closeModal).toHaveBeenCalled();
        });
        // TODO: more work needed for this method.
        /*
        it('resetSaveFilter should reset the saved filter', function () {
            spyOn(scope, "closeModal");

            scope.resetSaveFilter();

            expect(scope.closeModal).toHaveBeenCalled();
        });*/
        it('saveColumns should reset the columns', function () {
            spyOn(scope, "resetColumns");

            scope.saveColumns();
            scope.$digest();

            expect(scope.resetColumns).toHaveBeenCalled();
        });
        it('saveFilter should close a modal', function () {
            spyOn(scope, "closeModal");

            scope.saveFilter();
            scope.$digest();
            expect(scope.closeModal).toHaveBeenCalled();

            scope.activeFilters.columnsFlag = true;

            scope.saveFilter();
            scope.$digest();
        });
        it('saveUserFilters should close a modal', function () {
            spyOn(scope, "closeModal");

            scope.saveUserFilters();
            scope.$digest();

            expect(scope.closeModal).toHaveBeenCalled();
        });
        it('selectPage should assign the page number', function () {
            var i = 1;

            scope.selectPage(i);

            expect(scope.pageNumber).toBe(i);
        });
        it('sortBy should sort', function () {
            scope.userColumns = [ { title: "a" }, { title: "b" } ];

            scope.sortBy({ title: "test", sort: "ASC" });
            scope.sortBy({ title: "test", sort: "DESC" });
            scope.sortBy({ sort: "NONE" });
            scope.sortBy({ sort: "unknown" });
        });
        it('updatePagination should assign the page number', function () {
            scope.pageNumber = 1;

            scope.updatePagination();

            expect(scope.pageNumber).toBe(0);
        });
        it('viewSubmission should change the url path', function () {
            var submission = new mockSubmission(q);
            submission.submissionWorkflowSteps = [ new mockWorkflowStep(q) ];

            spyOn(location, "path");

            scope.viewSubmission(submission);

            expect(location.path).toHaveBeenCalled();
        });
    });
});
