vireo.controller("EmailTemplateRepoController", function ($controller, $scope, $q, EmailTemplateRepo, DragAndDropListenerFactory) {
  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  $scope.emailTemplates = EmailTemplateRepo.get();
  
  $scope.ready = $q.all([EmailTemplateRepo.ready()]);

  $scope.dragging = false;

  $scope.trashCanId = 'email-template-trash';
  
  $scope.monthOptions = {};

  $scope.sortAction = "confirm";
  
  // $scope.resetGraduationMonth = function() {
  //   $scope.modalData = {};
  //   $scope.resetMonthOptions();
  // };
  
  $scope.ready.then(function() {
    
    $scope.resetGraduationMonth();
    
    $scope.createGraduationMonth = function() {
      GraduationMonthRepo.add($scope.modalData).then(function() {
        $scope.resetGraduationMonth();
      });
    };
    
    $scope.selectGraduationMonth = function(index) {
      $scope.resetMonthOptions();
      $scope.modalData = $scope.graduationMonths.list[index];
      $scope.modalData.month = $scope.modalData.month.toString();
    };
    
    $scope.editGraduationMonth = function(index) {
      $scope.selectGraduationMonth(index - 1);
      angular.element('#graduationMonthEditModal').modal('show');
    };
    
    $scope.updateGraduationMonth = function() {
      GraduationMonthRepo.update($scope.modalData).then(function() {
        $scope.resetGraduationMonth();
      });
    };

    $scope.reorderGraduationMonth = function(src, dest) {
      GraduationMonthRepo.reorder(src, dest);
    };

    $scope.sortGraduationMonths = function(column) {
      if($scope.sortAction == 'confirm') {
        $scope.sortAction = 'sort';
      }
      else if($scope.sortAction == 'sort') {
        GraduationMonthRepo.sort(column);
        $scope.sortAction = 'confirm';
      }
      
    };

    $scope.removeGraduationMonth = function(index) {
      GraduationMonthRepo.remove(index).then(function() {
        $scope.resetGraduationMonth();
      });
    };
    
    $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
      trashId: $scope.trashCanId,
      dragging: $scope.dragging,
      select: $scope.selectGraduationMonth,			
      list: $scope.graduationMonths.list,
      confirm: '#graduationMonthConfirmRemoveModal',
      reorder: $scope.reorderGraduationMonth,
      container: '#graduation-month'
    });
    
  });	

});
