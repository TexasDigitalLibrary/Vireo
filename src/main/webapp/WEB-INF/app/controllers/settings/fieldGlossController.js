vireo.controller('FieldGlossController', function ($controller, $scope, $q, $filter, SidebarService, DragAndDropListenerFactory, FieldGlossRepo) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    SidebarService.addBoxes([]);

    $scope.fieldGlossRepo = FieldGlossRepo;

    FieldGlossRepo.listen(function(res) {
        $scope.resetFieldGlosses();
    });

    $scope.fieldGlosses = FieldGlossRepo.getAll();

    $scope.ready = $q.all([FieldGlossRepo.ready()]);

    $scope.dragging = false;

    $scope.trashCanId = 'field-predicates-trash';

    $scope.forms = {};

    $scope.ready.then(function() {

        $scope.resetFieldGlosses = function() {
            FieldGlossRepo.clearValidationResults();
            for(var key in $scope.forms) {
                if($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
                    $scope.forms[key].$setPristine();
                }
            }
            if($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
                $scope.modalData.refresh();
            }
            $scope.modalData = {
                language: 'English'
            };
            $scope.closeModal();
        };

        $scope.resetFieldGlosses();

        $scope.selectFieldGloss = function(index) {
            $scope.resetFieldGlosses();
            var filtereFieldGlosses = $filter('filter')($scope.fieldGlosses, $scope.dragAndDropTextFilterValue[$scope.dragAndDropSelectedFilter]);
            $scope.modalData = filtereFieldGlosses[index];
        };

        $scope.createNewFieldGloss = function() {
            $scope.modalData.documentTypeGloss = false;
            FieldGlossRepo.create($scope.modalData);
        };

        $scope.removeFieldGloss = function() {
            $scope.modalData.delete();
        };

        $scope.updateFieldGloss = function() {
            $scope.modalData.save();
        };

        $scope.launchEditModal = function(fieldGloss) {
            $scope.resetFieldGlosses();
            $scope.modalData = fieldGloss;
            $scope.openModal('#fieldGlossEditModal');
        };

        $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
            trashId: $scope.trashCanId,
            dragging: $scope.dragging,
            select: $scope.selectFieldGloss,
            model: $scope.fieldGlosses,
            confirm: '#fieldGlossConfirmRemoveModal',
            reorder: null,
            container: '#field-predicates'
        });

        var listener = $scope.dragControlListeners.getListener();

        $scope.dragControlListeners.accept = function (sourceItemHandleScope, destSortableScope) {
            var currentElement = destSortableScope.element;
            if(listener.dragging && currentElement[0].id == listener.trash.id) {
                listener.trash.hover = true;
                listener.trash.element = currentElement;
                listener.trash.element.addClass('dragging');
            }
            else {
                listener.trash.hover = false;
            }
            return false;
        };

        $scope.dragControlListeners.orderChanged = function () {};

    });

});
