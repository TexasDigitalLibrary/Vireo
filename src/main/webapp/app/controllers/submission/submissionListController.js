vireo.controller("SubmissionListController", function (NgTableParams, $controller, $filter, $location, $q, $scope, CustomActionDefinitionRepo, DepositLocationRepo, DocumentTypeRepo, EmailTemplateRepo, EmbargoRepo, ManagerFilterColumnRepo, ManagerSubmissionListColumnRepo, NamedSearchFilterGroup, OrganizationRepo, OrganizationCategoryRepo, PackagerRepo, SavedFilterRepo, SidebarService, Submission, SubmissionListColumnRepo, SubmissionRepo, SubmissionStatusRepo, UserRepo, UserSettings, WsApi) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.page = {
        number: 1,
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
            500
        ]
    };

    $scope.columns = [];

    $scope.userColumns = [];

    $scope.change = false;

    $scope.filterChange = false;

    $scope.activeFilters = new NamedSearchFilterGroup();

    var ready = $q.all([SubmissionListColumnRepo.ready(), ManagerSubmissionListColumnRepo.ready()]);
    
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
                    return SubmissionRepo.query(params.page() > 0 ? params.page() - 1 : params.page(), params.count()).then(function (response) {
                        angular.extend($scope.page, angular.fromJson(response.body).payload.ApiPage);
                        // NOTE: this causes way to many subscriptions!!!
                        // SubmissionRepo.addAll($scope.page.content);
                        params.total($scope.page.totalElements);
                        $scope.page.count = params.count();
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
                        $scope.page.count = apiRes.payload.Integer;
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

                    $scope.change = false;

                    $scope.closeModal();
                });
            });
        };

        update();

        var assignableUsers = UserRepo.getAssignableUsers();

        var savedFilters = SavedFilterRepo.getAll();
        var emailTemplates = EmailTemplateRepo.getAll();
        var organizations = OrganizationRepo.getAll();
        var organizationCategories = OrganizationCategoryRepo.getAll();
        var submissionStatuses = SubmissionStatusRepo.getAll();
        var documentTypes = DocumentTypeRepo.getAll();
        var customActionDefinitions = CustomActionDefinitionRepo.getAll();
        var depositLocations = DepositLocationRepo.getAll();
        var embargos = EmbargoRepo.getAll();
        var packagers = PackagerRepo.getAll();

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
            batchCommentEmail.commentVisiblity = userSettings.notes_mark_comment_as_private_by_default ? "private" : "public";
            batchCommentEmail.recipientEmail = userSettings.notes_email_student_by_default === "true" ? $scope.submission.submitter.email : "";
            batchCommentEmail.ccRecipientEmail = userSettings.notes_cc_student_advisor_by_default === "true" ? $scope.submission.getContactEmails().join(",") : "";
            batchCommentEmail.sendEmailToRecipient = (userSettings.notes_email_student_by_default === "true") || (userSettings.notes_cc_student_advisor_by_default === "true");
            batchCommentEmail.sendEmailToCCRecipient = userSettings.notes_cc_student_advisor_by_default === "true";
            batchCommentEmail.subject = "";
            batchCommentEmail.message = "";
            batchCommentEmail.actionLogCurrentLimit = $scope.actionLogLimit;
            batchCommentEmail.selectedTemplate = emailTemplates[0];
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
                            for (var k in currentFieldProfile.controlledVocabularies) {
                                var cv = currentFieldProfile.controlledVocabularies[k];
                                for (var l in cv.dictionary) {
                                    var dictionary = cv.dictionary[l];
                                    if (words.indexOf(cv.dictionary[l].name) == -1) {
                                        words.push(cv.dictionary[l].name);
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
            $scope.activeFilters.addFilter(column.title, $scope.furtherFilterBy[column.title.split(" ").join("")], gloss, column.exactMatch).then(function () {
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
            var dateGloss = $filter('date')($scope.furtherFilterBy[column.title.split(" ").join("")].d1, "MM/dd/yyyy");
            dateValue += $scope.furtherFilterBy[column.title.split(" ").join("")].d2 ? "|" + $scope.furtherFilterBy[column.title.split(" ").join("")].d2.toISOString() : "";
            dateGloss += $scope.furtherFilterBy[column.title.split(" ").join("")].d2 ? " to " + $filter('date')($scope.furtherFilterBy[column.title.split(" ").join("")].d2, "MM/dd/yyyy") : "";
            $scope.activeFilters.addFilter(column.title, dateValue, dateGloss, false).then(function () {
                $scope.furtherFilterBy[column.title.split(" ").join("")] = "";
                query();
            });
        };

        var resetBatchUpdateStatus = function () {
            $scope.advancedfeaturesBox.processing = false;
            $scope.advancedfeaturesBox.assignee = findFirstAssignable();
            $scope.closeModal();
        };

        var batchUpdateStatus = function (newStatus) {
            $scope.advancedfeaturesBox.processing = true;
            SubmissionRepo.batchUpdateStatus(newStatus).then(function () {
                resetBatchUpdateStatus();
                query();
            });
        };

        var resetBatchAssignTo = function () {
            $scope.advancedfeaturesBox.assignee = assignableUsers[0];
            $scope.closeModal();
        };

        var batchAssignTo = function (assignee) {
            $scope.advancedfeaturesBox.processing = true;
            SubmissionRepo.batchAssignTo(assignee).then(function () {
                resetBatchUpdateStatus();
                query();
            });
        };

        var batchPublish = function (newStatus) {
            $scope.advancedfeaturesBox.processing = true;
            SubmissionRepo.batchPublish($scope.advancedfeaturesBox.depositLocation).then(function () {
                resetBatchUpdateStatus();
                query();
            });
        };

        var resetBatchDownloadExport = function () {
            $scope.closeModal();
        };

        var batchDownloadExport = function (packager) {
            $scope.advancedfeaturesBox.exporting = true;
            SubmissionRepo.batchExport(packager).then(function (data) {
                saveAs(new Blob([data], {
                    type: 'application/zip'
                }), packager.name + '.zip');
                resetBatchUpdateStatus();
            });
        };

        var getValueFromArray = function (array, col) {
            var value = "";
            for (var j in array) {
                var member = array[j];
                if (member.fieldPredicate !== undefined) {
                    if (member.fieldPredicate.value == col.predicate) {
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

        var listenForManagersSubmissionColumns = function () {
            return $q(function (resolve) {
                ManagerSubmissionListColumnRepo.listen(function () {
                    resolve();
                });
            });
        };

        var listenForAllSubmissionColumns = function () {
            return $q(function (resolve) {
                SubmissionListColumnRepo.listen(function () {
                    resolve();
                });
            });
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
            "getTypeAheadByPredicateName": getTypeAheadByPredicateName
        };

        $scope.advancedfeaturesBox = {
            "title": "Advanced Features:",
            "processing": false,
            "depositLocations": depositLocations,
            "viewUrl": "views/sideboxes/advancedFeatures.html",
            "resetBatchUpdateStatus": resetBatchUpdateStatus,
            "batchUpdateStatus": batchUpdateStatus,
            "submissionStatuses": submissionStatuses,
            "newStatus": submissionStatuses[0],
            "assignableUsers": assignableUsers,
            "resetBatchAssignTo": resetBatchAssignTo,
            "batchAssignTo": batchAssignTo,
            "batchPublish": batchPublish,
            "resetBatchCommentEmailModal": resetBatchCommentEmailModal,
            "batchCommentEmail": batchCommentEmail,
            "addBatchCommentEmail": addBatchCommentEmail,
            "emailTemplates": emailTemplates,
            "updateTemplate": updateTemplate,
            "resetBatchDownloadExport": resetBatchDownloadExport,
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
                                value = value[col.valuePath[i]];
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
                value = $filter('date')(value, 'MMM dd, yyyy');
            }
            return value;
        };

        $scope.isDateColumn = function (col) {
            return (col.inputType.name == 'INPUT_DATE' || col.inputType.name == 'INPUT_DATETIME');
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
        };

        $scope.removeFilter = function (filter) {
            SavedFilterRepo.delete(filter).then(function () {
                SavedFilterRepo.reset();
            });
        };

        $scope.resetRemoveFilters = function () {
            $scope.closeModal();
        };

        $scope.getFilterColumnOptions = function () {
            return $scope.filterColumnOptions;
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
            $q.all(listenForAllSubmissionColumns(), listenForManagersSubmissionColumns()).then(function () {
                update();
            });
        };

        $scope.resetColumnsToDefault = function () {
            ManagerSubmissionListColumnRepo.resetSubmissionListColumns().then(function () {
                $scope.resetColumns();
            });
        };

        $scope.saveColumns = function () {
            ManagerSubmissionListColumnRepo.updateSubmissionListColumns($scope.userColumns, $scope.page.count).then(function () {
                $scope.resetColumns();
            });
        };

        $scope.saveUserFilters = function () {
            ManagerFilterColumnRepo.updateFilterColumns(filterColumns.userFilterColumns).then(function () {
                for (var i in filterColumns.userFilterColumns) {
                    delete filterColumns.userFilterColumns[i].status;
                }
                update();
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

            previousSortColumnToggled = sortColumn;

            query();
        };

        $scope.columnOptions = {
            accept: function (sourceItemHandleScope, destSortableScope, destItemScope) {
                return true;
            },
            dragStart: function (event) {
                event.source.itemScope.element.css('margin-top', '60px');
            },
            dragEnd: function (event) {
                event.source.itemScope.element.css('margin-top', '');
            },
            itemMoved: function (event) {
                if (event.source.sortableScope.$id < event.dest.sortableScope.$id) {
                    event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'previouslyDisplayed' : null;
                } else {
                    event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'pervisoulyDisabled' : null;
                }
                $scope.change = true;
            },
            orderChanged: function (event) {
                $scope.change = true;
            },
            containment: '.customize-submission-list-columns',
            containerPositioning: 'relative',
            additionalPlaceholderClass: 'column-placeholder'
        };

        $scope.filterColumnOptions = {
            accept: function (sourceItemHandleScope, destSortableScope, destItemScope) {
                return true;
            },
            dragStart: function (event) {
                event.source.itemScope.element.css('margin-top', '60px');
            },
            dragEnd: function (event) {
                event.source.itemScope.element.css('margin-top', '');
            },
            itemMoved: function (event) {
                if (event.source.sortableScope.$id < event.dest.sortableScope.$id) {
                    event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'previouslyDisplayed' : null;
                } else {
                    event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'previouslyDisabled' : null;
                }
                $scope.filterChange = true;
            },
            orderChanged: function (event) {
                $scope.filterChange = true;
            },
            containment: '.customize-filters',
            containerPositioning: 'relative',
            additionalPlaceholderClass: 'column-placeholder'
        };

        $scope.viewSubmission = function (submission) {
            $location.path("/admin/view/" + submission.id + "/" + submission.submissionWorkflowSteps[0].id);
        };

        SidebarService.addBoxes([{
                "title": "Now filtering By:",
                "viewUrl": "views/sideboxes/nowfiltering.html",
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
                "getFilterColumnOptions": $scope.getFilterColumnOptions,
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

        SubmissionRepo.listen(function () {
            update();
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
