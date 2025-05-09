vireo.repo("ManagedConfigurationRepo", function ManagedConfigurationRepo($q, ManagedConfiguration, WsApi) {

    var configurationRepo = this;

    var configurations = {};

    var defer = $q.defer();

    var fetching = false;

    // additional repo methods and variables

    var fetch = function (mapping) {
        if (mapping.all !== undefined) {
            fetching = true;
            return WsApi.fetch(mapping.all).then(function (res) {
                build(unwrap(res)).then(function () {
                    fetching = false;
                    defer.resolve(configurations);
                });
            });
        }
    };

    var build = function (data) {
        return $q(function (resolve) {
            angular.extend(configurations, data);
            resolve();
        });
    };

    var unwrap = function (res) {
        var repoObj = {};
        var payload = angular.fromJson(res.body).payload;
        var keys = Object.keys(payload);
        angular.forEach(keys, function (key) {
            angular.forEach(payload[key],function (configurations, type) {
                repoObj[type] = {};
                angular.forEach(configurations, function (config) {
                    repoObj[type][config.name] = new ManagedConfiguration(config);

                });
            });
        });
        return repoObj;
    };

    configurationRepo.getAll = function () {
        if (this.mapping.lazy && !fetching) {
            fetch(this.mapping);
        }
        return configurationRepo.getContents();
    };

    configurationRepo.getAllShibbolethConfigurations = function () {
        return configurationRepo.getAll().shibboleth;
    };

    configurationRepo.getContents = function () {
        return configurations;
    };

    configurationRepo.ready = function () {
        return defer.promise;
    };

    configurationRepo.ready().then(function() {
        WsApi.listen(configurationRepo.mapping.selectiveListen).then(null, null, function(response) {
            var payload = angular.fromJson(response.body).payload;
            var config = {};
            if (payload.ManagedConfiguration) {
                config = new ManagedConfiguration(payload.ManagedConfiguration);
            } else {
                config = new ManagedConfiguration(payload.DefaultConfiguration);
            }
            configurations[config.type][config.name] = config;
        });
    });

    configurationRepo.reset = function(model) {
        return model.reset();
    };

    configurationRepo.findByTypeAndName = function(type, name) {

        var configuration;

        var list = configurationRepo.getAll();

        for (var i in list) {
            var config = list[i];
            if (config.type == type && config.name == name) {
                configuration = config;
                break;
            }
        }

        return configuration;
    };

    return configurationRepo;

});
