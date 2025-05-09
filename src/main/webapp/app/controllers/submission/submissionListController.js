vireo.controller("SubmissionListController", function (NgTableParams, $controller, $filter, $location, $q, $scope, ControlledVocabularyRepo, CustomActionDefinitionRepo, DepositLocationRepo, DocumentTypeRepo, EmailRecipient, EmailRecipientType, EmailTemplateRepo, EmbargoRepo, FieldPredicateRepo, FieldValueRepo, ManagerFilterColumnRepo, ManagerSubmissionListColumnRepo, NamedSearchFilterGroup, OrganizationRepo, OrganizationCategoryRepo, PackagerRepo, SavedFilter, SavedFilterRepo, SidebarService, SubmissionListColumnRepo, SubmissionRepo, SubmissionStatusRepo, UserRepo, UserSettings, $window, WsApi) {

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

    $scope.simplifyTitle = function (str) {
      return str.replaceAll(/[()\s]/gi, '');
    };

    var userSettings = new UserSettings();

    var rowFilterTitle = "Exclude";

    var ready = $q.all([SubmissionListColumnRepo.ready(), ManagerSubmissionListColumnRepo.ready(), EmailTemplateRepo.ready(), FieldPredicateRepo.ready(), userSettings.ready()]);

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
            var sessionPageNumber = sessionStorage.getItem("list-page-number");
            var sessionPageSize = sessionStorage.getItem("list-page-size");

            $scope.tableParams = new NgTableParams({
                page: angular.isDefined(sessionPageNumber) && sessionPageNumber !== null ? sessionPageNumber : $scope.page.number,
                count: angular.isDefined(sessionPageSize) && sessionPageSize !== null ? sessionPageSize : $scope.page.count,
            }, {
                counts: $scope.page.options,
                total: $scope.page.totalElements,
                filterDelay: 0,
                getData: function (params) {
                    start = window.performance.now();
                    return queryGetData(params);
                }.bind(start)
            });
            $scope.finished = function() {
                console.log('Building submission list took ' + ((window.performance.now() - start) / 1000.0) + ' seconds');
            }.bind(start);
        }.bind(start);

        var queryGetData = function (params, forcePageNumber) {
            var requestPage = 0;

            if (forcePageNumber === undefined) {
                if (params.page() > 0) {
                    requestPage = params.page() - 1;
                }
            } else {
                requestPage = forcePageNumber;
            }

            return SubmissionRepo.query($scope.userColumns, requestPage, params.count()).then(function (response) {
                var page = angular.fromJson(response.body).payload.ApiPage;

                // Forcibly fix invalid page and try again, but only once.
                if (forcePageNumber === undefined && angular.isDefined(page.number) && angular.isDefined(page.totalPages)) {
                    var totalPages = parseInt(page.totalPages);
                    var pageNumber = parseInt(page.number);

                    if (!isNaN(totalPages) && !isNaN(pageNumber) && (pageNumber >= totalPages || pageNumber < 0)) {
                        pageNumber = pageNumber >= totalPages && totalPages > 0 ? totalPages - 1 : 0;

                        return queryGetData(params, pageNumber);
                    }
                }

                angular.extend($scope.page, page);

                // The service sets page number starting at 0, which needs to be incremented by 1 when provided.
                if (angular.isDefined(page.number)) {
                    $scope.page.number++;
                }

                params.total($scope.page.totalElements);
                params.page($scope.page.number);
                $scope.page.count = params.count();
                sessionStorage.setItem("list-page-size", $scope.page.count);
                sessionStorage.setItem("list-page-number", $scope.page.number);

                return $scope.page.content;
            });
        }

        var update = function (reloadList) {
            SavedFilterRepo.reset();
            ManagerFilterColumnRepo.reset();
            SubmissionListColumnRepo.reset();
            ManagerSubmissionListColumnRepo.reset();

            $q.all([SavedFilterRepo.ready(), SubmissionListColumnRepo.ready(), ManagerSubmissionListColumnRepo.ready()]).then(function() {

                // Only get the list size if/when there is no page count set.
                if (angular.isUndefined(sessionStorage.getItem("list-page-size")) || sessionStorage.getItem("list-page-size") === null) {
                    ManagerSubmissionListColumnRepo.submissionListPageSize().then(function (response) {
                        var apiRes = angular.fromJson(response.body);

                        if (apiRes.meta.status === 'SUCCESS') {
                            $scope.page.count = apiRes.payload.Integer;
                        } else if (sessionStorage.getItem("list-page-size")) {
                            $scope.page.count = sessionStorage.getItem("list-page-size");
                        }

                        processUpdate(reloadList);
                    });
                } else {
                    processUpdate(reloadList);
                }
            });
        };

        var processUpdate = function (reloadList) {
            var allManagerFilters = ManagerFilterColumnRepo.getAll();
            var allSubmissionListFilters = SubmissionListColumnRepo.getAll();
            var managerFilterColumns = [];
            var submissionListColumns = [];
            var submissionListColumnsForManage = [];

            if (!!allManagerFilters) {
                managerFilterColumns = allManagerFilters.filter(function excludeSearchBox(slc) {
                    return slc.title !== 'Search Box';
                });
            }

            if (!!allSubmissionListFilters) {
                submissionListColumns = allSubmissionListFilters.filter(function excludeCustomFilters(slc) {
                    return slc.title !== 'Search Box' && slc.title !== "Submission Type (List)" && slc.title !== "Graduation Semester (List)" && slc.title !== "Embargo Type";
                });

                submissionListColumnsForManage = allSubmissionListFilters.filter(function excludeSearchBox(slc) {
                    return slc.title !== 'Search Box';
                });
            }

            $scope.userColumns = angular.fromJson(angular.toJson(ManagerSubmissionListColumnRepo.getAll()));

            angular.forEach($scope.userColumns, function (userColumn) {
                if ($scope.activeFilters.sortColumnTitle === userColumn.title) {
                    userColumn.sortOrder = 1;
                    userColumn.sort = $scope.activeFilters.sortDirection;
                }
            });

            $scope.excludedColumns = [];

            angular.copy($scope.userColumns, $scope.excludedColumns);

            $scope.excludedColumns.push(SubmissionListColumnRepo.findByTitle('Search Box'));

            $scope.columns = angular.fromJson(angular.toJson($filter('orderBy')($filter('exclude')(submissionListColumns, $scope.excludedColumns, 'title'), 'title')));

            setFilterColumns(managerFilterColumns, $filter('orderBy')($filter('exclude')(submissionListColumnsForManage, managerFilterColumns, 'title'), 'title'));

            if (reloadList === true) {
                query();
            }

            updateChange(false);
        };

        update(true);

        const withoutActiveFilter = function(value) {
            return $scope.activeFilters.namedSearchFilters.filter((nsf) => nsf.filterValues.indexOf(value) >= 0).length == 0;
        };

        var assignableUsers = UserRepo.getAssignableUsers(0, 0);
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
        var submissionTypeList = FieldValueRepo.findValuesByPredicateValue('submission_type');
        var graduationSemesters = FieldValueRepo.findValuesByPredicateValue('dc.date.issued', function (a, b) {
            try {
                if (typeof a !== 'string' || typeof b !== 'string') {
                    throw new Error('Invalid input type. Both inputs must be strings.');
                }

                const regex = /^[A-Za-z]+\s\d{4}$/;

                if (!regex.test(a) || !regex.test(b)) {
                    throw new Error('Invalid input format. Inputs must be in the format "Month Year".');
                }

                const [monthA, yearA] = a.split(' ');
                const [monthB, yearB] = b.split(' ');

                const dateA = new Date(`${monthA} 1, ${yearA}`);
                const dateB = new Date(`${monthB} 1, ${yearB}`);

                if (isNaN(dateA) || isNaN(dateB)) {
                    throw new Error('Invalid date. Inputs must be valid dates.');
                }

                return dateB - dateA;
            } catch (error) {
                console.error(error.message);
                return 0;
            }
        });

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
            $scope.resetPagination();

            var filterValue = $scope.furtherFilterBy[$scope.simplifyTitle(column.title)];
            if (!!filterValue && typeof filterValue !== 'string') {
                filterValue = filterValue.toString();
            }

            $scope.activeFilters.addFilter(column.title, filterValue, gloss, column.exactMatch).then(function () {
                $scope.furtherFilterBy[$scope.simplifyTitle(column.title)] = "";
                query();
            });
        };

        var addExactMatchFilter = function (column, gloss) {
            column.exactMatch = true;
            addFilter(column, gloss);
        };

        var addDateFilter = function (column) {
            var key = $scope.simplifyTitle(column.title);
            var date1Value = $scope.furtherFilterBy[key].d1
            var date2Value = $scope.furtherFilterBy[key].d2 ? $scope.furtherFilterBy[key].d2 : null;
            var dateGloss = date1Value;

            if (angular.isDefined(column) && angular.isDefined(column.inputType) && angular.isDefined(column.inputType.name)) {
                var dateColumn = $scope.findDateColumn(column.inputType.name);

                if (dateColumn !== null && angular.isDefined(date1Value) && date1Value != null) {
                    // Work-around datepicker messing up the time zone by stripping off the time and setting it to 0 to prevent Javascript date() from altering the day based on time zone.
                    var date = new Date(date1Value.getFullYear(), date1Value.getMonth(), date1Value.getDate(), 0, 0, 0);
                    date1Value = $filter('date')(date, dateColumn.database);
                    dateGloss = date1Value;

                    if (date2Value !== null) {
                        // Work-around datepicker messing up the time zone by stripping off the time and setting it to 0 to prevent Javascript date() from altering the day based on time zone.
                        var date = new Date(date2Value.getFullYear(), date2Value.getMonth(), date2Value.getDate(), 0, 0, 0);
                        date2Value = $filter('date')(date, dateColumn.database);
                    }
                }
            } else {
                var date = new Date(date1Value);

                if (!Number.isNaN(date) && !Number.isNaN(date.getTime())) {
                    date1Value = date.toISOString();

                    if (date2Value !== null) {
                        date = new Date(date2Value);

                        if (!Number.isNaN(date) && !Number.isNaN(date.getTime())) {
                            date2Value = date2Value.toISOString();
                        }
                    }
                }
            }

            if (date2Value !== null) {
                date1Value += "|" + date2Value;
                dateGloss += " to " + date2Value;
            }

            $scope.activeFilters.addFilter(column.title, date1Value, dateGloss, false).then(function () {
                $scope.furtherFilterBy[$scope.simplifyTitle(column.title)] = "";
                query();
            });
        };

        var hasSinglePredicate = (c) => c.predicate === undefined || c.predicate === null || c.predicate.trim().indexOf(' ') === -1;

        var setFilterColumns = function(userFilterColumns, inactiveFilterColumns) {
            // exclude columns which have multiple predicates
            angular.extend(filterColumns, {
                userFilterColumns: userFilterColumns.filter(hasSinglePredicate),
                inactiveFilterColumns: inactiveFilterColumns.filter(hasSinglePredicate)
            });
        }

        $scope.addRowFilter = function ($index, row) {
            // When removing the last row for a page, and the page number is the last
            if ($scope.page.content.length == 1 && $scope.page.number > 0 && $scope.page.number == $scope.page.totalPages) {
                sessionStorage.setItem("list-page-number", --$scope.page.number);
            }

            $scope.page.content.splice($index, 1);

            var value = row.id.toString();
            var gloss = "Submission #" + row.id;
            $scope.activeFilters.addFilter(rowFilterTitle, value, gloss, true);
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
                if (!data.meta || !data.meta.status || data.meta.status !== "ERROR") {
                    saveAs(new Blob([data], {
                        type: packager.mimeType
                    }), packager.name + '.' + packager.fileExtension);
                }
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
                var path = col.valuePath;
                var curr = member;
                for (var p = 1; p < path.length; p++) {
                    curr = curr[path[p]];
                }
                value += value.length > 0 ? ", " + curr : curr;
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

        // Warning: setting ngModelOptions: { timezone: 'utc' } can cause the off by 1 day problem.
        var datepickerOptions = {
            datepickerMode: 'day',
            formatDay: 'dd',
            formatMonth: 'MMMM',
            formatYear: 'yyyy',
            formatDayHeader: 'EEE',
            formatDayTitle: 'MMMM yyyy',
            formatMonthTitle: 'yyyy',
            maxDate: null,
            maxMode: 'month',
            minDate: null,
            minMode: 'month',
            monthColumns: 3,
            ngModelOptions: {},
            shortcutPropagation: false,
            showWeeks: true,
            yearColumns: 5,
            yearRows: 4,
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
            "submissionTypeList": submissionTypeList,
            "assignableUsers": assignableUsers,
            "graduationSemesters": graduationSemesters,
            "withoutActiveFilter": withoutActiveFilter,
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
            "graduationSemesters": graduationSemesters,
            "withoutActiveFilter": withoutActiveFilter,
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
            if (row.columnValues !== undefined) {
                value = row.columnValues[col.id];
            } else {
                console.error('row.columnValues is undefined')
            }

            // check value path to get values
            if (value === undefined) {
                for (var i in col.valuePath) {
                    if (typeof col.valuePath[i] !== 'function') {
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
            }

            return value;
        };

        $scope.displaySubmissionProperty = function (row, col) {
            if (angular.isDefined(col) && col !== null) {
                return $filter('displayFieldValue')($scope.getSubmissionProperty(row, col), col.inputType);
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

        $scope.findDateColumn = function (match) {
            if (angular.isDefined(appConfig.dateColumns) && angular.isDefined(match)) {
                for (var i = 0; i < appConfig.dateColumns.length; i++) {
                    if (appConfig.dateColumns[i].how === 'exact') {
                        if (match === appConfig.dateColumns[i].name) {
                            return appConfig.dateColumns[i];
                        }
                    } else if (appConfig.dateColumns[i].how === 'start') {
                        if (match.startsWith(appConfig.dateColumns[i].name)) {
                            return appConfig.dateColumns[i];
                        }
                    }
                }
            }

            return null;
        };

        $scope.getUserById = function (userId) {
            return UserRepo.findById(userId);
        };

        $scope.removeFilterValue = function (criterionName, filterValue) {
            // Reset filter except for when row filters are in use.
            if (criterionName !== rowFilterTitle) {
                $scope.resetPagination();
            }

            $scope.activeFilters.removeFilter(criterionName, filterValue);
        };

        $scope.clearFilters = function () {
            $scope.resetPagination();
            $scope.activeFilters.clearFilters();
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
            $scope.activeFilters.set(filter);
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

            setFilterColumns(userFilterColumns, inactiveFilterColumns);

            updateChange(false);
        };

        $scope.removeSaveFilter = function (filter) {
            $scope.resetPagination();

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
            update(true);
            $scope.closeModal();
        };

        $scope.resetColumnsToDefault = function () {
            ManagerSubmissionListColumnRepo.resetSubmissionListColumns().then(function () {
                $scope.resetColumns();
            });
        };

        $scope.saveColumns = function () {
            ManagerSubmissionListColumnRepo.updateSubmissionListColumns($scope.userColumns, $scope.page.count).then(function (res) {
                var results = angular.fromJson(res.body);
                if (results.meta.status === 'SUCCESS') {
                    $scope.resetPagination();
                    sessionStorage.setItem("list-page-size", $scope.page.count);
                    $scope.resetColumns();
                }
            });
        };

        $scope.saveUserFilters = function () {
            ManagerFilterColumnRepo.updateFilterColumns(filterColumns.userFilterColumns).then(function (res) {
                var results = angular.fromJson(res.body);
                if (results.meta.status === 'SUCCESS') {
                    update(false);
                    $scope.closeModal();
                }
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

            ManagerSubmissionListColumnRepo.updateSubmissionListColumnSort($scope.userColumns).then(function () {
                query();
            });

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

        $scope.viewSubmission = function (event, submission) {
            const url = $location.absUrl();
            if (event.ctrlKey || event.metaKey) {
                $window.open(url.replace('/list', '/view/' + submission.id));
                event.stopPropagation();
            } else {
                $location.path('/admin/view/' + submission.id);
            };
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
                "removeSaveFilter": $scope.removeSaveFilter,
                "getUserById": $scope.getUserById,
                "userSettings": userSettings
            },
            $scope.furtherFilterBy,
            $scope.advancedfeaturesBox
        ]);

        $scope.resetPagination = function() {
            $scope.pageNumber = 0;
            $scope.page.number = 1;

            sessionStorage.setItem("list-page-number", 1);
        };

        var listenActive = angular.copy(apiMapping.NamedSearchFilterGroup.listenActive);
        var listenSaved = angular.copy(apiMapping.NamedSearchFilterGroup.listenSaved);
        var listenPublic = angular.copy(apiMapping.NamedSearchFilterGroup.listenSaved);
        listenActive.controller = 'active-filters/user/' + userSettings.id;
        listenSaved.controller = 'saved-filters/user/' + userSettings.id;
        listenPublic.controller = 'saved-filters/public';

        WsApi.listen(listenActive).then(null, null, function (res) {
            if (res !== undefined && res.body) {
                var apiRes = angular.fromJson(res.body);

                if (apiRes.payload && apiRes.payload.FilterAction) {
                    var keys = Object.keys(apiRes.payload);
                    var regex = /^NamedSearchFilterGroup\b/;
                    for (var i = 0; i < keys.length; i++) {
                        if (keys[i].match(regex)) {
                            if (apiRes.payload.FilterAction == 'CLEAR' || apiRes.payload.FilterAction == 'SET') {
                                $scope.resetPagination();
                            }

                            angular.extend($scope.activeFilters, apiRes.payload[keys[i]]);
                            query();
                            break;
                        }
                    }
                }
            }
        });

        WsApi.listen(listenSaved).then(null, null, function (res) {
            if (res !== undefined && res.body) {
                var apiRes = angular.fromJson(res.body);

                if (apiRes.payload) {
                    var keys = Object.keys(apiRes.payload);
                    var regex = /^PersistentBag\b/;
                    for (var i = 0; i < keys.length; i++) {
                        if (keys[i].match(regex)) {
                            savedFilters.length = 0;
                            for (var j = 0; j < apiRes.payload[keys[i]].length; j++) {
                                savedFilters.push(new SavedFilter(apiRes.payload[keys[i]][j]));
                            }
                            break;
                        }
                    }
                }
            }
        });

        WsApi.listen(listenPublic).then(null, null, function (res) {
            if (res !== undefined && res.body) {
                var apiRes = angular.fromJson(res.body);

                if (apiRes.payload && apiRes.payload.FilterAction) {
                    if (apiRes.payload.FilterAction == 'REMOVE') {
                        SavedFilterRepo.reset();
                    } else {
                        var keys = Object.keys(apiRes.payload);
                        var regex = /^NamedSearchFilterGroup\b/;
                        for (var i = 0; i < keys.length; i++) {
                            if (keys[i].match(regex)) {
                                if (apiRes.payload.FilterAction == 'SAVE') {
                                    // If the user is the same, then the filter list should already be up to date.
                                    if (apiRes.payload[keys[i]].user !== userSettings.id) {
                                        SavedFilterRepo.reset();
                                    }
                                }

                                break;
                            }
                        }
                    }
                }
            }
        });

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
                if (item === undefined || item == null) return undefined;
                return item[prop];
            });
        }
        return input.filter(function byExclude(item) {
            if (item === undefined || item == null) return undefined;
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
