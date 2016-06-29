// TODO: remove AbstractModel and refactor this!!!
vireo.service("AbstractModelNew", function() {

	this.save = function() {
		console.log('save');
	};

	this.delete = function() {
		console.log('delete');
	};

	this.listen = function() {
		console.log('listen');
	};

	// additional core level model methods and variables
	
	return this;
	
});