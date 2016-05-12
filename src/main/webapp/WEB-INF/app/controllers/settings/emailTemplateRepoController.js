vireo.controller("EmailTemplateRepoController", function ($controller, $scope, $q, EmailTemplateRepo, DragAndDropListenerFactory) {
  angular.extend(this, $controller("AbstractController", {$scope: $scope}));

  $scope.emailTemplates = EmailTemplateRepo.get();
  
  $scope.ready = $q.all([EmailTemplateRepo.ready()]);

  $scope.dragging = false;
  
  $scope.serverErrors = [];

  $scope.trashCanId = 'email-template-trash';
  
  $scope.sortAction = "confirm";

  $scope.templateToString = function(template) {
    return template.name;
  }

  $scope.ready.then(function() {

    $scope.resetEmailTemplates = function() {
      $scope.modalData = {'name':'', 'subject':'', 'message':''};
    }
    
    $scope.closeModal = function(modalId) {
		angular.element('#' + modalId).modal('hide');
		// clear all errors, but not infos or warnings
		if($scope.serverErrors !== undefined) {
			$scope.serverErrors.errors = undefined;
		}
	}

    $scope.resetEmailTemplates();

    $scope.selectEmailTemplate = function(index){
    	$scope.modalData = $scope.emailTemplates.list[index];
    }

    $scope.createEmailTemplate = function() {
      EmailTemplateRepo.add($scope.modalData).then(function(data) {
    	  $scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
    	  if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
    		  $scope.resetEmailTemplates();
    		  $scope.closeModal("emailTemplatesNewModal");
    	  }
      });
    };

    $scope.launchEditModal = function(index) {
    	$scope.serverErrors = [];
    	$scope.modalData = $scope.emailTemplates.list[index-1];
    	angular.element('#emailTemplatesEditModal').modal('show');
    };

    $scope.updateEmailTemplate = function() {
      EmailTemplateRepo.update($scope.modalData).then(function(data) {
    	  $scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
    	  if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
    		  $scope.resetEmailTemplates();
    		  $scope.closeModal("emailTemplatesEditModal");
    	  }
      });
    };

    $scope.removeEmailTemplate = function(index) {
      EmailTemplateRepo.remove(index).then(function(data) {
    	  $scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
    	  if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
    		  $scope.resetEmailTemplates();
    		  $scope.closeModal("emailTemplatesConfirmRemoveModal");
    	  }
      });
    };

    $scope.reorderEmailTemplates = function(src, dest){
      EmailTemplateRepo.reorder(src, dest).then(function(data) {
    	  $scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
    	  if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
    		  $scope.resetEmailTemplates();
    	  }
      });
    }

    $scope.sortEmailTemplates = function(column) {
      if($scope.sortAction == 'confirm') {
        $scope.sortAction = 'sort';
      }
      else if($scope.sortAction == 'sort') {
        EmailTemplateRepo.sort(column).then(function(data) {
        	$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
        	if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
        		$scope.resetEmailTemplates();
        	}
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
