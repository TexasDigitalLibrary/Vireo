vireo.directive('dehashcolor', function() {
    return {
        require: 'ngModel',
        link: function(scope, element, attrs, ngModelController) {
            ngModelController.$parsers.push(function(fromView) {

                var toModel = "";
                if(fromView[0] != "#") {
                    toModel = "#" + fromView;
                } else {
                    toModel = fromView;
                }

                return toModel; //converted

            });

            ngModelController.$formatters.push(function(fromModel) {

                var toView = "";
                if(!fromModel) fromModel = "";

                if(fromModel[0] == "#") toView = fromModel.substring(1, fromModel.length);

                return toView; //converted

            });
        }
    }
});