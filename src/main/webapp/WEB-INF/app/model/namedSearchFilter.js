vireo.model("NamedSearchFilter", function NamedSearchFilter(WsApi) {

	return function NamedSearchFilter() {
		var namedSearchFilter = this;

		namedSearchFilter.set = function(filter) {

			angular.extend(apiMapping.NamedSearchFilter.setFilter, {
				"data": filter
			});

			var promise = WsApi.fetch(namedSearchFilter.getMapping().setFilter);

			return promise;
		};

		namedSearchFilter.removeFilter = function(criterionName, filterValue) {

			angular.extend(apiMapping.NamedSearchFilter.removeFilter, {
				"data": {
					"criterionName":criterionName,
					"filterValue":filterValue
				}
			});

			var promise = WsApi.fetch(namedSearchFilter.getMapping().removeFilter);

			return promise;
		};

		namedSearchFilter.clearFilters = function() {
			var promise = WsApi.fetch(namedSearchFilter.getMapping().clearFilters);
			return promise;
		};

		return namedSearchFilter;
	};

});