vireo.controller("FooterController", function ($scope, $controller, ManagedConfigurationRepo) {

    angular.extend($scope, $controller("AbstractController", { $scope: $scope }));

    console.log('FooterController');

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

            $scope.webmaster_label = $scope.buildLabel($scope.configurable.footer.link_webmaster, "to the webmaster.", "contacting the webmaster.");
            $scope.legal_label = $scope.buildLabel($scope.configurable.footer.link_legal, "regarding legal matters.", "legal matters.");
            $scope.comments_label = $scope.buildLabel($scope.configurable.footer.link_comments, "with any comments.", "providing comments.");
            $scope.accessibility_label = $scope.buildLabel($scope.configurable.footer.link_accessibility, "regarding accessibility.", "accessibility.");

            $scope.webmaster_hidden = $scope.webmaster_label === undefined ? true : undefined;
            $scope.legal_hidden = $scope.legal_label === undefined ? true : undefined;
            $scope.comments_hidden = $scope.comments_label === undefined ? true : undefined;
            $scope.accessibility_hidden = $scope.accessibility_label === undefined ? true : undefined;
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

    $scope.buildLabel = function (setting, email, about) {

        if (setting && setting.value) {
            var regex = /^[^@]{1,}@[^@]{1,}$/;

            if (regex.test(setting.value)) {
                return "Send an e-mail " + email;
            }

            return "View page with further information about " + about;
        }

        return undefined;
    };

});
