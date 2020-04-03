vireo.repo("UserRepo", function UserRepo($timeout, TableFactory, User, WsApi) {

    var userRepo = this;

    userRepo.getPageSettings = function () {
        return table.getPageSettings();
    };

    userRepo.getTableParams = function () {
        return table.getTableParams();
    };

    userRepo.fetchPage = function (pageSettings) {
        angular.extend(userRepo.mapping.page, {
            'data': pageSettings ? pageSettings : table.getPageSettings()
        });
        return WsApi.fetch(userRepo.mapping.page);
    };

    var table = TableFactory.buildTable({
        pageNumber: sessionStorage.getItem('users-page') ? sessionStorage.getItem('users-page') : 1,
        pageSize: sessionStorage.getItem('users-size') ? sessionStorage.getItem('users-size') : 10,
        filters: {},
        counts: [5, 10, 25, 50, 100],
        name: 'users',
        repo: userRepo
    });

    userRepo.getAssignableUsers = function(roles) {
        var assignable = [];
        WsApi.fetch(userRepo.mapping.assignable).then(function(response) {
            var resObj = angular.fromJson(response.body);
            if(resObj.meta.status === 'SUCCESS') {
                var users = resObj.payload['ArrayList<User>'];
                for(var i in users) {
                    assignable.push(new User(users[i]));
                }
            }
        });
        return assignable;
    };

    WsApi.listen(userRepo.mapping.createListen).then(null, null, function (response) {
        $timeout(function () {
            userRepo.reset();
            table.getTableParams().reload();
        }, 250);
    });

    WsApi.listen(userRepo.mapping.updateListen).then(null, null, function (response) {
        $timeout(function () {
            userRepo.reset();
            table.getTableParams().reload();
        }, 250);
    });

    WsApi.listen(userRepo.mapping.deleteListen).then(null, null, function (response) {
        $timeout(function () {
            userRepo.reset();
            table.getTableParams().reload();
        }, 250);
    });

    return userRepo;

});
