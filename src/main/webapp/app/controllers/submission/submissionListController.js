vireo.controller("SubmissionListController", function (NgTableParams, $controller, $filter, $location, $q, $scope, ControlledVocabularyRepo, CustomActionDefinitionRepo, CustomActionValueRepo, DepositLocationRepo, DocumentTypeRepo, EmailRecipient, EmailRecipientType, EmailTemplateRepo, EmbargoRepo, FieldPredicateRepo, ManagerFilterColumnRepo, ManagerSubmissionListColumnRepo, NamedSearchFilterGroup, OrganizationRepo, OrganizationCategoryRepo, PackagerRepo, SavedFilterRepo, SidebarService, SubmissionListColumnRepo, SubmissionRepo, SubmissionStatusRepo, UserRepo, UserSettings, WsApi) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.page = {
        number: sessionStorage.getItem("list-page-number") ? sessionStorage.getItem("list-page-number") : 1,
        count: 10,
        options: [
            5,
            10,
            20,
            40,
            60,
            100,
            200,
            400,
            500,
            1000
        ]
    };

    $scope.columns = [];

    $scope.userColumns = [];

    $scope.activeFilters = new NamedSearchFilterGroup();

    $scope.fieldPredicates = FieldPredicateRepo.getAll();

    var ready = $q.all([SubmissionListColumnRepo.ready(), ManagerSubmissionListColumnRepo.ready(), EmailTemplateRepo.ready(), FieldPredicateRepo.ready()]);

    var updateChange = function(change) {
        $scope.change = change;
        $scope.filterChange = change;
    };

    updateChange(false);

    ready.then(function () {

        // This is for piping the user/all columns through to the customizeFilters modal
        var filterColumns = {
            userFilterColumns: [],
            inactiveFilterColumns: []
        };

        var batchCommentEmail = {};

        var start;

        var query = function () {
            $scope.tableParams = new NgTableParams({
                page: $scope.page.number,
                count: $scope.page.count
            }, {
                counts: $scope.page.options,
                total: $scope.page.totalElements,
                filterDelay: 0,
                getData: function (params) {
                    start = window.performance.now();
                    return SubmissionRepo.query($scope.userColumns, params.page() > 0 ? params.page() - 1 : params.page(), params.count()).then(function (response) {
                        angular.extend($scope.page, angular.fromJson(response.body).payload.ApiPage);
                        // NOTE: this causes way to many subscriptions!!!
                        // SubmissionRepo.addAll($scope.page.content);
                        params.total($scope.page.totalElements);
                        $scope.page.count = params.count();
                        sessionStorage.setItem("list-page-size", $scope.page.count);
                        sessionStorage.setItem("list-page-number", $scope.page.number + 1);
                        return $scope.page.content;
                    });
                }.bind(start)
            });
            $scope.finished = function() {
                console.log('Building submission list took ' + ((window.performance.now() - start) / 1000.0) + ' seconds');
            }.bind(start);
        }.bind(start);

        var update = function () {

            SavedFilterRepo.reset();

            SubmissionListColumnRepo.reset();

            ManagerSubmissionListColumnRepo.reset();

            $q.all([SavedFilterRepo.ready(), SubmissionListColumnRepo.ready(), ManagerSubmissionListColumnRepo.ready()]).then(function() {
                ManagerSubmissionListColumnRepo.submissionListPageSize().then(function(response) {
                    var apiRes = angular.fromJson(response.body);
                    if(apiRes.meta.status === 'SUCCESS') {
                        $scope.page.count = sessionStorage.getItem("list-page-size") ? sessionStorage.getItem("list-page-size") : apiRes.payload.Integer;
                    }

                    var managerFilterColumns = ManagerFilterColumnRepo.getAll();
                    var submissionListColumns = SubmissionListColumnRepo.getAll();

                    $scope.userColumns = angular.fromJson(angular.toJson(ManagerSubmissionListColumnRepo.getAll()));

                    $scope.excludedColumns = [];

                    angular.copy($scope.userColumns, $scope.excludedColumns);

                    $scope.excludedColumns.push(SubmissionListColumnRepo.findByTitle('Search Box'));

                    $scope.columns = angular.fromJson(angular.toJson($filter('orderBy')($filter('exclude')(submissionListColumns, $scope.excludedColumns, 'title'), 'title')));

                    angular.extend(filterColumns, {
                        userFilterColumns: managerFilterColumns,
                        inactiveFilterColumns:  $filter('orderBy')($filter('exclude')(submissionListColumns, managerFilterColumns, 'title'), 'title')
                    });

                    query();

                    updateChange(false);
                });
            });
        };

        update();

        var assignableUsers = UserRepo.getAssignableUsers();
        var savedFilters = SavedFilterRepo.getAll();
        var emailTemplates = EmailTemplateRepo.getAll();
        var emailValidationPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        var organizations = OrganizationRepo.getAll();
        var organizationCategories = OrganizationCategoryRepo.getAll();
        var submissionStatuses = SubmissionStatusRepo.getAll();
        var documentTypes = DocumentTypeRepo.getAll();
        var customActionDefinitions = CustomActionDefinitionRepo.getAll();
        var depositLocations = DepositLocationRepo.getAll();
        var embargos = EmbargoRepo.getAll();
        var packagers = PackagerRepo.getAll();
        var controlledVocabularies = ControlledVocabularyRepo.getAll();

        var userSettings = new UserSettings();

        var addBatchCommentEmail = function (message) {
            batchCommentEmail.adding = true;
            angular.extend(apiMapping.Submission.batchComment, {
                'data': message
            });
            var promise = WsApi.fetch(apiMapping.Submission.batchComment);
            promise.then(function (res) {
                if (res.meta && res.meta.status == "INVALID") {
                    submission.setValidationResults(res.payload.ValidationResults);
                } else {
                    resetBatchCommentEmailModal(batchCommentEmail);
                }
            });
            return promise;
        };

        var updateTemplate = function (template) {
            batchCommentEmail.message = template.message;
            batchCommentEmail.subject = template.subject;
        };

        var resetBatchCommentEmailModal = function (batchCommentEmail) {
            $scope.closeModal();
            batchCommentEmail.adding = false;
            batchCommentEmail.commentVisibility = userSettings.notes_mark_comment_as_private_by_default ? "private" : "public";
            batchCommentEmail.recipientEmails = userSettings.notes_email_student_by_default === "true" ? [new EmailRecipient({
                name: "Submitter",
                type: EmailRecipientType.SUBMITTER,
                data: "Submitter"
            })] : [];
            batchCommentEmail.ccRecipientEmails = [];
            batchCommentEmail.sendEmailToRecipient = (batchCommentEmail.commentVisibility === "public" || userSettings.notes_email_student_by_default === "true") || (userSettings.notes_cc_student_advisor_by_default === "true");
            batchCommentEmail.sendEmailToCCRecipient = userSettings.notes_cc_student_advisor_by_default === "true";
            batchCommentEmail.subject = "";
            batchCommentEmail.message = "";
            batchCommentEmail.actionLogCurrentLimit = $scope.actionLogLimit;
            batchCommentEmail.selectedTemplate = "";
        };

        resetBatchCommentEmailModal(batchCommentEmail);

        var getTypeAheadByPredicateName = function(predicateValue) {
            var words = [];
            for (var h in organizations) {
                var organization = organizations[h];
                for (var i in organization.aggregateWorkflowSteps) {
                    var aggWorkflowStep = organization.aggregateWorkflowSteps[i];
                    for (var j in aggWorkflowStep.aggregateFieldProfiles) {
                        var currentFieldProfile = aggWorkflowStep.aggregateFieldProfiles[j];
                        if (currentFieldProfile.fieldPredicate.value === predicateValue) {
                            if(angular.isDefined(currentFieldProfile.controlledVocabulary)) {
                                var cv = currentFieldProfile.controlledVocabulary;
                                for (var l in cv.dictionary) {
                                    var dictionary = cv.dictionary[l];
                                    if (words.indexOf(dictionary.name) == -1) {
                                        words.push(dictionary.name);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return words;
        };


        var addFilter = function (column, gloss) {
            var filterValue = $scope.furtherFilterBy[column.title.split(" ").join("")];
            if (filterValue !== null) {
                filterValue = filterValue.toString();
            }
            $scope.activeFilters.addFilter(column.title, filterValue, gloss, column.exactMatch).then(function () {
                $scope.furtherFilterBy[column.title.split(" ").join("")] = "";
                query();
            });
        };

        var addExactMatchFilter = function (column, gloss) {
            column.exactMatch = true;
            addFilter(column, gloss);
        };

        var addDateFilter = function (column) {
            var dateValue = $scope.furtherFilterBy[column.title.split(" ").join("")].d1.toISOString();
            var dateGloss = $filter('date')($scope.furtherFilterBy[column.title.split(" ").join("")].d1, column.title==="Graduation Semester" ? "MMMM yyyy" : "MM/dd/yyyy");
            dateValue += $scope.furtherFilterBy[column.title.split(" ").join("")].d2 ? "|" + $scope.furtherFilterBy[column.title.split(" ").join("")].d2.toISOString() : "";
            dateGloss += $scope.furtherFilterBy[column.title.split(" ").join("")].d2 ? " to " + $filter('date')($scope.furtherFilterBy[column.title.split(" ").join("")].d2, "MM/dd/yyyy") : "";
            $scope.activeFilters.addFilter(column.title, dateValue, dateGloss, false).then(function () {
                $scope.furtherFilterBy[column.title.split(" ").join("")] = "";
                query();
            });
        };

         $scope.addRowFilter = function ($index, row) {
            $scope.page.content.splice($index, 1);
            var columnTitle = "Exclude";
            var value = row.id.toString();
            var gloss = "Submission #" + row.id;
            $scope.activeFilters.addFilter(columnTitle, value, gloss, true).then (function () {
                query();
            });
        };

        var resetBatchProcess = function () {
            $scope.advancedfeaturesBox.processing = false;
            $scope.advancedfeaturesBox.exporting = false;
            $scope.advancedfeaturesBox.assignee = assignableUsers[0];
            $scope.advancedfeaturesBox.packager = packagers[0];
            $scope.advancedfeaturesBox.type = 'Active Filter';
            $scope.advancedfeaturesBox.filterId = undefined;
            $scope.closeModal();
        };

        var batchUpdateStatus = function (newStatus) {
            $scope.advancedfeaturesBox.processing = true;
            SubmissionRepo.batchUpdateStatus(newStatus).then(function () {
                resetBatchProcess();
                query();
            });
        };

        var batchAssignTo = function (assignee) {
            $scope.advancedfeaturesBox.processing = true;
            SubmissionRepo.batchAssignTo(assignee).then(function () {
                resetBatchProcess();
                query();
            });
        };

        var batchPublish = function (newStatus) {
            $scope.advancedfeaturesBox.processing = true;
            SubmissionRepo.batchPublish($scope.advancedfeaturesBox.depositLocation).then(function () {
                resetBatchProcess();
                query();
            });
        };

        var batchDownloadExport = function (packager, filterId) {
            $scope.advancedfeaturesBox.exporting = true;
            SubmissionRepo.batchExport(packager, filterId).then(function (data) {
                saveAs(new Blob([data], {
                    type: packager.mimeType
                }), packager.name + '.' + packager.fileExtension);
                resetBatchProcess();
            });
        };

        var setupEmailTemplates = function() {
            var addDefaultTemplate = true;

            for (var i in emailTemplates) {
                var template = emailTemplates[i];
                if (template.name === "Choose a Message Template") {
                    addDefaultTemplate = false;
                    break;
                }
            }

            if (addDefaultTemplate) {
                emailTemplates.unshift({
                    name: "Choose a Message Template"
                });
            }
        };

        setupEmailTemplates();

        var getBatchContactEmails = function() {
            var emails = [
                {
                  name: "Submitter",
                  type: EmailRecipientType.SUBMITTER,
                  data: "Submitter"
                },
                {
                  name: "Advisor",
                  type: EmailRecipientType.ADVISOR,
                  data: "Advisor"
                },
                {
                  name: "Assignee",
                  type: EmailRecipientType.ASSIGNEE,
                  data: "Assignee"
                },
                {
                  name: "Organization",
                  type: EmailRecipientType.ORGANIZATION,
                  data: null
                }
            ];

            for (var i in $scope.fieldPredicates) {
                if ($scope.fieldPredicates[i].value === "dc.contributor.advisor") {
                    emails.push({
                      name: "Committee Chair",
                      type: EmailRecipientType.CONTACT,
                      data: $scope.fieldPredicates[i].id
                    });

                    break;
                }
            }

            return emails;
        };

        var addBatchEmailAddressee = function(emails, formField) {
            var recipient = formField.$$rawModelValue;

            if (recipient) {
                if (typeof recipient === 'string') {
                    if (!validateBatchEmailAddressee(formField)) return;

                    recipient = new EmailRecipient({
                      name: recipient,
                      type: EmailRecipientType.PLAIN_ADDRESS,
                      data: recipient
                    });
                }

                emails.push(recipient);

                //This is not ideal, as it assumes the attr name and attr ngModel are the same.
                $scope[formField.$$attr.name + "Invalid"] = false;
                batchCommentEmail[formField.$$attr.name] = "";
            }
        };

        var validateBatchEmailAddressee = function(formField) {
            var valueIsContact = false;

            if (typeof formField.$$rawModelValue !== 'string') {
                var allContacts = getBatchContactEmails();

                for (var i in allContacts) {
                    var contact = allContacts[i];
                    if (formField.$$rawModelValue && formField.$$rawModelValue.type === contact.type) {
                        valueIsContact = true;
                        break;
                    }
                }
            }

            $scope[formField.$$attr.name+"Invalid"] = formField.$invalid && !valueIsContact;
            return !$scope[formField.$$attr.name + "Invalid"];
        };

        var isBatchEmailAddresseeInvalid = function(formField) {
            return formField.$invalid && $scope[formField.$$attr.name + "Invalid"];
        };

        var removeBatchEmailAddressee = function (email, destinationModel) {
            var removeIndex = destinationModel.indexOf(email);
            destinationModel.splice(removeIndex, 1);
        };

        var disableAddBatchComment = function () {
            var disable = batchCommentEmail.adding ||
                          batchCommentEmail.subject === undefined ||
                          batchCommentEmail.subject === "" ||
                          batchCommentEmail.message === undefined ||
                          batchCommentEmail.message === "";

            if (!disable && batchCommentEmail.commentVisibility === 'public') {
                if (batchCommentEmail.sendEmailToRecipient) {
                    if (batchCommentEmail.sendEmailToCCRecipient) {
                        disable = batchCommentEmail.recipientEmails.length === 0 || batchCommentEmail.ccRecipientEmails.length === 0;
                    } else {
                        disable = batchCommentEmail.recipientEmails.length === 0;
                    }
                }
            }

            return disable;
        };

        var getValueFromArray = function (array, col) {
            var value = "";
            for (var j in array) {
                var member = array[j];
                if (member.fieldPredicate !== undefined) {
                    if (member.fieldPredicate.value === col.predicate) {
                        value += value.length > 0 ? ", " + member.value : member.value;
                    }
                } else {
                    var path = col.valuePath;
                    var curr = member;
                    for (var p = 1; p < path.length; p++) {
                        curr = curr[path[p]];
                    }
                    value += value.length > 0 ? ", " + curr : curr;
                }
            }
            return value;
        };

        var getAssigneeDisplayName = function (row) {
            return row.assignee.firstName + " " + row.assignee.lastName;
        };

        var getFiltersWithColumns = function() {
            return savedFilters.filter(function(filter) { return filter.columnsFlag; });
        };

        var getFiltersWithoutColumns = function() {
            return savedFilters.filter(function(filter) { return !filter.columnsFlag; });
        };

        var checkDisabled = function (dateAndMode) {
            var disabled = true;

            for (var h in controlledVocabularies) {
                var cv = controlledVocabularies[h];
                for (var i in cv.dictionary) {
                    var dictionary = cv.dictionary[i];
                    if (dictionary.name == dateAndMode.date.getMonth()) {
                        disabled = false;
                        break;
                    }
                }
            }
            return disabled;
        };

        var datepickerOptions = {
            minMode: "month",
            minViewMode: "month",
            maxViewMode: "month",
            maxMode: "month",
            dateDisabled: checkDisabled,
            customClass: function (dateAndMode) {
                if (checkDisabled(dateAndMode)) return "disabled";
            }
        };

        $scope.furtherFilterBy = {
            "title": "Further Filter By:",
            "viewUrl": "views/sideboxes/furtherFilterBy/furtherFilterBy.html",
            "filterColumns": filterColumns,
            "addFilter": addFilter,
            "addExactMatchFilter": addExactMatchFilter,
            "addDateFilter": addDateFilter,
            "submissionStatuses": submissionStatuses,
            "customActionDefinitions": customActionDefinitions,
            "organizations": organizations,
            "organizationCategories": organizationCategories,
            "documentTypes": documentTypes,
            "embargos": embargos,
            "assignableUsers": assignableUsers,
            "defaultLimit": 3,
            "getTypeAheadByPredicateName": getTypeAheadByPredicateName,
            "datepickerOptions": datepickerOptions
        };

        $scope.advancedfeaturesBox = {
            "packager": packagers[0],
            "type": 'Active Filter',
            "filterTypes": ['Active Filter', 'Saved Filter'],
            "filterId": undefined,
            "getFiltersWithColumns": getFiltersWithColumns,
            "getFiltersWithoutColumns": getFiltersWithoutColumns,
            "title": "Advanced Features:",
            "processing": false,
            "depositLocations": depositLocations,
            "viewUrl": "views/sideboxes/advancedFeatures.html",
            "resetBatchProcess": resetBatchProcess,
            "batchUpdateStatus": batchUpdateStatus,
            "submissionStatuses": submissionStatuses,
            "newStatus": submissionStatuses[0],
            "assignableUsers": assignableUsers,
            "batchAssignTo": batchAssignTo,
            "batchPublish": batchPublish,
            "resetBatchCommentEmailModal": resetBatchCommentEmailModal,
            "batchCommentEmail": batchCommentEmail,
            "addBatchCommentEmail": addBatchCommentEmail,
            "getBatchContactEmails": getBatchContactEmails,
            "addBatchEmailAddressee": addBatchEmailAddressee,
            "validateBatchEmailAddressee": validateBatchEmailAddressee,
            "isBatchEmailAddresseeInvalid": isBatchEmailAddresseeInvalid,
            "removeBatchEmailAddressee": removeBatchEmailAddressee,
            "disableAddBatchComment": disableAddBatchComment,
            "emailValidationPattern": emailValidationPattern,
            "emailTemplates": emailTemplates,
            "updateTemplate": updateTemplate,
            "batchDownloadExport": batchDownloadExport,
            "packagers": packagers
        };

        $scope.getSubmissionProperty = function (row, col) {
            var value;
            for (var i in col.valuePath) {
                if(typeof col.valuePath[i] !== 'function') {
                    if (value === undefined) {
                        value = row[col.valuePath[i]];
                    } else {
                        if (value instanceof Array) {
                            return getValueFromArray(value, col);
                        } else {
                            if (value !== null) {
                                if (col.valuePath[0] === "assignee") {
                                    value = getAssigneeDisplayName(row);
                                } else {
                                    value = value[col.valuePath[i]];
                                }
                            }
                        }
                    }
                }
            }
            return value;
        };

        $scope.displaySubmissionProperty = function (row, col) {
            var value = $scope.getSubmissionProperty(row, col);
            if ($scope.isDateColumn(col)) {
                if(col.predicate === 'dc.date.issued') {
                    value = $filter('date')(value, 'MMMM yyyy');
                } else {
                    value = $filter('date')(value, 'MMM dd, yyyy');
                }
            }
            return value;
        };

        $scope.getCustomActionLabelById = function (id) {
            for (var i in customActionDefinitions) {
                if (customActionDefinitions[i].id === id) {
                    return customActionDefinitions[i].label;
                }
            }
        };

        $scope.isDateColumn = function (col) {
            return (col.inputType.name == 'INPUT_DATETIME' || col.inputType.name == 'INPUT_DEGREEDATE');
        };

        $scope.getUserById = function (userId) {
            return UserRepo.findById(userId);
        };

        $scope.removeFilterValue = function (criterionName, filterValue) {
            $scope.activeFilters.removeFilter(criterionName, filterValue).then(function () {
                query();
            });
        };

        $scope.clearFilters = function () {
            $scope.activeFilters.clearFilters().then(function () {
                query();
            });
        };

        $scope.saveFilter = function () {
            if ($scope.activeFilters.columnsFlag) {
                $scope.activeFilters.savedColumns = $scope.userColumns;
            }
            SavedFilterRepo.create($scope.activeFilters).then(function () {
                $scope.closeModal();
                SavedFilterRepo.reset();
            });
        };

        $scope.applyFilter = function (filter) {
            if (filter.columnsFlag) {
                $scope.userColumns = filter.savedColumns;
            }
            $scope.activeFilters.set(filter).then(function () {
                query();
            });
        };

        $scope.resetSaveFilter = function () {
            $scope.closeModal();
            $scope.activeFilters.refresh();
            var filtersPreviouslyDisabled = [];
            for (var i = filterColumns.userFilterColumns.length - 1; i >= 0; i--) {
                if (filterColumns.userFilterColumns[i].status === 'previouslyDisabled') {
                    delete filterColumns.userFilterColumns[i].status;
                    filtersPreviouslyDisabled.push(filterColumns.userFilterColumns[i]);
                    filterColumns.userFilterColumns.splice(i, 1);
                }
            }
            var filtersPreviouslyDisplayed = [];
            for (var j = filterColumns.inactiveFilterColumns.length - 1; j >= 0; j--) {
                if (filterColumns.inactiveFilterColumns[j].status === 'previouslyDisplayed') {
                    delete filterColumns.inactiveFilterColumns[j].status;
                    filtersPreviouslyDisplayed.push(filterColumns.inactiveFilterColumns[j]);
                    filterColumns.inactiveFilterColumns.splice(j, 1);
                }
            }

            var userFilterColumns = filtersPreviouslyDisplayed.concat(filterColumns.userFilterColumns);
            var inactiveFilterColumns = filtersPreviouslyDisabled.concat(filterColumns.inactiveFilterColumns);

            angular.extend(filterColumns, {
                userFilterColumns: userFilterColumns,
                inactiveFilterColumns: inactiveFilterColumns
            });

            updateChange(false);
        };

        $scope.removeFilter = function (filter) {
            SavedFilterRepo.delete(filter).then(function () {
                SavedFilterRepo.reset();
            });
        };

        $scope.resetRemoveFilters = function () {
            $scope.closeModal();
        };

        $scope.getFilterChange = function () {
            return $scope.filterChange;
        };

        $scope.updatePagination = function () {
            $scope.pageNumber = 0;
            $scope.change = true;
        };

        $scope.selectPage = function (i) {
            $scope.pageNumber = i;
            query();
        };

        $scope.resetColumns = function () {
            ManagerSubmissionListColumnRepo.reset();
            update();
            $scope.closeModal();
            updateChange(false);
        };

        $scope.resetColumnsToDefault = function () {
            ManagerSubmissionListColumnRepo.resetSubmissionListColumns().then(function () {
                $scope.resetColumns();
            });
        };

        $scope.saveColumns = function () {
            ManagerSubmissionListColumnRepo.updateSubmissionListColumns($scope.userColumns, $scope.page.count).then(function () {
                $scope.page.number = 1;
                sessionStorage.setItem("list-page-size", $scope.page.count);
                $scope.resetColumns();
            });
        };

        $scope.saveUserFilters = function () {
            ManagerFilterColumnRepo.updateFilterColumns(filterColumns.userFilterColumns).then(function () {
                for (var i in filterColumns.userFilterColumns) {
                    delete filterColumns.userFilterColumns[i].status;
                }
                update();
                $scope.closeModal();
            });
        };

        $scope.sortBy = function (sortColumn) {

            switch (sortColumn.sort) {
            case "ASC":
                {
                    sortColumn.sort = "NONE";
                    sortColumn.sortOrder = 0;
                }
                break;
            case "DESC":
                {
                    sortColumn.sort = "ASC";
                    sortColumn.sortOrder = 1;
                }
                break;
            case "NONE":
                {
                    sortColumn.sort = "DESC";
                    sortColumn.sortOrder = 1;
                }
                break;
            default:
                break;
            }

            angular.forEach($scope.userColumns, function (userColumn) {
                if (sortColumn.title != userColumn.title) {
                    userColumn.sort = "NONE";
                    userColumn.sortOrder = 0;
                }
            });

            query();

        };

        var createDisplayedColumnOptions = function() {
            return {
                accept: function (sourceItemHandleScope, destSortableScope, destItemScope) {
                    return true;
                },
                itemMoved: function (event) {
                    event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'previouslyDisplayed' : undefined;
                    updateChange(true);
                },
                orderChanged: function (event) {
                    updateChange(true);
                },
                containment: 'displayed-column-container',
                containerPositioning: 'relative',
                additionalPlaceholderClass: 'column-placeholder'
            };
        };

        var createDisabledColumnOptions = function() {
            return {
                accept: function (sourceItemHandleScope, destSortableScope, destItemScope) {
                    return true;
                },
                itemMoved: function (event) {
                    event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'previouslyDisabled' : undefined;
                    updateChange(true);
                },
                orderChanged: function (event) {
                    updateChange(true);
                },
                containment: 'disabled-column-container',
                containerPositioning: 'relative',
                additionalPlaceholderClass: 'column-placeholder'
            };
        };

        $scope.disableColumn = function(column) {
            $scope.userColumns.splice($scope.userColumns.indexOf(column), 1);
            $scope.columns.push(column);
            $scope.change = true;
            column.status = !column.status ? 'previouslyDisplayed' : undefined;
        };

        $scope.enableColumn = function(column) {
            $scope.columns.splice($scope.columns.indexOf(column), 1);
            $scope.userColumns.push(column);
            $scope.change = true;
            column.status = !column.status ? 'previouslyDisabled' : undefined;
        };

        $scope.displayedColumnOptions = createDisplayedColumnOptions();

        $scope.disabledColumnOptions = createDisabledColumnOptions();

        var disableFilter = function(column) {
            filterColumns.userFilterColumns.splice(filterColumns.userFilterColumns.indexOf(column), 1);
            filterColumns.inactiveFilterColumns.push(column);
            $scope.filterChange = true;
            column.status = !column.status ? 'previouslyDisplayed' : undefined;
        };

        var enableFilter = function(column) {
            filterColumns.inactiveFilterColumns.splice(filterColumns.inactiveFilterColumns.indexOf(column), 1);
            filterColumns.userFilterColumns.push(column);
            $scope.filterChange = true;
            column.status = !column.status ? 'previouslyDisabled' : undefined;
        };

        var displayedFilterColumnOptions = createDisplayedColumnOptions();

        var disabledFilterColumnOptions = createDisabledColumnOptions();

        $scope.viewSubmission = function (submission) {
            $location.path("/admin/view/" + submission.id + "/" + submission.submissionWorkflowSteps[0].id);
        };

        SidebarService.addBoxes([{
                "title": "Now Filtering By:",
                "viewUrl": "views/sideboxes/nowFiltering.html",
                "activeFilters": $scope.activeFilters,
                "removeFilterValue": $scope.removeFilterValue
            }, {
                "title": "Filter Options:",
                "viewUrl": "views/sideboxes/filterOptions.html",
                "activeFilters": $scope.activeFilters,
                "clearFilters": $scope.clearFilters,
                "saveFilter": $scope.saveFilter,
                "savedFilters": savedFilters,
                "filterColumns": filterColumns,
                "disableFilter": disableFilter,
                "enableFilter": enableFilter,
                "displayedFilterColumnOptions": displayedFilterColumnOptions,
                "disabledFilterColumnOptions": disabledFilterColumnOptions,
                "saveUserFilters": $scope.saveUserFilters,
                "getFilterChange": $scope.getFilterChange,
                "resetSaveFilter": $scope.resetSaveFilter,
                "resetSaveUserFilters": $scope.resetSaveFilter,
                "applyFilter": $scope.applyFilter,
                "resetRemoveFilters": $scope.resetRemoveFilters,
                "removeFilter": $scope.removeFilter,
                "getUserById": $scope.getUserById
            },
            $scope.furtherFilterBy,
            $scope.advancedfeaturesBox
        ]);

    });

});

vireo.filter('exclude', function () {
    return function (input, exclude, prop) {
        if (!angular.isArray(input)) {
            return input;
        }
        if (!angular.isArray(exclude)) {
            exclude = [];
        }
        if (prop) {
            exclude = exclude.map(function byProp(item) {
                return item[prop];
            });
        }
        return input.filter(function byExclude(item) {
            return exclude.indexOf(prop ? item[prop] : item) === -1;
        });
    };
});

vireo.filter('range', function () {
    return function (val, range) {
        range = parseInt(range);
        for (var i = 0; i < range; i++) {
            val.push(i);
        }
        return val;
    };
});
