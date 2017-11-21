vireo.controller("LookAndFeelController", function ($scope, $controller, $q, FileService, WsApi, RestApi) {

    angular.extend(this, $controller("AbstractController", {
        $scope: $scope
    }));

    $scope.modalData = {
        newLogo: {}
    };

    $scope.modalData.logoLeft = $scope.settings.configurable.lookAndFeel.left_logo.value;

    $scope.modalData.logoRight = $scope.settings.configurable.lookAndFeel.right_logo.value;

    $scope.previewLogo = function (files) {
        if (files.length > 0) {
            previewLogo(files[0]).then(function (result) {

                var fileType = result.substring(result.indexOf("/") + 1, result.indexOf(";"));

                $scope.modalData.newLogo.fileType = fileType;
                $scope.modalData.newLogo.display = result;
                $scope.modalData.newLogo.file = files[0];

                angular.element('#newLogoConfirmUploadModal').modal('show');

            });
        }
    };

    $scope.modalData.confirmLogoUpload = function () {

        //TODO: This may be better if removed to a service
        var uploadPromise = FileService.upload({
            'endpoint': '',
            'controller': 'settings/look-and-feel',
            'method': 'logo/upload/' + $scope.modalData.newLogo.setting + '/' + $scope.modalData.newLogo.fileType,
            'file': $scope.modalData.newLogo.file
        });

        uploadPromise.then(
            function (response) {
                var data = response.data;
                if (data.meta.status === 'SUCCESS') {
                    updateLogos(data.payload);
                }
            },
            function (response) {
                if (response.payload !== undefined) {
                    // validation
                }
                console.log("Error");
            }
        );

        return uploadPromise;

    };

    $scope.modalData.cancelLogoUpload = function () {
        $scope.resetModalData();
        angular.element('#newLogoConfirmUploadModal').modal('hide');
    };

    $scope.resetLogo = function (setting) {

        //TODO: This may be better if removed to a service
        var resetPromise = WsApi.fetch({
            'endpoint': '/private/queue',
            'controller': 'settings/look-and-feel',
            'method': 'logo/reset/' + setting
        });

        resetPromise.then(
            function (response) {
                var data = angular.fromJson(response.body);
                if (data.meta.status === 'SUCCESS') {
                    updateLogos(data.payload);
                }
            },
            function (response) {
                if (response.payload !== undefined) {
                    // validation
                }
                console.log("error");
            }
        );

        return resetPromise;
    };

    var updateLogos = function (payload) {
        var newLogoConfiguration = payload.ManagedConfiguration ? payload.ManagedConfiguration : payload.DefaultConfiguration;

        if (newLogoConfiguration !== undefined) {
            if (newLogoConfiguration.name == "left_logo") {
                $scope.modalData.logoLeft = newLogoConfiguration.value;
                $scope.settings.configurable.lookAndFeel.left_logo = newLogoConfiguration.value;
            }
            if (newLogoConfiguration.name == "right_logo") {
                $scope.modalData.logoRight = newLogoConfiguration.value;
                $scope.settings.configurable.lookAndFeel.right_logo = newLogoConfiguration.value;
            }

            $scope.resetModalData();
        }

    };

    $scope.resetModalData = function () {
        $scope.modalData.newLogo = {};
        $scope.modalData.newLogo.setting = "left_logo";
    };

    var previewLogo = function (file) {

        var defer = $q.defer();
        var reader = new FileReader();

        reader.onload = function () {
            defer.resolve(reader.result);
        };

        reader.readAsDataURL(file);

        return defer.promise;
    };

});
