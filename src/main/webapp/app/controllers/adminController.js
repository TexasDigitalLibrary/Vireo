vireo.controller('AdminController', function ($controller, $location, $scope) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.isList = function () {
        return $location.path().indexOf("/admin/list") >= 0;
    };

    $scope.isView = function () {
        return ($location.path().indexOf("/admin/view") >= 0 && $location.path().indexOf("Error") == 0);
    };

    $scope.isViewError = function () {
        return $location.path().indexOf("/admin/viewError") >= 0;
    };

    $scope.isLog = function () {
        return $location.path().indexOf("/admin/log") >= 0;
    };

    $scope.isSettings = function () {
        return $location.path().indexOf("/admin/settings") >= 0;
    };

});
