vireo.controller("CompleteSubmissionController", function($controller, $scope, ManagedConfigurationRepo) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    $scope.configuration = ManagedConfigurationRepo.getAll();

});
