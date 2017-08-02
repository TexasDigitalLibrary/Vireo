vireo.directive("dropzone", function ($timeout) {
    return {
        templateUrl: 'views/directives/dropZone.html',
        restrict: 'E',
        scope: {
            'id': '@',
            'text': '@',
            'patterns': '@',
            'maxFiles': '@',
            'allowMultiple': '@',
            'dropMethod': '&',
            'fileModel': '='
        },
        link: function ($scope) {

            $scope.fileValidationError = false;

            $scope.dropMethodWrapper = function (data) {
                $scope.fileValidationError = data.files.length === 0;
                if ($scope.fileValidationError) {
                    $timeout(function () {
                        $scope.fileValidationError = false;
                    }, 3000);
                }
                $scope.dropMethod(data);
            };

            $scope.dragging = function () {
                return "dragging-accept";
            };
        }
    };
});
