vireo.controller("SubmissionListController", function ($controller, $filter, $q, $scope, NgTableParams, SubmissionRepo, SubmissionStateRepo, SubmissionListColumnRepo, ManagerSubmissionListColumnRepo, UserRepo, WsApi, SidebarService) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	$scope.page = {};

	$scope.pageSize = 10;

	$scope.pageNumber = 0;

	$scope.pageSizeOptions = [5, 10, 20, 40, 60, 100, 200, 400, 500];

	$scope.columns = [];

	$scope.userColumns = [];

	$scope.change = false;

	SubmissionStateRepo.ready().then(function() {
		$scope.advancedfeaturesBox.newStatus = submissionStates[0];	
	});

	var submissionStates = SubmissionStateRepo.getAll();

	var findFirstAssignable = function() {
		var firstAssignable;
		for(var i in allUsers) {
			if(allUsers[i].role === "ADMINISTRATOR" || allUsers[i].role === "MANAGER") {
				firstAssignable = allUsers[i];
				break;	
			}	
		}
		return firstAssignable;
	};

	UserRepo.ready().then(function() {
		$scope.advancedfeaturesBox.assignee = findFirstAssignable();
	});

	var allUsers = UserRepo.getAll();

	var resetBatchUpdateStatus = function() {
		$scope.advancedfeaturesBox.assignee = findFirstAssignable();
		$scope.closeModal();
	};

	var batchUpdateStatus = function(newStatus) {
		SubmissionRepo.batchUpdateStatus(newStatus).then(function() {
			resetBatchUpdateStatus();
			query();	
		});
	};

	var resetBatchAssignTo = function() {
		$scope.advancedfeaturesBox.assignee = allUsers[0];	
		$scope.closeModal();
	};

	var batchAssignTo = function(assignee) {
		SubmissionRepo.batchAssignTo(assignee).then(function() {
			resetBatchUpdateStatus();
			query();	
		});
	};

	var assignable = function(user) {
		return user.role === "MANAGER" || user.role === "ADMINISTRATOR";
	};

	var resetBatchCommentEmail = function() {
		$scope.closeModal();
	};

	var batchCommentEmail = function() {
		console.log("batchCommentEmail");
	};

	var resetBatchDownloadExport = function() {
		$scope.closeModal();
	};

	var batchDownloadExport = function() {
		console.log("batchDownloadExport");
	};

	$scope.advancedfeaturesBox = {
        "title": "Advanced Features:",
        "viewUrl": "views/sideboxes/advancedFeatures.html",
        "resetBatchUpdateStatus": resetBatchUpdateStatus,
        "batchUpdateStatus": batchUpdateStatus,
        "submissionStates": submissionStates,
        "allUsers": allUsers,
        "resetBatchAssignTo": resetBatchAssignTo,
        "assignable": assignable,
        "batchAssignTo": batchAssignTo,
        "resetBatchCommentEmail": resetBatchCommentEmail,
        "batchCommentEmail": batchCommentEmail,
        "resetBatchDownloadExport": resetBatchDownloadExport,
        "batchDownloadExport": batchDownloadExport
    };

	var query = function() {
		SubmissionRepo.query($scope.userColumns, $scope.pageNumber, $scope.pageSize).then(function(data) {

			angular.extend($scope.page, angular.fromJson(data.body).payload.PageImpl);

			$scope.tableParams = new NgTableParams({
				count: $scope.page.totalElements
			},
			{
				counts: [],
				filterDelay: 0, 
				dataset: $scope.page.content
			});
		});
	};

	var update = function() {

		SubmissionListColumnRepo.reset();
		ManagerSubmissionListColumnRepo.reset();

		$q.all([SubmissionListColumnRepo.ready(), ManagerSubmissionListColumnRepo.ready()]).then(function(data) {

			ManagerSubmissionListColumnRepo.submissionListPageSize().then(function(data) {
				
				$scope.pageSize = angular.fromJson(data.body).payload.Integer;

				$scope.userColumns = ManagerSubmissionListColumnRepo.getAll();

				$scope.columns = $filter('exclude')(SubmissionListColumnRepo.getAll(), $scope.userColumns, 'title');

				query();

				$scope.change = false;
				$scope.closeModal();
			});

			SidebarService.addBox($scope.advancedfeaturesBox);	

		});		
	};

	SubmissionRepo.listen(function() {
		update();
	});

	update();

	var listenForManagersSubmissionColumns = function() {
		return $q(function(resolve) {
			ManagerSubmissionListColumnRepo.listen(function() {
				resolve();
			});
		});
	};

	var listenForAllSubmissionColumns = function() {
		return $q(function(resolve) {
			SubmissionListColumnRepo.listen(function() {
				resolve();
			});
		});
	};

	$scope.updatePagination = function() {
		$scope.pageNumber = 0;
		$scope.change = true;
	};

	$scope.selectPage = function(i) {
		$scope.pageNumber = i;
		query();
	};

	$scope.resetColumns = function() {
		$q.all(listenForAllSubmissionColumns(), listenForManagersSubmissionColumns()).then(function() {
			update();
		});		
	};

	$scope.resetColumnsToDefault = function() {
		ManagerSubmissionListColumnRepo.resetSubmissionListColumns().then(function() {
			$scope.resetColumns();
		});
	};

	$scope.saveColumns = function() {
		ManagerSubmissionListColumnRepo.updateSubmissionListColumns($scope.pageSize).then(function() {
			$scope.resetColumns();
		});
	};

	var getValueFromArray = function(array, path, col) {
		for(var j in array) {
			if(array[j].fieldPredicate.value == col.predicate) {
				return array[j].value;
			}
		}
	};

	$scope.getSubmissionProperty = function(row, col) {
		var value;

		for(var i in col.valuePath) {

			if(value === undefined) {
				value = row[col.valuePath[i]];
			}
			else {
				if(value instanceof Array) {
					return getValueFromArray(value, col.predicatePath, col);
				}
				else {
					if(value !== null) {
						value = value[col.valuePath[i]];
					}
				}
			}
		}
		return value;
	};

	var previousSortColumnToggled;

	$scope.sortBy = function(sortColumn) {

		switch(sortColumn.sort) {
			case "ASC": {
				sortColumn.sort = "NONE";
				sortColumn.sortOrder = 0;
			} break;
			case "DESC": {
				sortColumn.sort = "ASC";
				sortColumn.sortOrder = 1;
			} break;
			case "NONE": {
				sortColumn.sort = "DESC";
				sortColumn.sortOrder = 1;
			} break;
			default: break;
		}

		if(previousSortColumnToggled === undefined || sortColumn.title != previousSortColumnToggled.title) {
			angular.forEach($scope.userColumns, function(userColumn) {
				if(sortColumn.title != userColumn.title) {
					if(userColumn.sort != "NONE") {
						userColumn.sortOrder++;
					}
					else {
						userColumn.sortOrder = 0;
					}
				}
			});
		}
		
		previousSortColumnToggled = sortColumn;

		query();
	};

	$scope.columnOptions = {
		accept: function (sourceItemHandleScope, destSortableScope, destItemScope) {
			return true;
		},
		itemMoved: function (event) {
			if(event.source.sortableScope.$id < event.dest.sortableScope.$id) {
				event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'previouslyDisplayed' : null;	
			}
			else {
				event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'pervisoulyDisabled' : null;
			}
			$scope.change = true;
		},
		orderChanged: function (event) {
			$scope.change = true;
		},
		containment: '#column-modal',
		additionalPlaceholderClass: 'column-placeholder'
	};

});

vireo.filter('exclude', function() {
	return function(input, exclude, prop) {
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

vireo.filter('range', function() {
  	return function(val, range) {
    	range = parseInt(range);
    	for (var i = 0; i < range; i++) {
      		val.push(i);
    	}
    	return val;
  	};
});
