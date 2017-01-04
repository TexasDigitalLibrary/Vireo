vireo.controller("AttachmentTypesController", function ($controller, $scope, AttachmentTypeRepo, DragAndDropListenerFactory) {
	
    angular.extend(this, $controller("AbstractController", {$scope: $scope}));

    $scope.attachmentTypeRepo = AttachmentTypeRepo;

	$scope.attachmentTypes = AttachmentTypeRepo.getAll();
	
	AttachmentTypeRepo.listen(function(data) {
        $scope.resetAttachmentTypes();
	});
	
	$scope.ready = AttachmentTypeRepo.ready();

	$scope.dragging = false;
	
	$scope.trashCanId = 'attachment-types-trash';
	
	$scope.sortAction = "confirm";

    $scope.degreeLevels = { 
		'UNDERGRADUATE': 'Undergraduate',
        'MASTERS': 'Masters',
    	'DOCTORAL': 'Doctoral'
    };
    
    $scope.forms = {};
    
    $scope.ready.then(function() {

    	$scope.resetAttachmentTypes = function() {
            $scope.attachmentTypeRepo.clearValidationResults();
    		for(var key in $scope.forms) {
    			if($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
    				$scope.forms[key].$setPristine();
    			}
    		}
    		if($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
    			$scope.modalData.refresh();
    		}
            $scope.modalData = {
                degreeLevel: 'UNDERGRADUATE'
            };            
            $scope.closeModal();
        };

        $scope.resetAttachmentTypes();
        
        $scope.createNewAttachmentType = function() {
            AttachmentTypeRepo.create($scope.modalData);
	    };	

        $scope.launchEditModal = function(index) {
            $scope.modalData = $scope.attachmentTypes[index -1];
            $scope.openModal('#attachmentTypesEditModal');
	    };	

        $scope.updateAttachmentType = function() {
            $scope.modalData.save();
        };

        $scope.removeAttachmentType = function() {
            $scope.modalData.delete();
        };

        $scope.reorderAttachmentTypes = function(src, dest) {
            AttachmentTypeRepo.reorder(src, dest);
        };

        $scope.selectAttachmentType = function(index) {
            $scope.modalData = $scope.attachmentTypes[index];
        };

        $scope.sortAttachmentTypes = function(column) {
            if($scope.sortAction == 'confirm') {
                $scope.sortAction = 'sort';
            }
            else if($scope.sortAction == 'sort') {
                AttachmentTypeRepo.sort(column);
                $scope.sortAction = 'confirm';
            }
        };

        $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
            trashId: $scope.trashCanId,
            dragging: $scope.dragging,
            select: $scope.selectAttachmentType,
            model: $scope.attachmentTypes,
            confirm: '#attachmentTypesConfirmRemoveModal',
            reorder: $scope.reorderAttachmentTypes,
            container: '#attachment-types'
        });

    });

});
