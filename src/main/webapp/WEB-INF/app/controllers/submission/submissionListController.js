vireo.controller("SubmissionListController", function ($controller, $filter, $q, $scope, NgTableParams, SubmissionRepo, SubmissionListColumnRepo, ManagerSubmissionListColumnRepo, WsApi) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
  	$scope.page = 0;
  	$scope.pageSize = 10;
  	
  	$scope.columns = [];

  	$scope.userColumns = [];

  	$scope.resultsPerPageOptions = [10, 20, 40, 60, 100, 200, 400, 500, 1000];

  	$scope.resultsPerPage = 100;

  	$scope.itemMoved = false;

  	var updateColumns = function() {
  		
  		$scope.userColumns = ManagerSubmissionListColumnRepo.getAll();
		
		$scope.columns = $filter('exclude')(SubmissionListColumnRepo.getAll(), $scope.userColumns, 'title');
  		
  		SubmissionRepo.query($scope.userColumns, $scope.page, $scope.pageSize).then(function(data) {
  			
			$scope.submissions = angular.fromJson(data.body).payload.PageImpl.content

			$scope.tableParams = new NgTableParams({ }, 
	  		{
	  			filterDelay: 0, 
	  			dataset: $scope.submissions
	  		});

	  		$scope.tableParams.reload();
		});
  	};

  	SubmissionRepo.listen(function() {
		updateColumns();
  	});

  	$q.all([SubmissionListColumnRepo.ready(), ManagerSubmissionListColumnRepo.ready()]).then(function(data) {
		updateColumns();
	});

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

	$scope.resetColumns = function() {

		$q.all(listenForAllSubmissionColumns(), listenForManagersSubmissionColumns()).then(function() {
	  		updateColumns();
	  	});

		SubmissionListColumnRepo.reset();
		ManagerSubmissionListColumnRepo.reset();

		$scope.closeModal();

		$scope.itemMoved = false;
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
			if(value === undefined) {
				value = row[col.path[i]];
			}
			else {
				value = value[col.path[i]];
			}
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
