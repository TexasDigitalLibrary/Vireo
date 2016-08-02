vireo.controller("DocumentTypesController", function ($controller, $scope, DocumentTypeRepo, DragAndDropListenerFactory) {
	
    angular.extend(this, $controller("AbstractController", {$scope: $scope}));

    $scope.documentTypeRepo = DocumentTypeRepo;

	$scope.documentTypes = DocumentTypeRepo.getAll();
	
	DocumentTypeRepo.listen(function(data) {
        $scope.resetDocumentTypes();
	});
	
	$scope.ready = DocumentTypeRepo.ready();

	$scope.dragging = false;
	
	$scope.trashCanId = 'document-types-trash';
	
	$scope.sortAction = "confirm";

    $scope.degreeLevels = { 
		'UNDERGRADUATE': 'Undergraduate',
        'MASTERS': 'Masters',
    	'DOCTORAL': 'Doctoral'
    };
    
    $scope.forms = {};
    
    $scope.ready.then(function() {

    	$scope.resetDocumentTypes = function() {
            $scope.documentTypeRepo.clearValidationResults();
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

        $scope.resetDocumentTypes();
        
        $scope.createNewDocumentType = function() {
            DocumentTypeRepo.create($scope.modalData);
	    };	

        $scope.launchEditModal = function(index) {
            $scope.modalData = $scope.documentTypes[index -1];
            $scope.openModal('#documentTypesEditModal');
	    };	

        $scope.updateDocumentType = function() {
            $scope.modalData.save();
        };

        $scope.removeDocumentType = function() {
            $scope.modalData.delete();
        };

        $scope.reorderDocumentTypes = function(src, dest) {
            DocumentTypeRepo.reorder(src, dest);
        };

        $scope.selectDocumentType = function(index) {
            $scope.modalData = $scope.documentTypes[index];
        };

        $scope.sortDocumentTypes = function(column) {
            if($scope.sortAction == 'confirm') {
                $scope.sortAction = 'sort';
            }
            else if($scope.sortAction == 'sort') {
                DocumentTypeRepo.sort(column);
                $scope.sortAction = 'confirm';
            }
        };

        $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
            trashId: $scope.trashCanId,
            dragging: $scope.dragging,
            select: $scope.selectDocumentType,
            model: $scope.documentTypes,
            confirm: '#documentTypesConfirmRemoveModal',
            reorder: $scope.reorderDocumentTypes,
            container: '#document-types'
        });

    });

});
