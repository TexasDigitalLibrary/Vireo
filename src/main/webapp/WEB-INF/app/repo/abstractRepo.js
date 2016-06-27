vireo.service("AbstractRepo", function() {

	var AbstractRepo = function (constructor) {

		var cache = [];

		this.getCache = function() {
			return cache;
		};

		this.setOnCache = function(obj) {
			cache.push(new constructor(obj));
		};
		
		this.getAll = function() {};
		this.saveAll = function() {};
		this.listen = function() {};
		this.findById = function(id) {};
		this.deleteById = function(id) {};
		this.create = function(model) {};

		//these should be added through decoration
		this.sort = function(facet) {};
		this.reorder = function(src, dest) {};

		return this;
	}

	return AbstractRepo;
});