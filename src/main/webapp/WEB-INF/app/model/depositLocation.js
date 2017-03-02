vireo.model("DepositLocation", function DepositLocation(WsApi) {

    return function DepositLocation() {

        // additional model methods and variables

        var depositLocation = this;

        depositLocation.testConnection = function() {
            angular.extend(depositLocation.getMapping().testConnection, {'data': depositLocation});

            var promise = WsApi.fetch(depositLocation.getMapping().testConnection);

            return promise;
        };

        return depositLocation;
    };

});
