vireo.controller("NoteManagementController", function ($controller, $scope, DragAndDropListenerFactory, Note, NoteRepo, OrganizationRepo, WorkflowStepRepo) {

    angular.extend(this, $controller("AbstractController", {$scope: $scope}));

    $scope.workflowStepRepo = WorkflowStepRepo;

    $scope.noteRepo = NoteRepo;

    $scope.selectedOrganization = OrganizationRepo.getSelectedOrganization();

    $scope.$watch(
        "step",
        function handleStepChanged(newStep, oldStep) {
            $scope.resetNotes();

            $scope.dragControlListeners.getListener().model = $scope.step.aggregateNotes;
            $scope.dragControlListeners.getListener().trash.id = 'note-trash-' + $scope.step.id;
            $scope.dragControlListeners.getListener().confirm.remove.modal = '#notesConfirmRemoveModal-' + $scope.step.id;
        }
    );

    $scope.dragging = false;

    $scope.sortAction = "confirm";

    $scope.uploadAction = "confirm";

    $scope.forms = {};

    $scope.resetNotes = function() {
        $scope.workflowStepRepo.clearValidationResults();
        $scope.noteRepo.clearValidationResults();
        for (var key in $scope.forms) {
            if ($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
                $scope.forms[key].$setPristine();
                $scope.forms[key].$setUntouched();
            }
        }

        var position = 1;

        if ($scope.step) {
            angular.forEach($scope.step.aggregateNotes, function(note) {
                note.position = position;
                position++;
            });
        }

        if ($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
            $scope.modalData.refresh();
        }
        $scope.modalData = new Note({
            overrideable: true,
            name: '',
            text: ''
        });

        $scope.closeModal();
    };

    $scope.resetNotes();

    $scope.createNote = function() {
        if (!!$scope.getSelectedOrganizationId()) {
            $scope.getSelectedOrganization().$dirty = true;
        }

        WorkflowStepRepo.addNote($scope.step, $scope.modalData);
    };

    $scope.selectNote = function(index) {
        $scope.modalData = new Note($scope.step.aggregateNotes[index]);
    };

    $scope.editNote = function(index) {
        $scope.selectNote(index - 1);
        $scope.openModal('#notesEditModal-' + $scope.step.id);
    };

    $scope.updateNote = function() {
        if (!!$scope.getSelectedOrganizationId()) {
            $scope.getSelectedOrganization().$dirty = true;
        }

        WorkflowStepRepo.updateNote($scope.step, $scope.modalData);
    };

    $scope.removeNote = function() {
        if (!!$scope.getSelectedOrganizationId()) {
            $scope.getSelectedOrganization().$dirty = true;
        }

        WorkflowStepRepo.removeNote($scope.step, $scope.modalData);
    };

    $scope.reorderNotes = function(src, dest) {
        if (!!$scope.getSelectedOrganizationId()) {
            $scope.getSelectedOrganization().$dirty = true;
        }

        return WorkflowStepRepo.reorderNotes($scope.step, src, dest);
    };

    $scope.isEditable = function(note) {
        var editable = note.overrideable;
        if (!editable) {
            editable = note.originatingWorkflowStep == $scope.step.id &&
                       !!$scope.selectedOrganization && $scope.selectedOrganization.originalWorkflowSteps.indexOf(note.originatingWorkflowStep) > -1;
        }
        return editable;
    };

    $scope.openNewModal = function(id) {
        $scope.openModal('#notesNewModal-' + id);
    };

    $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
        trashId: 'note-trash-' + $scope.step.id,
        dragging: $scope.dragging,
        select: $scope.selectNote,
        model: $scope.step.aggregateNotes,
        confirm: '#notesConfirmRemoveModal-' + $scope.step.id,
        reorder: $scope.reorderNotes,
        container: '#notes'
    });

});
