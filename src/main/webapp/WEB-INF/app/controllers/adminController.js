vireo.controller('AdminController', function ($controller, $location, $scope) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    var view = $location.path();

    $scope.isList = function () {
        return view.includes("/admin/list");
    };

    $scope.isView = function () {
        return view.includes("/admin/view");
    };

    $scope.isLog = function () {
        return view.includes("/admin/log");
    };

    $scope.isSettings = function () {
        return view.includes("/admin/settings");
    };

});
