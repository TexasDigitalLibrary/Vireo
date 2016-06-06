vireo.controller("FieldProfileManagementController", function ($controller, $q, $scope, OrganizationRepo, DragAndDropListenerFactory, WorkflowStepRepo) {
	
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));
	
	var selectedOrganization = OrganizationRepo.getSelectedOrganization(); 
	
	
	console.log(selectedOrganization);
	
	$scope.fieldProfiles = {
		list: $scope.step.aggregateFieldProfiles
	};

	//$scope.ready = $q.all([OrganizationRepo.ready()]);

	$scope.dragging = false;

	$scope.trashCanId = 'field-profile-trash';
	
	$scope.sortAction = "confirm";

	$scope.uploadAction = "confirm";
	

	
	//$scope.ready.then(function() {

		$scope.resetFieldProfiles = function() {

			var position = 1;	
			angular.forEach($scope.fieldProfiles.list, function(fieldProfile) {
				fieldProfile.position = position;
				position++;
			});
			
		};

		$scope.resetFieldProfiles();
		
		$scope.createFieldProfile = function() {
			// TODO
			console.log('create field profile');
		};
		
		$scope.selectFieldProfile = function(index) {
			$scope.modalData = $scope.step.aggregateFieldProfiles[index];
		};
		
		$scope.editFieldProfile = function(index) {
			$scope.selectFieldProfile(index - 1);
			angular.element('#fieldProfilesEditModal').modal('show');
		};
		
		$scope.updateFieldProfile = function() {
			// TODO
			console.log('update field profile');
		};

		$scope.reorderFieldProfiles = function(src, dest) {
			WorkflowStepRepo.reorder($scope.step.id, src, dest).then(function() {
				$scope.resetFieldProfiles();
			});
		};

		$scope.sortFieldProfiles = function(column) {
			
			if($scope.sortAction == 'confirm') {
				$scope.sortAction = 'sort';
			}
			else if($scope.sortAction == 'sort') {
				// TODO
				console.log('sort field profile');
			}
		};

		$scope.removeFieldProfile = function(index) {
	    	// TODO
			console.log('remove field profile');
		};

		$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectFieldProfile,
			model: $scope.fieldProfiles,
			confirm: '#fieldProfilesConfirmRemoveModal',
			reorder: $scope.reorderFieldProfiles,
			container: '#fieldProfiles'
		});

		$scope.resetFieldProfiles();

	//});	


});