vireo.controller("SubmissionListController", function (NgTableParams, $controller, $filter, $location, $q, $scope, CustomActionDefinitionRepo, DepositLocationRepo, DocumentTypeRepo, EmailTemplateRepo, EmbargoRepo, ManagerFilterColumnRepo, ManagerSubmissionListColumnRepo, NamedSearchFilterGroup, OrganizationRepo, OrganizationCategoryRepo, PackagerRepo, SavedFilterRepo, SidebarService, Submission, SubmissionListColumnRepo, SubmissionRepo, SubmissionStatusRepo, UserRepo, UserSettings) {

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

    

    var userSettingsUnfetched = new UserSettings();
    userSettingsUnfetched.fetch();


    var packagers = PackagerRepo.getAll();

    var ready = $q.all([
        CustomActionDefinitionRepo.getAll(),
        DepositLocationRepo.getAll(),
        DocumentTypeRepo.getAll(),
        EmbargoRepo.getAll(),
        EmailTemplateRepo.getAll(),
        OrganizationCategoryRepo.getAll(),
        OrganizationRepo.getAll(),
        PackagerRepo.getAll(),
        SubmissionStatusRepo.getAll(),
        UserRepo.getAll(),
        userSettingsUnfetched
    ]);

    ready.then(function (resolved) {
        var customActionDefinitions = resolved[0];
        var depositLocations = resolved[1];
        var documentTypes = resolved[2];
        var embargos = resolved[3];
        var emailTemplates = resolved[4];
        var organizationCategories = resolved[5];
        var organizations = resolved[6];
        var packagers = resolved[7];
        var submissionStatuses = resolved[8];
        var allUsers = resolved[9];
        var userSettings = resolved[10];

        var batchCommentEmail = {};

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

        var addBatchCommentEmail = function () {
            console.log("batchCommentEmail");
        };

        $scope.getFilterColumns = function () {
            return $scope.filterColumns;
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
    
            $scope.activeFilters.addFilter(column.title, dateValue, dateGloss).then(function () {
                $scope.furtherFilterBy[column.title.split(" ").join("")] = "";
                query();
            });
    
        };

        var assignable = function (user) {
            return user.role === "ROLE_MANAGER" || user.role === "ROLE_ADMIN";
        };

        var findFirstAssignable = function () {
            var firstAssignable;
            for (var i in allUsers) {
                if (allUsers[i].role === "ROLE_ADMIN" || allUsers[i].role === "ROLE_MANAGER") {
                    firstAssignable = allUsers[i];
                    break;
                }
            }
            return firstAssignable;
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
            $scope.advancedfeaturesBox.assignee = allUsers[0];
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

        $scope.furtherFilterBy = {
            "title": "Further Filter By:",
            "viewUrl": "views/sideboxes/furtherFilterBy/furtherFilterBy.html",
            "getFilterColumns": $scope.getFilterColumns,
            "addFilter": addFilter,
            "addExactMatchFilter": addExactMatchFilter,
            "addDateFilter": addDateFilter,
            "submissionStatuses": submissionStatuses,
            "customActionDefinitions": customActionDefinitions,
            "organizations": organizations,
            "organizationCategories": organizationCategories,
            "documentTypes": documentTypes,
            "embargos": embargos,
            "allUsers": allUsers,
            "assignable": assignable,
            "defaultLimit": 3
        };

        $scope.advancedfeaturesBox = {
            "title": "Advanced Features:",
            "processing": false,
            "depositLocations": depositLocations,
            "viewUrl": "views/sideboxes/advancedFeatures.html",
            "resetBatchUpdateStatus": resetBatchUpdateStatus,
            "batchUpdateStatus": batchUpdateStatus,
            "submissionStatuses": submissionStatuses,
            "allUsers": allUsers,
            "resetBatchAssignTo": resetBatchAssignTo,
            "assignable": assignable,
            "batchAssignTo": batchAssignTo,
            "batchPublish": batchPublish,
            "resetBatchCommentEmailModal": resetBatchCommentEmailModal,
            "batchCommentEmail": batchCommentEmail,
            "resetBatchDownloadExport": resetBatchDownloadExport,
            "batchDownloadExport": batchDownloadExport,
            "packagers": packagers
        };

        $scope.advancedfeaturesBox.newStatus = submissionStatuses[0];
        $scope.advancedfeaturesBox.assignee = findFirstAssignable();

        update();
    });

    $scope.filterChange = false;

    $scope.activeFilters = new NamedSearchFilterGroup();

    $scope.savedFilters = SavedFilterRepo.getAll();

    // This is for piping the user/all columns through to the customizeFilters modal
    $scope.filterColumns = {};

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
        for (var i = $scope.filterColumns.userFilterColumns.length - 1; i >= 0; i--) {
            if ($scope.filterColumns.userFilterColumns[i].status === 'previouslyDisabled') {
                delete $scope.filterColumns.userFilterColumns[i].status;
                filtersPreviouslyDisabled.push($scope.filterColumns.userFilterColumns[i]);
                $scope.filterColumns.userFilterColumns.splice(i, 1);
            }
        }
        var filtersPreviouslyDisplayed = [];
        for (var j = $scope.filterColumns.inactiveFilterColumns.length - 1; j >= 0; j--) {
            if ($scope.filterColumns.inactiveFilterColumns[j].status === 'previouslyDisplayed') {
                delete $scope.filterColumns.inactiveFilterColumns[j].status;
                filtersPreviouslyDisplayed.push($scope.filterColumns.inactiveFilterColumns[j]);
                $scope.filterColumns.inactiveFilterColumns.splice(j, 1);
            }
        }

        $scope.filterColumns.userFilterColumns = filtersPreviouslyDisplayed.concat($scope.filterColumns.userFilterColumns);

        $scope.filterColumns.inactiveFilterColumns = filtersPreviouslyDisabled.concat($scope.filterColumns.inactiveFilterColumns);
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

    var query = function () {

        $scope.tableParams = new NgTableParams({
            page: $scope.page.number,
            count: $scope.page.count
        }, {
            counts: $scope.page.options,
            total: $scope.page.totalElements,
            filterDelay: 0,
            getData: function (params) {
                return SubmissionRepo.query($scope.userColumns, params.page() > 0 ? params.page() - 1 : params.page(), params.count()).then(function (data) {
                    angular.extend($scope.page, angular.fromJson(data.body).payload.PageImpl);
                    SubmissionRepo.addAll($scope.page.content);
                    params.total($scope.page.totalElements);
                    $scope.page.count = params.count();
                    return $scope.page.content;
                });
            }
        });

    };

    var update = function () {

        SubmissionListColumnRepo.reset();
        ManagerSubmissionListColumnRepo.reset();

        $q.all([SubmissionListColumnRepo.ready(), ManagerSubmissionListColumnRepo.ready(), ManagerFilterColumnRepo.ready()]).then(function () {

            ManagerSubmissionListColumnRepo.submissionListPageSize().then(function (data) {

                $scope.userColumns = ManagerSubmissionListColumnRepo.getAll();

                $scope.excludedColumns = [];

                angular.copy($scope.userColumns, $scope.excludedColumns);

                $scope.excludedColumns.push(SubmissionListColumnRepo.findByTitle('Search Box'));

                $scope.columns = $filter('orderBy')($filter('exclude')(SubmissionListColumnRepo.getAll(), $scope.excludedColumns, 'title'), 'title');

                $scope.filterColumns.userFilterColumns = ManagerFilterColumnRepo.getAll();

                $scope.filterColumns.inactiveFilterColumns = $filter('orderBy')($filter('exclude')(SubmissionListColumnRepo.getAll(), $scope.filterColumns.userFilterColumns, 'title'), 'title');

                query();

                $scope.change = false;
                $scope.closeModal();
            });

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
                    "savedFilters": $scope.savedFilters,
                    "getFilterColumns": $scope.getFilterColumns,
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

        });
    };

    SubmissionRepo.listen(function () {
        update();
    });  

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

    $scope.updatePagination = function () {
        $scope.pageNumber = 0;
        $scope.change = true;
    };

    $scope.selectPage = function (i) {
        $scope.pageNumber = i;
        query();
    };

    $scope.resetColumns = function () {
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
        ManagerSubmissionListColumnRepo.updateSubmissionListColumns($scope.page.count).then(function () {
            $scope.resetColumns();
        });
    };

    $scope.saveUserFilters = function () {
        ManagerFilterColumnRepo.updateFilterColumns($scope.filterColumns.userFilterColumns).then(function () {
            for (var i in $scope.filterColumns.userFilterColumns) {
                delete $scope.filterColumns.userFilterColumns[i].status;
            }
            update();
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
