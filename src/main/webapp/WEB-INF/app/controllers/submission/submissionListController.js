vireo.controller("SubmissionListController", function ($controller, $filter, $q, $scope, NgTableParams, SubmissionRepo, SubmissionListColumnRepo, ManagerSubmissionListColumnRepo, WsApi) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	$scope.page = {};

	$scope.pageNumber = 0;

	$scope.pageSize = 2;

	$scope.columns = [];

	$scope.userColumns = [];

	$scope.resultsPerPageOptions = [10, 20, 40, 60, 100, 200, 400, 500, 1000];

	$scope.resultsPerPage = 100;

	$scope.itemMoved = false;

	var update = function() {

		ManagerSubmissionListColumnRepo.reset();

		SubmissionListColumnRepo.reset();

		$q.all([SubmissionListColumnRepo.ready(), ManagerSubmissionListColumnRepo.ready()]).then(function(data) {
			
			$scope.userColumns = ManagerSubmissionListColumnRepo.getAll();
		
			$scope.columns = $filter('exclude')(SubmissionListColumnRepo.getAll(), $scope.userColumns, 'title');

			SubmissionRepo.query($scope.userColumns, $scope.pageNumber, $scope.pageSize).then(function(data) {

				angular.extend($scope.page, angular.fromJson(data.body).payload.PageImpl);

				console.log($scope.page);

				$scope.tableParams = new NgTableParams({ }, 
				{
					counts: [],
					filterDelay: 0, 
					dataset: $scope.page.content
				});

				//$scope.tableParams.reload();
			});
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

	$scope.selectPage = function(i) {
		$scope.pageNumber = i;
		update();
	};

	$scope.resetColumns = function() {
		$q.all(listenForAllSubmissionColumns(), listenForManagersSubmissionColumns()).then(function() {

			update();

			$scope.closeModal();

			$scope.itemMoved = false;
		});		
	};

	$scope.resetColumnsToDefault = function() {
		ManagerSubmissionListColumnRepo.resetSubmissionListColumns().then(function() {
			$scope.resetColumns();
		});
	};

	$scope.saveColumns = function() {
		ManagerSubmissionListColumnRepo.updateSubmissionListColumns().then(function() {
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

vireo.filter('range', function() {
  	return function(val, range) {
    	range = parseInt(range);
    	for (var i = 0; i < range; i++) {
      		val.push(i);
    	}
    	return val;
  	};
});
