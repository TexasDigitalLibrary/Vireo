vireo.repo("UserRepo", function UserRepo(User, WsApi) {

    var userRepo = this;

    userRepo.getAllByRole = function(roles) {
        var userList = [];
        angular.forEach(userRepo.getAll(), function(user) {
            if(roles.indexOf(user.role) != -1) {
                userList.push(user);
            }
        });
        return userList;
    };

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

    return userRepo;

});