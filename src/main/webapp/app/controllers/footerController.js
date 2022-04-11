vireo.controller("FooterController", function ($scope, $controller, ManagedConfigurationRepo) {

    angular.extend($scope, $controller("AbstractController", { $scope: $scope }));

    $scope.configurable = ManagedConfigurationRepo.getAll();

    $scope.webmaster = "#";
    $scope.legal = "#";
    $scope.comments = "#";
    $scope.accessibility = "#";

    ManagedConfigurationRepo.ready().then(function() {

        if ($scope.configurable.footer) {
            $scope.webmaster = $scope.buildLink($scope.configurable.footer.link_webmaster);
            $scope.legal = $scope.buildLink($scope.configurable.footer.link_legal);
            $scope.comments = $scope.buildLink($scope.configurable.footer.link_comments);
            $scope.accessibility = $scope.buildLink($scope.configurable.footer.link_accessibility);
        }
    });

    $scope.buildLink = function (setting) {

        if (setting && setting.value) {
            var regex = /^[^@]{1,}@[^@]{1,}$/;

            if (regex.test(setting.value)) {
                return "mailto:" + setting.value;
            }

            return setting.value;
        }

        return "#";
    };

});
