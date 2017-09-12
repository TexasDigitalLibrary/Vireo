vireo.controller("CompleteSubmissionController", function($controller, $scope, ConfigurationRepo) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    ConfigurationRepo.ready().then(function() {
        $scope.configuration = ConfigurationRepo.getAll();
    });

});
