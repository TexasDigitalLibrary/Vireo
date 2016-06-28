// TODO: remove AbstractModel and refactor this!!!
vireo.service("AbstractModelNew", function() {

	var AbstractModelNew = function (mapping, data) {
		var abstractModelNew = this;

		angular.extend(abstractModelNew, data);

		abstractModelNew.save = function() {};
		abstractModelNew.delete = function() {};
		abstractModelNew.listen = function() {};

		// additional core level model methods and variables
		
		return abstractModelNew;
	}

	return AbstractModelNew;
});