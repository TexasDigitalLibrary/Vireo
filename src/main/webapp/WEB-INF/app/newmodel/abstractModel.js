// TODO: remove AbstractModel and refactor this!!!
vireo.service("AbstractModel2", function() {

	var AbstractModel2 = function (data) {
		var abstractModel2 = this;

		angular.extend(abstractModel2, data);

		abstractModel2.save = function() {};
		abstractModel2.delete = function() {};
		abstractModel2.listen = function() {};

		// additional core level model methods and variables
		
		return abstractModel2;
	}

	return AbstractModel2;
});