describe('controller: AdminController', function() {
	
	var controller, scope, User;

	beforeEach(module('core'));

	beforeEach(module('vireo'));
	
	beforeEach(module('mock.user'));
	
	beforeEach(inject(function($controller, $rootScope, _User_) {
        scope = $rootScope.$new(); 
        controller = $controller('AdminController', {
            $scope: scope,
            User: _User_,
        });
        User = _User_; 
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
		
});
