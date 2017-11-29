vireo.repo("UserRepo", function UserRepo() {

	var userRepo = this;

	userRepo.getAllByRole = function(roles) {

		var userList = [];
		
		angular.forEach(userRepo.getAll(), function(user) {
			if(roles.indexOf(user.role) != -1) userList.push(user);
		});

		return userList;

	};

	return userRepo;

});