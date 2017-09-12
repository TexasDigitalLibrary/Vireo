vireo.service("AbstractAppRepo", function() {

    return function AbstractAppRepo() {

        // additional app level repo methods and variables
        var repo = this;

        var applyPredicateFilter = function(data, predicate) {

            var filteredData = [];

            angular.forEach(data, function(datum) {
                if(predicate(datum)) {
                    filteredData.push(datum);
                }
            });

            return filteredData;
        };

        this.getAllFiltered = function(predicate) {

            var data = repo.getAll();
            var filteredData = [];

            repo.listen(function() {
                filteredData.length = 0;
                angular.extend(filteredData, applyPredicateFilter(data, predicate));
            });

            repo.ready().then(function() {
                angular.extend(filteredData, applyPredicateFilter(data, predicate));
            });

            return filteredData;

        };

        return this;
    };

});
