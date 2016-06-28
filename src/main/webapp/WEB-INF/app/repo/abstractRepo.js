vireo.service("AbstractRepo", function AbstractRepo(WsApi) {

	var AbstractRepo = function (modelConstructor, mapping) {

		var abstractRepo = this;

		var cache = [];
		var promise = WsApi.fetch(mapping.all);

		promise.then(function(res) {
			var resObj = angular.fromJson(res.body).payload.HashMap.list;

			angular.forEach(resObj, function(modelJson) {
				cache.push(new modelConstructor(modelJson));
			});

		});

		abstractRepo.getAll = function() {
			return cache;
		};
		abstractRepo.saveAll = function() {};
		abstractRepo.listen = function() {};
		abstractRepo.findById = function(id) {};
		abstractRepo.deleteById = function(id) {};
		abstractRepo.create = function(model) {};
		abstractRepo.ready = function() {
			return promise;
		};
		abstractRepo.listen = function() {};


		// additiona core level repo methods and variables


		// these should be added through decoration
		abstractRepo.sort = function(facet) {};
		abstractRepo.reorder = function(src, dest) {};

		return abstractRepo;
	}

	return AbstractRepo;
});