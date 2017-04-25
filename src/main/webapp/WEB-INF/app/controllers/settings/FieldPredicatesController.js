vireo.controller('FieldPredicatesController', function ($controller, $scope, $q, SidebarService, DragAndDropListenerFactory, FieldPredicateRepo) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    SidebarService.addBoxes([]);

    $scope.fieldPredicates = FieldPredicateRepo.getAll();

    FieldPredicateRepo.listen(function() {
        $scope.resetFieldPredicates();
    });

    $scope.ready = $q.all([FieldPredicateRepo.ready()]);

    $scope.dragging = false;

    $scope.trashCanId = 'field-predicates-trash';

    $scope.forms = {};

    $scope.ready.then(function() {

        $scope.resetFieldPredicates = function() {
            FieldPredicateRepo.clearValidationResults();
            for(var key in $scope.forms) {
                if($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
                    $scope.forms[key].$setPristine();
                }
            }
            if($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
                $scope.modalData.refresh();
            }
            $scope.modalData = {};
            $scope.closeModal();
        };

        $scope.resetFieldPredicates();

        $scope.selectFieldPredicate = function(index) {
            $scope.resetFieldPredicates();
            $scope.modalData = $scope.resetFieldPredicates[index];
        };

        $scope.createNewFieldPredicate = function() {
           FieldPredicateRepo = $scope.modalData;
        };

        $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
            trashId: $scope.trashCanId,
            dragging: $scope.dragging,
            select: $scope.selectFieldPredicate,
            model: $scope.fieldPredicates,
            confirm: '#fieldPredicatesConfirmRemoveModal',
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



        $scope.dragControlListeners.orderChanged = function (event) {};

    });

});
