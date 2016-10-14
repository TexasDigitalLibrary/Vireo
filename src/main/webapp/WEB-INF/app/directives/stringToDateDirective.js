vireo.directive('stringtodate', function() {
    return {
        require: 'ngModel',
        link: function(scope, element, attrs, ngModelController) {
            ngModelController.$formatters.push(function(fromModel) {
                return Date.parse(fromModel); //converted
            });
        }
    };
});