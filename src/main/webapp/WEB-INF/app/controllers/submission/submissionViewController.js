vireo.controller("SubmissionViewController", function ($controller, $filter, $q, $scope, NgTableParams, SubmissionRepo, SubmissionViewColumnRepo, ManagerSubmissionViewColumnRepo, WsApi) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

  	$scope.submissions = SubmissionRepo.getAll();

  	$scope.columns = [];

  	$scope.userColumns = [];

  	$scope.resultsPerPageOptions = [20, 40, 60, 100, 200, 400, 500, 1000];

  	$scope.resultsPerPage = 100;


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

	$q.all([SubmissionViewColumnRepo.ready(), ManagerSubmissionViewColumnRepo.ready()]).then(function(data) {
		$scope.userColumns = ManagerSubmissionViewColumnRepo.getAll();
		$scope.columns = $filter('exclude')(SubmissionViewColumnRepo.getAll(), $scope.userColumns, 'title');
	});

	$scope.resetColumns = function() {

		$q.all(listenForAllSubmissionColumns(), listenForManagersSubmissionColumns()).then(function() {
	  		$scope.userColumns = ManagerSubmissionViewColumnRepo.getAll();
			$scope.columns = $filter('exclude')(SubmissionViewColumnRepo.getAll(), $scope.userColumns, 'title');
	  	});

		SubmissionViewColumnRepo.reset();
		ManagerSubmissionViewColumnRepo.reset();

		$scope.closeModal();
	};

	$scope.resetColumnsToDefault = function() {
		$scope.closeModal();
	};

	$scope.saveColumns = function() {
		$scope.closeModal();
	};

	$scope.columnOptions = {
		accept: function (sourceItemHandleScope, destSortableScope, destItemScope) {
			return true;
		},
		itemMoved: function (event) {			
			if(event.source.sortableScope.$id < event.dest.sortableScope.$id) {
				event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'previouslyDisplayed' : undefined;
				
			}
			else {
				event.source.itemScope.column.status = !event.source.itemScope.column.status ? 'pervisoulyDisabled' : undefined;
			}
		},
		orderChanged: function (event) {

		},
		containment: '#column-modal',
		additionalPlaceholderClass: 'column-placeholder'
	};

  	SubmissionRepo.ready().then(function() {
  		$scope.tableParams = new NgTableParams({}, {filterDelay: 0, dataset: $scope.submissions}); 
  		$scope.tableParams.reload();
  	})

  	SubmissionRepo.listen(function() {
		$scope.tableParams.reload();
  	});

});

vireo.filter('exclude', function() {
    return function(input, exclude, prop) {
        if (!angular.isArray(input))
            return input;

        if (!angular.isArray(exclude))
            exclude = [];

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
