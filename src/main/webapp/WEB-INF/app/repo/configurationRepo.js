vireo.repo("ConfigurationRepo", function ConfigurationRepo($q,Configuration, WsApi) {

    var configurationRepo = this;

    var configurations = {};

    var listening = false;

    var defer = $q.defer();

    // additional repo methods and variables

    var fetch = function (mapping) {
        if (mapping.all !== undefined) {
            return WsApi.fetch(mapping.all).then(function (res) {
                build(unwrap(res)).then(function () {
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
            angular.forEach(payload[key],function (configurations,type) {
                repoObj[type] = {};
                angular.forEach(configurations, function (config) {
                    repoObj[type][config.name] = new Configuration(config);

                });
            });
        });
        return repoObj;
    };

    configurationRepo.getAll = function () {
        if (this.mapping.lazy) {
            fetch(this.mapping);
        }
        return configurationRepo.getContents();
    };

    configurationRepo.getContents = function () {
        return configurations;
    };

    configurationRepo.ready = function () {
        return defer.promise;
    };

    configurationRepo.ready().then(function() {
        WsApi.listen(configurationRepo.mapping.selectiveListen).then(null, null, function(response) {
            var config = new Configuration(angular.fromJson(response.body).payload.ManagedConfiguration);
            configurations[config.type][config.name] = config;
        });
    });

    this.reset = function(model) {
        return model.reset();
    };

    this.findByTypeAndName = function(type, name) {

        var configuration;

        var list = this.getAll();

        for (var i in list) {
            var config = list[i];
            if (config.type == type && config.name == name) {
                configuration = config;
                break;
            }
        }

        return configuration;
    };

    return this;

});
