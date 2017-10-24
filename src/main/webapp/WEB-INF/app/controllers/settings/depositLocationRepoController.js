vireo.controller("DepositLocationRepoController", function ($controller, $scope, $q, DepositLocationRepo, DepositLocation, PackagerRepo, DragAndDropListenerFactory) {
    angular.extend(this, $controller("AbstractController", {
        $scope: $scope
    }));

    $scope.depositLocationRepo = DepositLocationRepo;

    $scope.depositLocations = DepositLocationRepo.getAll();

    DepositLocationRepo.listen(function (data) {
        $scope.resetDepositLocation();
    });

    $scope.protocols = {
        "SWORDv1Depositor": "SWORD Version 1",
        "FileDeposit": "File Deposit"
    };

    $scope.collections = [];

    $scope.packagers = PackagerRepo.getAll();

    $scope.ready = $q.all([DepositLocationRepo.ready(), PackagerRepo.ready()]);

    $scope.dragging = false;

    $scope.trashCanId = 'deposit-location-trash';

    $scope.forms = {};

    var isTestDepositing = false;

    $scope.ready.then(function () {

        $scope.resetDepositLocation = function () {
            $scope.depositLocationRepo.clearValidationResults();
            for (var key in $scope.forms) {
                if ($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
                    $scope.forms[key].$setPristine();
                }
            }
            if ($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
                $scope.modalData.refresh();
            }
            $scope.modalData = {
                depositorName: 'SWORDv1Depositor',
                timeout: 240,
                packager: $scope.packagers.length > 0 ? $scope.packagers[0] : undefined
            };

            $scope.modalData.testDepositLocation = function () {
                isTestDepositing = true;
                var testData = angular.copy($scope.modalData);
                delete testData.packager;
                var testableDepositLocation = new DepositLocation(testData);
                testableDepositLocation.testConnection().then(function (response) {
                    var data = angular.fromJson(response.body);
                    var collections = data.payload.HashMap;
                    angular.forEach(collections, function (uri, name) {
                        $scope.collections.push({
                            "name": name,
                            "uri": uri
                        });
                    });
                    isTestDepositing = false;
                });
            };

            $scope.modalData.isTestDepositing = function () {
                return isTestDepositing;
            };

            $scope.modalData.isTestable = function () {
                return (!isTestDepositing && $scope.modalData.name && $scope.modalData.depositorName && $scope.modalData.repository && $scope.modalData.username && $scope.modalData.password);
            };

            $scope.closeModal();
        };

        $scope.resetDepositLocation();

        $scope.createDepositLocation = function () {
            DepositLocationRepo.create($scope.modalData);
        };

        $scope.selectDepositLocation = function (index) {
            $scope.modalData = $scope.depositLocations[index];
        };

        $scope.editDepositLocation = function (index) {
            $scope.selectDepositLocation(index - 1);
            $scope.openModal('#depositLocationEditModal');
        };

        $scope.updateDepositLocation = function () {
            console.log('update');
            $scope.modalData.save();
        };

        $scope.removeDepositLocation = function () {
            console.log('delete');
            $scope.modalData.delete();
        };

        $scope.reorderDepositLocation = function (src, dest) {
            DepositLocationRepo.reorder(src, dest);
        };

        $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
            trashId: $scope.trashCanId,
            dragging: $scope.dragging,
            select: $scope.selectDepositLocation,
            model: $scope.depositLocations,
            confirm: '#depositLocationConfirmRemoveModal',
            reorder: $scope.reorderDepositLocation,
            container: '#deposit-locations'
        });

    });

});
