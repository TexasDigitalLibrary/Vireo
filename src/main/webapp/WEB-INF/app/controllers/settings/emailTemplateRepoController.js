vireo.controller("EmailTemplateRepoController", function ($controller, $scope, $q, EmailTemplateRepo, DragAndDropListenerFactory) {

  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  $scope.emailTemplateRepo = EmailTemplateRepo;

  $scope.emailTemplates = EmailTemplateRepo.getAll();

  EmailTemplateRepo.listen(function(data) {
        $scope.resetEmailTemplates();
  });

  $scope.ready = $q.all([EmailTemplateRepo.ready()]);

  $scope.dragging = false;

  $scope.trashCanId = 'email-template-trash';

  $scope.sortAction = "confirm";

  $scope.templateToString = function(template) {
    return template.name;
  };

  $scope.forms = {};

  $scope.ready.then(function() {

    $scope.resetEmailTemplates = function() {
      $scope.emailTemplateRepo.clearValidationResults();
      for(var key in $scope.forms) {
          if($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
            $scope.forms[key].$setPristine();
          }
        }
      if($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
          $scope.modalData.refresh();
        }
        $scope.modalData = {
          'name': '',
          'subject': '',
          'message':''
      };
      $scope.closeModal();
    };

    $scope.resetEmailTemplates();

    $scope.selectEmailTemplate = function(index){
      $scope.modalData = $scope.emailTemplates[index];
    };

    $scope.createEmailTemplate = function() {
        EmailTemplateRepo.create($scope.modalData).then(function(data) {

        });
    };

    $scope.launchEditModal = function(index) {
      $scope.modalData = $scope.emailTemplates[index-1];
      $scope.openModal('#emailTemplatesEditModal');
    };

    $scope.updateEmailTemplate = function() {
        $scope.modalData.save();
    };

    $scope.removeEmailTemplate = function() {
      $scope.modalData.delete();
    };

    $scope.reorderEmailTemplates = function(src, dest){
        EmailTemplateRepo.reorder(src, dest);
    };

    $scope.sortEmailTemplates = function(column) {
      if($scope.sortAction == 'confirm') {
        $scope.sortAction = 'sort';
      }
      else if($scope.sortAction == 'sort') {
        EmailTemplateRepo.sort(column);
        $scope.sortAction = 'confirm';
      }
    };

    $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
        trashId: $scope.trashCanId,
        dragging: $scope.dragging,
        select: $scope.selectEmailTemplate,
        model: $scope.emailTemplates,
        confirm: '#emailTemplatesConfirmRemoveModal',
        reorder: $scope.reorderEmailTemplates,
        container: '#email-templates'
    });

  });

});
