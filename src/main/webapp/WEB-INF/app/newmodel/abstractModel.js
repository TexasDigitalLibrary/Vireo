// TODO: remove AbstractModel and refactor this!!!
vireo.service("AbstractModelNew", function() {

	var AbstractModelNew = function () {
		var abstractModelNew = this;

		abstractModelNew.save = function() {
			console.log('save');
		};

		abstractModelNew.delete = function() {
			console.log('delete');
		};

		abstractModelNew.listen = function() {
			console.log('listen');
		};

		// additional core level model methods and variables
		
		return abstractModelNew;
	}

	return AbstractModelNew;
});