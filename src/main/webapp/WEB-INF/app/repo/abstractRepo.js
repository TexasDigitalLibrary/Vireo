vireo.service("AbstractRepo", function AbstractRepo() {

	var AbstractRepo = function (constructor) {

		var abstractRepo = this;

		var cache = [];
		var ready = false;
		var promise = null;

		abstractRepo.getAll = function() {};
		abstractRepo.saveAll = function() {};
		abstractRepo.listen = function() {};
		abstractRepo.findById = function(id) {};
		abstractRepo.deleteById = function(id) {};
		abstractRepo.create = function(model) {};
		abstractRepo.ready = function() {};
		abstractRepo.listen = function() {};


		// additiona core level repo methods and variables


		// these should be added through decoration
		abstractRepo.sort = function(facet) {};
		abstractRepo.reorder = function(src, dest) {};

		return abstractRepo;
	}

	return AbstractRepo;
});