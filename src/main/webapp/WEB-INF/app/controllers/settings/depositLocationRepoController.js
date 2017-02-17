vireo.controller("DepositLocationRepoController", function ($controller, $scope, $q, DepositLocationRepo, DragAndDropListenerFactory) {
  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  $scope.depositLocationRepo = DepositLocationRepo;

  $scope.depositLocations = DepositLocationRepo.getAll();

  DepositLocationRepo.listen(function(data) {
        $scope.resetDepositLocation();
  });

  $scope.protocols = {
    "Sword1Deposit": "SWORD Version 1",
    "FileDeposit": "File Deposit"
  };


  $scope.packagers = {
    "VireoExport": "Vireo Export"
  };

  $scope.ready = $q.all([DepositLocationRepo.ready()]);

  $scope.dragging = false;

  $scope.trashCanId = 'deposit-location-trash';

  $scope.forms = {};

  $scope.ready.then(function() {

    $scope.resetDepositLocation = function() {
      $scope.depositLocationRepo.clearValidationResults();
      for(var key in $scope.forms) {
          if($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
            $scope.forms[key].$setPristine();
          }
        }
      if($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
          $scope.modalData.refresh();
        }
      $scope.modalData = {
        depositor: 'Sword1Deposit',
        packager: 'VireoExport'
      };
      $scope.closeModal();
    };

    $scope.resetDepositLocation();

    $scope.createDepositLocation = function() {
      DepositLocationRepo.create($scope.modalData);
    };

    $scope.selectDepositLocation = function(index) {
      $scope.modalData = $scope.depositLocations[index];
    };

    $scope.editDepositLocation = function(index) {
      $scope.selectDepositLocation(index - 1);
      $scope.openModal('#depositLocationEditModal');
    };

    $scope.updateDepositLocation = function() {
      $scope.modalData.save();
    };

    $scope.removeDepositLocation = function() {
      $scope.modalData.delete();
    };

    $scope.reorderDepositLocation = function(src, dest) {
        DepositLocationRepo.reorder(src, dest);
    };

    $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
      trashId: $scope.trashCanId,
      dragging: $scope.dragging,
      select: $scope.selectDepositLocation,
      model: $scope.depositLocations,
      confirm: '#depositLocationConfirmRemoveModal',
      reorder: $scope.reorderDepositLocation,
      container: '#deposit-location'
    });

  });

});
