vireo.model("ActiveFilters", function (WsApi) {

	return function ActiveFilters() {
		var activeFilters = this;

		activeFilters.removeFilter = function(filterCriterionId,filterString) {
			angular.extend(apiMapping.ActiveFilters.removeFilter, {
																	"method": "clear-filter-criterion/"+filterCriterionId,
																	"data": {"filterString":filterString}});
			var promise = WsApi.fetch(activeFilters.getMapping().removeFilter);

			return promise;
		}
		
		return activeFilters;
	}

});