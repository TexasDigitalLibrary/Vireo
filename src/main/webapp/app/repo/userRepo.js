vireo.repo("UserRepo", function UserRepo(User, WsApi) {

    var userRepo = this;

    userRepo.fetchPage = function (pageSettings) {
        var endpoint = angular.extend({}, userRepo.mapping.page, { data: pageSettings });

        return WsApi.fetch(endpoint);
    };

    userRepo.getAssignableUsers = function(page, size, name) {
        var assignable = [];

        if (size > 0) {
            angular.extend(userRepo.mapping.assignable, {
                query: {
                  page: page,
                  size: size,
                  name: name
                }
            });
        }

        WsApi.fetch(userRepo.mapping.assignable).then(function(response) {
            var resObj = angular.fromJson(response.body);
            if (resObj.meta.status === 'SUCCESS') {
                var users = resObj.payload['ArrayList<User>'];
                for (var i in users) {
                    assignable.push(new User(users[i]));
                }
            }
        });

        return assignable;
    };

    userRepo.getAssignableUsersTotal = function(name) {
        var total = 0;

        if (angular.isDefined(name)) {
            angular.extend(userRepo.mapping.assignableTotal, {
                query: {
                  name: name
                }
            });
        }

        return WsApi.fetch(userRepo.mapping.assignableTotal);
    };

    userRepo.getUnassignableUsers = function(page, size, name) {
        var unassignable = [];

        if (size > 0) {
            angular.extend(userRepo.mapping.unassignable, {
                query: {
                  page: page,
                  size: size,
                  name: name
                }
            });
        }

        WsApi.fetch(userRepo.mapping.unassignable).then(function(response) {
            var resObj = angular.fromJson(response.body);

            if (resObj.meta.status === 'SUCCESS') {
                var users = resObj.payload['ArrayList<User>'];

                for (var i in users) {
                    unassignable.push(new User(users[i]));
                }
            }
        });

        return unassignable;
    };

    userRepo.getUnassignableUsersTotal = function(name) {
        var total = 0;

        if (angular.isDefined(name)) {
            angular.extend(userRepo.mapping.unassignableTotal, {
                query: {
                  name: name
                }
            });
        }

        return WsApi.fetch(userRepo.mapping.unassignableTotal);
    };

    return userRepo;

});
