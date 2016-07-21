vireo.controller("SubmissionViewController", function ($controller, $filter, $q, $scope, NgTableParams, SubmissionRepo, SubmissionViewColumnRepo, ManagerSubmissionViewColumnRepo, WsApi) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	$scope.page = 0;

	$scope.pageSize = 10;

	$scope.columns = [];

	$scope.userColumns = [];

	$scope.resultsPerPageOptions = [10, 20, 40, 60, 100, 200, 400, 500, 1000];

	$scope.resultsPerPage = 100;

	$scope.itemMoved = false;

	var update = function() {

		ManagerSubmissionViewColumnRepo.reset();

		SubmissionViewColumnRepo.reset();

		$q.all([SubmissionViewColumnRepo.ready(), ManagerSubmissionViewColumnRepo.ready()]).then(function(data) {
			
			$scope.userColumns = ManagerSubmissionViewColumnRepo.getAll();
		
			$scope.columns = $filter('exclude')(SubmissionViewColumnRepo.getAll(), $scope.userColumns, 'title');

			SubmissionRepo.query($scope.userColumns, $scope.page, $scope.pageSize).then(function(data) {

				$scope.submissions = angular.fromJson(data.body).payload.PageImpl.content

				$scope.tableParams = new NgTableParams({ }, 
				{
					filterDelay: 0, 
					dataset: $scope.submissions
				});

				$scope.tableParams.reload();

				console.log($scope.userColumns)
			});
		});
		
	};

	SubmissionRepo.listen(function() {
		update();
	});

	update();

	var listenForManagersSubmissionColumns = function() {
		return $q(function(resolve) {
			ManagerSubmissionViewColumnRepo.listen(function() {
				resolve();
			});
		});
	};

	var listenForAllSubmissionColumns = function() {
		return $q(function(resolve) {
			SubmissionViewColumnRepo.listen(function() {
				resolve();
			});
		});
	};

	$scope.resetColumns = function() {
		$q.all(listenForAllSubmissionColumns(), listenForManagersSubmissionColumns()).then(function() {

			update();

			$scope.closeModal();

			$scope.itemMoved = false;
		});		
	};

	$scope.resetColumnsToDefault = function() {
		ManagerSubmissionViewColumnRepo.resetSubmissionViewColumns().then(function() {
			$scope.resetColumns();
		});
	};

	$scope.saveColumns = function() {
		ManagerSubmissionViewColumnRepo.updateSubmissionViewColumns().then(function() {
			$scope.resetColumns();
		});
	};

	$scope.getSubmissionProperty = function(row, col) {
		var value;
		for(var i in col.path) {
			value = (value === undefined) ? row[col.path[i]] : value[col.path[i]];
		}
		return value;
	};

	setTimeout(function() {$scope.sortBy($scope.userColumns[3])}, 10000);

	$scope.sortBy = function(sortColumn) {

		angular.forEach($scope.userColumns, function(userColumn) {
			if(userColumn.sortOrder !== undefined && userColumn.sortOrder > 0) {
				
				if(userColumn.sort != "NONE") {
					userColumn.sortOrder++;
					if(userColumn.sortOrder > $scope.userColumns.length - 1) {
						userColumn.sortOrder = 0;
						userColumn.sort = "NONE";
					}
				}
				else {
					userColumn.sortOrder = 0;
				}
				
			}
			if(userColumn == sortColumn) {
				userColumn.sort = ($scope.userColumns.sort == "NONE") ? "ASC" : ($scope.userColumns.sort == "ASC") ? "DESC" : "ASC";
				userColumn.sortOrder = 1;
			}
		});

		$scope.saveColumns();		
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
			$scope.itemMoved = true;
		},
		orderChanged: function (event) {
			$scope.itemMoved = true;
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
