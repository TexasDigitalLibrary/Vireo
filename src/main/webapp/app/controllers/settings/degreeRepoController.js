vireo.controller("DegreeRepoController", function ($controller, $scope, $q, DegreeRepo, DegreeLevelRepo, DragAndDropListenerFactory) {

    angular.extend(this, $controller("AbstractController", {
        $scope: $scope
    }));

    $scope.degreeRepo = DegreeRepo;

    $scope.degrees = DegreeRepo.getAll();

    $scope.degreeLevels = DegreeLevelRepo.getAll();

    $scope.ready = $q.all([DegreeLevelRepo.ready(), DegreeRepo.ready()]);

    $scope.dragging = false;

    $scope.trashCanId = 'degree-trash';

    $scope.sortAction = "confirm";

    $scope.forms = {};

    $scope.ready.then(function () {

        $scope.resetDegree = function () {
            $scope.degreeRepo.clearValidationResults();
            for (var key in $scope.forms) {
                if ($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
                    $scope.forms[key].$setPristine();
                    $scope.forms[key].$setUntouched();
                }
            }

            if ($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
                $scope.modalData.refresh();
            }

            $scope.modalData = {
                level: $scope.degreeLevels.length > 0 ? $scope.degreeLevels[0] : ''
            };

            $scope.closeModal();
        };

        $scope.resetDegree();

        $scope.createDegree = function () {
            DegreeRepo.create($scope.modalData);
        };

        $scope.selectDegree = function (index) {
            $scope.modalData = $scope.degrees[index];
        };

        $scope.editDegree = function (index) {
            $scope.selectDegree(index - 1);
            $scope.openModal('#degreeEditModal');
        };

        $scope.updateDegree = function () {
            $scope.modalData.save();
        };

        $scope.removeDegree = function () {
            $scope.modalData.delete();
        };

        $scope.confirmRemoveAllDegrees = function () {
            $scope.openModal("#degreeConfirmRemoveAllModal");
        };

        $scope.removeAllDegrees = function () {
            DegreeRepo.removeAll().then(function (res){
            });
        };
        
        $scope.reorderDegree = function (src, dest) {
            return DegreeRepo.reorder(src, dest);
        };

        $scope.sortDegrees = function (column) {
            if ($scope.sortAction == 'confirm') {
                $scope.sortAction = 'sort';
            } else if ($scope.sortAction == 'sort') {
                DegreeRepo.sort(column);
                $scope.sortAction = 'confirm';
            }
        };

        $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
            trashId: $scope.trashCanId,
            dragging: $scope.dragging,
            select: $scope.selectDegree,
            model: $scope.degrees,
            confirm: '#degreeConfirmRemoveModal',
            reorder: $scope.reorderDegree,
            container: '#degrees-container'
        });

        DegreeRepo.listen(function (data) {
            $scope.resetDegree();
        });

    });

});
