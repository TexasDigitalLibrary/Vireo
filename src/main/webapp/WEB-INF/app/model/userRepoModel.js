vireo.service("UserRepo", function($route, WsApi, AbstractModel, StorageService) {

	var self;
	
	var Users = function(futureData) {
		self = this;

		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);
		
		self.unwrap(self, futureData);
		
	};
	
	Users.data = null;
	
	Users.listener = null;

	Users.promise = null;
	
	Users.set = function(data) {
		self.unwrap(self, data);
	};

	Users.get = function() {

		if(Users.promise) return Users.data;

		var newAllUsersPromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'user', 
				method: 'all',
		});

		Users.promise = newAllUsersPromise;

		if(Users.data) {
			newAllUsersPromise.then(function(data) {
				Users.set(JSON.parse(data.body).payload.HashMap);
			});
		}
		else {
			Users.data = new Users(newAllUsersPromise);	
		}
		
		Users.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'users', 
			method: '',
		});
				
		Users.set(Users.listener);

		return Users.data;
	
	};
	
	Users.updateRole = function(user, userToEdit) {
		
		var updateUserRolePromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'user', 
			method: 'update-role',
			data: userToEdit
		});

		if(updateUserRolePromise.$$state) {
			updateUserRolePromise.then(function(data) {				
				if(user.email == userToEdit.email) {
					StorageService.set("role", userToEdit.role);
					if(userToEdit.role == 'ROLE_STUDENT' || userToEdit.role == 'ROLE_REVIEWER') {
						$location.path('/myprofile');
					}
					else {
						$route.reload();
					}
				}
			});
		}

		return updateUserRolePromise;	
	};

	Users.ready = function() {
		return Users.promise;
	};

	Users.refresh = function() {
		Users.promise = null;
		Users.get();
	};
	
	Users.listen = function() {
		return Users.listener;
	};
	
	return Users;
	
});
