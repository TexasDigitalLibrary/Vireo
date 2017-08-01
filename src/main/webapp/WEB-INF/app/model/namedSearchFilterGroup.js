vireo.model("NamedSearchFilterGroup", function(WsApi) {

    return function NamedSearchFilterGroup() {
        var namedSearchFilterGroup = this;

        namedSearchFilterGroup.set = function(filter) {

            angular.extend(apiMapping.NamedSearchFilterGroup.setFilter, {"data": filter});

            var promise = WsApi.fetch(namedSearchFilterGroup.getMapping().setFilter);

            return promise;
        };

        namedSearchFilterGroup.addFilter = function(criterionName, filterValue, filterGloss, exactMatch) {

            angular.extend(apiMapping.NamedSearchFilterGroup.addFilter, {
                "data": {
                    "criterionName": criterionName,
                    "filterValue": filterValue,
                    "filterGloss": filterGloss,
                    "exactMatch": exactMatch
                }
            });

            var promise = WsApi.fetch(namedSearchFilterGroup.getMapping().addFilter);

            return promise;

        };

        namedSearchFilterGroup.removeFilter = function(namedSearchFilterName, filterCriterion) {

            angular.extend(apiMapping.NamedSearchFilterGroup.removeFilter, {
                'method': 'remove-filter-criterion/' + namedSearchFilterName,
                "data": filterCriterion
            });

            var promise = WsApi.fetch(namedSearchFilterGroup.getMapping().removeFilter);

            return promise;
        };

        namedSearchFilterGroup.clearFilters = function() {
            var promise = WsApi.fetch(namedSearchFilterGroup.getMapping().clearFilters);
            return promise;
        };

        return namedSearchFilterGroup;
    };

});
