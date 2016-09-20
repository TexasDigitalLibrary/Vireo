vireo.model("NamedSearchFilter", function (WsApi) {

	return function NamedSearchFilter() {
		var namedSearchFilter = this;

		namedSearchFilter.removeFilter = function(filterCriterionId,filterString) {
			angular.extend(apiMapping.NamedSearchFilter.removeFilter, {
				"method": "clear-filter-criterion/"+filterCriterionId,
				"data": {"filterString":filterString}
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