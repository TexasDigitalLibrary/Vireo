vireo.controller("SubmissionCompleteController", function ($controller, $scope, $routeParams) {

  angular.extend(this, $controller('AbstractController', {$scope: $scope}));
  console.info('complete controller scope', $scope);

});
