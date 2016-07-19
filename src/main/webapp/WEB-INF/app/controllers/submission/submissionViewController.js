vireo.controller("SubmissionViewController", function ($controller, $filter, $q, $scope, NgTableParams, SubmissionRepo, WsApi) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

  	$scope.submissions = SubmissionRepo.getAll();

  	$scope.columns = [];

  	$scope.userColumns = [];

  	$scope.resultsPerPageOptions = [20, 40, 60, 100, 200, 400, 500, 1000];

  	$scope.resultsPerPage = 100;

  	var allColumnsPromise = WsApi.fetch({
		'endpoint': '/private/queue',
		'controller': 'submission',
		'method': 'all-columns'
	});

	var userColumnsPromise = WsApi.fetch({
		'endpoint': '/private/queue',
		'controller': 'submission',
		'method': 'columns-by-user'
	});

	$q.all([allColumnsPromise, userColumnsPromise]).then(function(data) {
		angular.extend($scope.userColumns, angular.fromJson(data[1].body).payload["ArrayList<String>"]);
		angular.extend($scope.columns, $filter('exclude')(angular.fromJson(data[0].body).payload["ArrayList<String>"], $scope.userColumns));
	});

	$scope.columnOptions = {
		accept: function (sourceItemHandleScope, destSortableScope, destItemScope) {
			return true;
		},
		itemMoved: function (event) {

		},
		orderChanged: function (event) {

		},
		containment: '#column-modal'
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
