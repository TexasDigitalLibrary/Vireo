vireo.repo("ConfigurationRepo", function ConfigurationRepo() {

    var configurations = {};

    // additional repo methods and variables

    this.reset = function(model) {
        return model.reset();
    };

    this.getAllMapByType = function() {

        var configurationRepo = this;

        var allConfigurations = configurationRepo.getAll();

        var mapByType = function(configurations) {
            angular.forEach(allConfigurations, function(config) {
                if (configurations[config.type] === undefined) {
                    configurations[config.type] = {};
                }
                configurations[config.type][config.name] = config;
            });
        }

        this.ready().then(function() {
            mapByType(configurations);
        });

        this.listen(function() {
            mapByType(configurations);
        });

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

    return this;

});
