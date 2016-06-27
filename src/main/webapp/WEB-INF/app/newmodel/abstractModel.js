vireo.service("AbstractModel2", function() {

	var AbstractModel2 = function (data) {

		angular.extend(this, data);

		this.save = function() {};
		this.delete = function() {};
		this.listen = function() {};
		
		return this;
	}

	return AbstractModel2;
});