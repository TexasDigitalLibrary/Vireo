vireo.controller("EmailTemplateRepoController", function ($controller, $scope, $q, EmailTemplateRepo, DragAndDropListenerFactory) {
  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  $scope.emailTemplates = EmailTemplateRepo.get();
  
  $scope.ready = $q.all([EmailTemplateRepo.ready()]);

  $scope.dragging = false;

  $scope.trashCanId = 'email-template-trash';
  
  $scope.sortAction = "confirm";

  $scope.resetModalData = function() {
    $scope.modalData = {'name':'', 'subject':'', 'message':''};
  };

  $scope.templateToString = function(template) {
    return template.name;
  }

  $scope.ready.then(function() {

    $scope.resetModalData();

    $scope.selectEmailTemplate = function(index){
      $scope.modalData = $scope.emailTemplates.list[index];
    }

    $scope.createEmailTemplate = function() {
      EmailTemplateRepo.add($scope.modalData).then(function() {
        $scope.resetModalData();
      });
    };

    $scope.launchEditModal = function(index) {
      $scope.modalData = $scope.emailTemplates.list[index];
      angular.element('#emailTemplatesEditModal').modal('show');
    };

    $scope.updateEmailTemplate = function() {
      EmailTemplateRepo.update($scope.modalData).then(function() {
        $scope.resetModalData();
      });
    };

    $scope.removeEmailTemplate = function(index) {
      EmailTemplateRepo.remove(index).then(function() {
        $scope.resetModalData();
      });
    };

    $scope.reorderEmailTemplates = function(src, dest){
      EmailTemplateRepo.reorder(src, dest).then(function() {
        $scope.resetModalData();
      });
    }

    $scope.sortEmailTemplates = function(column) {
      if($scope.sortAction == 'confirm') {
        $scope.sortAction = 'sort';
      }
      else if($scope.sortAction == 'sort') {
        EmailTemplateRepo.sort(column).then(function() {
          $scope.resetModalData();
        });
        $scope.sortAction = 'confirm';
      }
    };

    

    $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
      trashId: $scope.trashCanId,
      dragging: $scope.dragging,
      select: $scope.selectEmailTemplate,     
      list: $scope.emailTemplates.list,
      confirm: '#emailTemplatesConfirmRemoveModal',
      reorder: $scope.reorderEmailTemplates,
      container: '#email-templates'
    });
    
  });	

});
