vireo.controller("CompleteSubmissionController", function($controller, $scope, ManagedConfigurationRepo) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    console.log('CompleteSubmissionController');

    $scope.configuration = ManagedConfigurationRepo.getAll();

});
