describe('controller: UserRepoController', function() {
	
	var controller, location, scope, User, UserRepo;

	beforeEach(module('core'));

	beforeEach(module('seedApp'));
	
	beforeEach(module('mock.user'));
	beforeEach(module('mock.userRepo'));
	
	beforeEach(inject(function($controller, $location, $rootScope, _User_, _UserRepo_) {
        scope = $rootScope.$new();
        location = $location;
        controller = $controller('UserRepoController', {
            $scope: scope,
            $location: location,
            User: _User_,
            UserRepo: _UserRepo_
        });
        User = _User_; 
        UserRepo = _UserRepo_;
    }));

	describe('Is the controller defined', function() {
		it('should be defined', function() {
			expect(controller).toBeDefined();
		});
	});
	
	describe('Is the scope defined', function() {
		it('should be defined', function() {
			expect(scope).toBeDefined();
		});
	});
	
	describe('Does the scope have a User', function() {
		it('User should be on the scope', function() {
			expect(scope.user).toBeDefined();
		});
	});
	
	describe('Does the User have expected credentials', function() {
		it('User should have expected credentials', function() {
			expect(scope.user).toEqual(mockUser1);
		});
	});
	
	describe('Should be able to set a User', function() {
		it('should have set the User', function() {			
			User.set(mockUser2)			
			expect(scope.user).toEqual(mockUser2);
		});
	});
	
	describe('Should be able to fetch a User', function() {		
		it('should have set the fetched User', function() {			
			User.fetch().then(function(data) {
				User.set(data);
				expect(scope.user).toEqual(mockUser3);
			});
		});		
	});	

	describe('Does the scope have a UserRepo', function() {
		it('UserRepo should be on the scope', function() {
			expect(scope.userRepo).toBeDefined();
		});
	});
	
	describe('Does the UserRepo have expected users credentials', function() {
		it('UserRepo should have expected users credentials', function() {
			expect(scope.userRepo).toEqual(mockUserRepo1);
		});
	});
	
	describe('Should be able to set a UserRepo', function() {
		it('should have set the UserRepo', function() {			
			UserRepo.set(mockUserRepo2)			
			expect(scope.userRepo).toEqual(mockUserRepo2);
		});
	});
	
	describe('Should be able to fetch a UserRepo', function() {		
		it('should have set the fetched UserRep', function() {			
			UserRepo.fetch().then(function(data) {
				UserRepo.set(data);
				expect(scope.userRepo).toEqual(mockUserRepo3);
			});
		});		
	});	
		
});
