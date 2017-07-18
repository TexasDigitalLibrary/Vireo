vireo.repo("ConfigurationRepo", function ConfigurationRepo(Configuration, WsApi) {

    var configurationRepo = this;

    var configurations = {};

    var listening = false;

    // additional repo methods and variables

    var getAndListen = function() {
        var allConfigurations = configurationRepo.getAll();

        var mapByType = function() {
            angular.forEach(allConfigurations, function(config) {
                if (configurations[config.type] === undefined) {
                    configurations[config.type] = {};
                }
                configurations[config.type][config.name] = config;
            });
        };

        configurationRepo.ready().then(function() {
            mapByType();
            WsApi.listen(configurationRepo.mapping.selectiveListen).then(null, null, function(response) {
                var config = new Configuration(angular.fromJson(response.body).payload.Configuration);
                configurations[config.type][config.name] = config;
            });
        });

        configurationRepo.listen(function() {
            mapByType();
        });

        listening = true;
    };

    this.reset = function(model) {
        return model.reset();
    };

    this.getAllMapByType = function() {
        if (!listening) {
            getAndListen();
        }
        return configurations;
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

    this.findByTypeAndNamePreferOverride = function(type, name) {

        var configuration;

        var list = this.getAll();

        for (var i in list) {
            var config = list[i];
            if (config.type == type && config.name == name) {
                if (config.isSystemRequired == true && typeof configuration === 'undefined') {
                    configuration = config;
                } else if (config.isSystemRequired == false) {
                    configuration = config;
                }
            }
        }

        return configuration;
    };

    return this;

});
