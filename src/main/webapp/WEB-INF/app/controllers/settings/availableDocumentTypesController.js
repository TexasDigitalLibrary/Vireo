vireo.controller("AvailableDocumentTypesController", function ($controller, $scope, AvailableDocumentTypesRepo, DragAndDropListenerFactory) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.documentTypes = AvailableDocumentTypesRepo.get();
	
	$scope.ready = AvailableDocumentTypesRepo.ready();

        $scope.ready.then(function (){
            console.info('then doctypes V');
            console.info($scope.documentTypes.list);
            console.info('then doctypes ^');
        });

	$scope.dragging = false;

	$scope.trashCanId = 'available-document-types-trash';
	
	$scope.sortAction = "confirm";

        $scope.modalData = {};

        $scope.degreeLevels = { 'UNDERGRADUATE' : 'Undergraduate',
                                'MASTERS'       : 'Masters'      ,
                                'DOCTORAL'      : 'Doctoral'     };

        $scope.modalData.degreeLevel = 'UNDERGRADUATE';

        // $scope.createNewDocumentType = function(name, degreeLevel) {
        $scope.createNewDocumentType = function(documentType) {
            console.info('calling ctrl creation');
            AvailableDocumentTypesRepo.add(documentType);
	};	
});
