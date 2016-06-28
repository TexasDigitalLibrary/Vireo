// TODO: remove AbstractModel and refactor this!!!
vireo.service("AbstractModelNew", function() {

	var AbstractModelNew = function (data) {
		var abstractModelNew = this;

		angular.extend(abstractModelNew, data);

		var apiMapping = {};

		abstractModelNew.setApiMapping = function(mapping) {
			angular.extend(apiMapping, mapping);
		};

		abstractModelNew.getApiMapping = function() {
			return apiMapping;
		};

		abstractModelNew.save = function() {};
		abstractModelNew.delete = function() {};
		abstractModelNew.listen = function() {};

		// additional core level model methods and variables
		
		return abstractModelNew;
	}

	return AbstractModelNew;
});