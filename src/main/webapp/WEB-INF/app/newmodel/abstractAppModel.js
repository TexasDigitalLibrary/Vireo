vireo.service("AbstractAppModel", function(AbstractModel2) {

	var AbstractAppModel = function (data) {

		angular.extend(this, new AbstractModel2(data));

		return this;
	};

	return AbstractAppModel;

});