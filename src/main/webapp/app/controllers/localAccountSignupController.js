vireo.controller('LocalAccountSignupController', function ($scope, $controller, $location) {
    angular.extend($scope, $controller('AbstractController', { $scope: $scope }));

    $scope.registerEnabled =
        appConfig.localAuthentication === true ||
        appConfig.localAuthentication === 'alternateAllowRegister';

    $scope.goHome = function () {
        $location.path('/');
    };
});

