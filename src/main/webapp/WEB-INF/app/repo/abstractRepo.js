vireo.service("AbstractRepo", function ($q, WsApi) {

	var AbstractRepo = function (model, mapping) {

		var abstractRepo = this;

		var cache = [];

		var initialized = false;

		var defer = $q.defer();

		var build = function(data) {
			initialized = false;
			return $q(function(resolve) {
				cache.length = 0;
				angular.forEach(data, function(modelJson) {
					cache.push(new model(modelJson));
				});
				initialized = true
				resolve();
			});
		};

		var unwrap = function(res) {
			var repoObj = {};
			var payload = angular.fromJson(res.body).payload;
			var keys = Object.keys(payload);
			angular.forEach(keys, function(key) {
				angular.extend(repoObj, payload[key]);
			})
			return repoObj;
		};

		WsApi.fetch(mapping.all).then(function(res) {
			build(unwrap(res)).then(function() {
				defer.resolve();
			});
		});

		WsApi.listen(mapping.listen).then(null, null, function(res) {
			build(unwrap(res));
		});

		abstractRepo.getAll = function() {
			return cache;
		};

		abstractRepo.saveAll = function() {

		};

		abstractRepo.ready = function() {
			return defer.promise;
		};

		abstractRepo.findById = function(id) {
			var response = {};

			var find = function(id) {
				for(var key in cache) {
					if(cache[key].id == id) {
						return cache[key];
					}
				}
			}

			if(initialized) {
				angular.extend(response, find(id));
			}
			else {
				abstractRepo.ready().then(function() {
					angular.extend(response, find(id));
				});
			}

			return response;
		};

		abstractRepo.delete = function(model) {
			return model.delete();
		};

		abstractRepo.deleteById = function(id) {
			angular.extend(mapping.remove,  {'method': 'remove/' + id});
			return WsApi.fetch(mapping.remove);
		};

		abstractRepo.create = function(model) {
			angular.extend(mapping.create, {'data': model});
			return WsApi.fetch(mapping.create);
		};

		abstractRepo.save = function(model) {
			return model.save();
		};

		abstractRepo.update = function(data) {
			angular.extend(mapping.update, {'data': data});
			return WsApi.fetch(mapping.update);
		};

		// additiona core level repo methods and variables


		// these should be added through decoration
		abstractRepo.sort = function(facet) {
			angular.extend(mapping.sort, {'method': 'sort/' + facet});
			return WsApi.fetch(mapping.sort);
		};

		abstractRepo.reorder = function(src, dest) {
			angular.extend(mapping.reorder, {'method': 'reorder/' + src + '/' + dest});
			return WsApi.fetch(mapping.reorder);
		};

		return abstractRepo;
	}

	return AbstractRepo;
});