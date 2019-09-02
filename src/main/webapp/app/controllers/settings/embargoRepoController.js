vireo.controller("EmbargoRepoController", function ($controller, $scope, $q, EmbargoRepo, DragAndDropListenerFactory, $filter) {
    angular.extend(this, $controller("AbstractController", {$scope: $scope}));

    $scope.embargoRepo = EmbargoRepo;

    $scope.embargoes = EmbargoRepo.getAll();

    $scope.ready = $q.all([EmbargoRepo.ready()]);

    $scope.dragging = false;

    $scope.trashCanId = 'embargo-trash';

    $scope.sortAction = "confirm";

    $scope.sortDefault = "sortDefaultEmbargoes";
    $scope.sortProquest = "sortProquestEmbargoes";

    $scope.sortLabel = "";

    $scope.forms = {};

    $scope.ready.then(function() {

        $scope.resetEmbargo = function() {
            $scope.embargoRepo.clearValidationResults();
            for(var key in $scope.forms) {
                if($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
                        $scope.forms[key].$setPristine();
                        $scope.forms[key].$setUntouched();
                }
            }
            if($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
                $scope.modalData.refresh();
            }
            $scope.modalData = {
                isActive: false,
                duration: 12
            };
            $scope.proquestEmbargoes = $filter('filter')($scope.embargoes, {guarantor: "PROQUEST"});
            $scope.defaultEmbargoes = $filter('filter')($scope.embargoes, {guarantor: "DEFAULT"});
            $scope.closeModal();
        };

        $scope.resetEmbargo();

        $scope.createEmbargo = function() {
            EmbargoRepo.create($scope.modalData);
        };

        $scope.selectEmbargo = function(id) {
            $scope.modalData = $filter('filter')($scope.embargoes, {id: id})[0];
        };

        $scope.editEmbargo = function(id) {
            $scope.selectEmbargo(id);
            $scope.openModal('#embargoEditModal');
        };

        $scope.updateEmbargo = function() {
            // when systemRequired is TRUE, then only the isActive property can be updated.
            // in these cases, use the custom end-points for updating the isActive state.
            if ($scope.isSystemRequired($scope.modalData)) {
                if ($scope.modalData.isActive) {
                    EmbargoRepo.activate($scope.modalData);
                } else {
                    EmbargoRepo.deactivate($scope.modalData);
                }
            }
            else {
                $scope.modalData.save();
            }
        };

        $scope.removeEmbargo = function() {
            $scope.modalData.delete();
        };

        $scope.isSystemRequired = function(modalData) {
            if (angular.isDefined(modalData) && angular.isDefined(modalData.systemRequired)) {
                return modalData.systemRequired === true;
            }

            return false;
        };

        $scope.reorderEmbargoDefault = function(src, dest) {
            return EmbargoRepo.reorder("DEFAULT", src, dest);
        };

        $scope.reorderEmbargoProquest = function(src, dest) {
            return EmbargoRepo.reorder("PROQUEST", src, dest);
        };

        $scope.sortEmbargoesDefault = function(column) {
            if($scope.sortAction != $scope.sortDefault && $scope.sortAction != $scope.sortProquest) {
                $scope.sortAction = $scope.sortDefault;
            }
            else if($scope.sortAction == $scope.sortDefault || $scope.sortAction == $scope.sortProquest) {
                EmbargoRepo.sort("DEFAULT", column);
                $scope.sortAction = 'confirm';
            }
        };

        $scope.sortEmbargoesProquest = function(column) {
            if($scope.sortAction != $scope.sortDefault && $scope.sortAction != $scope.sortProquest) {
                $scope.sortAction = $scope.sortProquest;
            }
            else if($scope.sortAction == $scope.sortDefault || $scope.sortAction == $scope.sortProquest) {
                EmbargoRepo.sort("PROQUEST", column);
                $scope.sortAction = 'confirm';
            }
        };

        $scope.dragControlListenersDefault = DragAndDropListenerFactory.buildDragControls({
            trashId: $scope.trashCanId,
            dragging: $scope.dragging,
            select: $scope.selectEmbargo,
            model: $scope.embargoes,
            confirm: '#embargoConfirmRemoveModal',
            reorder: $scope.reorderEmbargoDefault,
            sortLabel: $scope.sortLabel,
            container: '#embargoes'
        });

        $scope.dragControlListenersProquest = DragAndDropListenerFactory.buildDragControls({
            trashId: $scope.trashCanId,
            dragging: $scope.dragging,
            select: $scope.selectEmbargo,
            model: $scope.embargoes,
            confirm: '#embargoConfirmRemoveModal',
            reorder: $scope.reorderEmbargoProquest,
            sortLabel: $scope.sortLabel,
            container: '#embargoes'
        });

        EmbargoRepo.listen(function(data) {
            $scope.resetEmbargo();
        });

    });
});
