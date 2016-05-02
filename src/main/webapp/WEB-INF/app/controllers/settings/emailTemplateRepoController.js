vireo.controller("EmailTemplateRepoController", function ($controller, $scope, $q, EmailTemplateRepo, DragAndDropListenerFactory) {
  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  $scope.emailTemplates = EmailTemplateRepo.get();
  
  $scope.ready = $q.all([EmailTemplateRepo.ready()]);

  $scope.dragging = false;

  $scope.trashCanId = 'email-template-trash';
  
  $scope.sortAction = "confirm";

  $scope.templateToString = function(template) {
    return template.name;
  }

  $scope.ready.then(function() {

    $scope.resetEmailTemplates = function() {
      $scope.modalData = {'name':'', 'subject':'', 'message':''};
    }

    $scope.resetEmailTemplates();

    $scope.selectEmailTemplate = function(index){
      $scope.modalData = $scope.emailTemplates.list[index];
    }

    $scope.createEmailTemplate = function() {
      EmailTemplateRepo.add($scope.modalData).then(function(data) {
    	  var validationResponse = angular.fromJson(data.body).payload.ValidationResponse;
          console.log(validationResponse);
    	  $scope.resetEmailTemplates();
      });
    };

    $scope.launchEditModal = function(index) {
      $scope.modalData = $scope.emailTemplates.list[index-1];
      angular.element('#emailTemplatesEditModal').modal('show');
    };

    $scope.updateEmailTemplate = function() {
      EmailTemplateRepo.update($scope.modalData).then(function(data) {
    	  var validationResponse = angular.fromJson(data.body).payload.ValidationResponse;
          console.log(validationResponse);
    	  $scope.resetEmailTemplates();
      });
    };

    $scope.removeEmailTemplate = function(index) {
      EmailTemplateRepo.remove(index).then(function(data) {
    	  var validationResponse = angular.fromJson(data.body).payload.ValidationResponse;
          console.log(validationResponse);
    	  $scope.resetEmailTemplates();
      });
    };

    $scope.reorderEmailTemplates = function(src, dest){
      EmailTemplateRepo.reorder(src, dest).then(function(data) {
    	  var validationResponse = angular.fromJson(data.body).payload.ValidationResponse;
          console.log(validationResponse);
    	  $scope.resetEmailTemplates();
      });
    }

    $scope.sortEmailTemplates = function(column) {
      if($scope.sortAction == 'confirm') {
        $scope.sortAction = 'sort';
      }
      else if($scope.sortAction == 'sort') {
        EmailTemplateRepo.sort(column).then(function(data) {
        	var validationResponse = angular.fromJson(data.body).payload.ValidationResponse;
            console.log(validationResponse);
			$scope.resetEmailTemplates();
        });
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
