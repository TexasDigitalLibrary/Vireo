vireo.repo("DepositLocationRepo", function DepositLocationRepo(DepositLocation, WsApi) {

    // additional repo methods and variables

    var depositLocationRepo = this;

    depositLocationRepo.testConnection = function (data) {
        var connection = angular.copy(new DepositLocation().getMapping().testConnection);
        connection.data = data;

        return WsApi.fetch(connection);
    };

    return depositLocationRepo;

});
