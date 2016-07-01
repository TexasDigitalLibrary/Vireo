vireo.service("AbstractRepo", function ($q, WsApi) {

	return function AbstractRepo(model, mapping) {

		var abstractRepo = this;

		abstractRepo.mapping = mapping;

		var cache = [];

		var initialized = false;

		var defer = $q.defer();

		var listenCallbacks = [];


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

		WsApi.fetch(abstractRepo.mapping.all).then(function(res) {
			build(unwrap(res)).then(function() {
				defer.resolve();
			});
		});

		WsApi.listen(abstractRepo.mapping.listen).then(null, null, function(res) {
			build(unwrap(res)).then(function() {
				angular.forEach(listenCallbacks, function(cb) {
					cb();
				});
			});
		});

		abstractRepo.getAll = function() {
			return cache;
		};

		abstractRepo.saveAll = function() {
			angular.forEach(cache, function(model) {
				model.save();
			});
		};

		abstractRepo.ready = function() {
			return defer.promise;
		};

		abstractRepo.findById = function(id) {
			var match;
			
			var find = function(id) {
				for(var key in cache) {
					if(cache[key].id == id) {
						return cache[key];
					}
				}
			}

			if(initialized) {
				match = find(id);
			}
			else {
				// TODO: think of a way to find after ready and have binding in cache
				console.error("Repo not initialized!");
			}

			return match;
		};

		// TODO: remove
		abstractRepo.delete = function(model) {
			return model.delete();
		};

		abstractRepo.deleteById = function(id) {
			return abstractRepo.findById(id).delete();
		};

		abstractRepo.create = function(model) {
			angular.extend(abstractRepo.mapping.create, {'data': model});
			return WsApi.fetch(abstractRepo.mapping.create);
		};

		// TODO: remove
		abstractRepo.update = function(model) {
           return model.save();
		};

		this.listen = function(cb) {
			listenCallbacks.push(cb);
		};

		// additiona core level repo methods and variables

		// these should be added through decoration
		abstractRepo.sort = function(facet) {
			angular.extend(abstractRepo.mapping.sort, {'method': 'sort/' + facet});
			return WsApi.fetch(abstractRepo.mapping.sort);
		};

		abstractRepo.reorder = function(src, dest) {
			angular.extend(abstractRepo.mapping.reorder, {'method': 'reorder/' + src + '/' + dest});
			return WsApi.fetch(abstractRepo.mapping.reorder);
		};

		return abstractRepo;
	}

});
