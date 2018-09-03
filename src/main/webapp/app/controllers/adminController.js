vireo.controller('AdminController', function ($controller, $location, $scope) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    var view = $location.path();

    $scope.isList = function () {
        return view.indexOf("/admin/list") >= 0;
    };

    $scope.isView = function () {
        return view.indexOf("/admin/view") >= 0;
    };

    $scope.isLog = function () {
        return view.indexOf("/admin/log") >= 0;
    };

    $scope.isSettings = function () {
        return view.indexOf("/admin/settings") >= 0;
    };

});
