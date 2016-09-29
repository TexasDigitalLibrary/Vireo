vireo.controller("SubmissionListController", function ($controller, $filter, $q, $scope, NgTableParams, SubmissionRepo, SubmissionListColumnRepo, ManagerSubmissionListColumnRepo, ManagerFilterColumnRepo, WsApi,SidebarService, NamedSearchFilter, SavedFilterRepo, UserRepo) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	$scope.page = {};

	$scope.pageSize = 10;

	$scope.pageNumber = 0;

	$scope.pageSizeOptions = [5, 10, 20, 40, 60, 100, 200, 400, 500];

	$scope.columns = [];

	$scope.userColumns = [];

	$scope.change = false;

	$scope.filterChange = false;

	$scope.activeFilters = new NamedSearchFilter();

	$scope.savedFilters = SavedFilterRepo.getAll();

	//This is for piping the user/all columns through to the customizeFilters modal
	$scope.filterColumns = {};

	$scope.getUserById = function(userId) {
		return UserRepo.findById(userId);
	};

	$scope.removeFilterValue = function(criterionName, filterValue) {
		$scope.activeFilters.removeFilter(criterionName, filterValue).then(function() {
			query();
		});
	};

	$scope.clearFilters = function() {
		$scope.activeFilters.clearFilters().then(function() {
			query();
		});
	};

	$scope.saveFilter = function() {
		if ($scope.activeFilters.columnsFlag) {
			$scope.activeFilters.savedColumns = $scope.userColumns;
		}
		SavedFilterRepo.create($scope.activeFilters).then(function() {
			$scope.closeModal();
			SavedFilterRepo.reset();
		});

	};

	$scope.applyFilter = function(filter) {
		if (filter.columnsFlag) {
			$scope.userColumns = filter.savedColumns;
		}

		$scope.activeFilters.set(filter).then(function() {
			query();
		});
	};

	$scope.resetSaveFilter = function() {
		$scope.closeModal();
		$scope.activeFilters.refresh();
		//Todo: reset the data in the modal
	};


	$scope.removeFilter = function(filter) {
		SavedFilterRepo.delete(filter).then(function() {
			SavedFilterRepo.reset();
		});
	};

	$scope.resetRemoveFilters = function() {
		$scope.closeModal();
	};

	$scope.getFilterColumns = function() {
		return $scope.filterColumns;
	};

	$scope.getFilterColumnOptions = function() {
		return $scope.filterColumnOptions;
	};

	$scope.getFilterChange = function() {
		return $scope.filterChange;
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

		$q.all([SubmissionListColumnRepo.ready(), ManagerSubmissionListColumnRepo.ready(), ManagerFilterColumnRepo.ready()]).then(function(data) {

			ManagerSubmissionListColumnRepo.submissionListPageSize().then(function(data) {
				
				$scope.pageSize = angular.fromJson(data.body).payload.Integer;

				$scope.userColumns = ManagerSubmissionListColumnRepo.getAll();

				$scope.columns = $filter('exclude')(SubmissionListColumnRepo.getAll(), $scope.userColumns, 'title');

				$scope.filterColumns.userFilterColumns = ManagerFilterColumnRepo.getAll();
				$scope.filterColumns.inactiveFilterColumns = $filter('exclude')(SubmissionListColumnRepo.getAll(), $scope.filterColumns.userFilterColumns, 'title');

				query();

				$scope.change = false;
				$scope.closeModal();
			});

			SidebarService.addBoxes([
			    {
			        "title": "Now filtering By:",
			        "viewUrl": "views/sideboxes/nowfiltering.html",
					"activeFilters": $scope.activeFilters,
					"removeFilterValue": $scope.removeFilterValue
			    },
			    {
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
			    }
			]);

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

	$scope.saveUserFilters = function() {
		ManagerFilterColumnRepo.updateFilterColumns($scope.filterColumns.userFilterColumns).then(function() {
			$scope.closeModal();
			update();			
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
					value = value[col.valuePath[i]];
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

	$scope.filterColumnOptions = {
		accept: function (sourceItemHandleScope, destSortableScope, destItemScope) {
			return true;
		},
		itemMoved: function (event) {
			if(event.source.sortableScope.$id < event.dest.sortableScope.$id) {
				event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'previouslyDisplayed' : null;	
			}
			else {
				event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'prreviouslyDisabled' : null;
			}
			$scope.filterChange = true;
		},
		orderChanged: function (event) {
			$scope.filterChange = true;
		},
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
