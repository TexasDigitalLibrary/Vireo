vireo.controller("EmailTemplateRepoController", function ($controller, $scope, $q, EmailTemplateRepo, DragAndDropListenerFactory) {
  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  $scope.emailTemplates = EmailTemplateRepo.get();
  
  $scope.ready = $q.all([EmailTemplateRepo.ready()]);

  $scope.dragging = false;

  $scope.trashCanId = 'email-template-trash';
  
  $scope.sortAction = "confirm";

  $scope.modalData = {'name':'', 'subject':'', 'message':''};
  
  $scope.resetModalData = function() {
    $scope.modalData = {'name':'', 'subject':'', 'message':''};
  };

  $scope.templateToString = function(template) {
    console.info('tostring');
    console.info(template);
    console.info(template.name);
    return template.name;
  }

  $scope.ready.then(function() {

    $scope.selectEmailTemplate = function(index){
      $scope.modalData = $scope.emailTemplates.list[index];
    }

    $scope.reorderEmailTemplates = function(src, dest){
      EmailTemplateRepo.reorder(src, dest);
    }

    $scope.createEmailTemplate = function() {
      EmailTemplateRepo.add($scope.modalData).then(function() {
        $scope.resetModalData();
      });
    };

    $scope.launchEditModal = function(index) {
      console.info('launching edit modal with index' + index);
      $scope.modalData = $scope.emailTemplates.list[index];
      angular.element('#emailTemplatesEditModal').modal('show');
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

    $scope.updateEmailTemplate = function() {
      console.info('attempting update...');
      EmailTemplateRepo.update($scope.modalData).then(function() {
        console.info('done; resetting');
        $scope.resetModalData();
      });
    };

    // $scope.sortGraduationMonths = function(column) {
    //   if($scope.sortAction == 'confirm') {
    //     $scope.sortAction = 'sort';
    //   }
    //   else if($scope.sortAction == 'sort') {
    //     GraduationMonthRepo.sort(column);
    //     $scope.sortAction = 'confirm';
    //   }
    // };

    $scope.removeEmailTemplate = function(index) {
      console.info('trying to remove: ' + index);
      EmailTemplateRepo.remove(index).then(function() {
        $scope.resetModalData();
      });
    };
    
  });	

});
