describe('model: User', function() {
	
	var User, WsApi, $rootScope, $scope;

	beforeEach(module('core'));
	
	beforeEach(module('vireo'));
	
	beforeEach(module('mock.wsApi'));
	
	beforeEach(inject(function(_User_, _WsApi_, _$rootScope_) {
        User = _User_;
        WsApi = _WsApi_; 
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
    }));

	describe('model is defined', function() {
		it('should be defined', function() {
			expect(User).toBeDefined();
		});
	});
	
	describe('get method should return a User', function() {
		it('the User was returned', function() {
			var user = User.get();
			$scope.$apply();
			expect(user.content).toEqual(mockUser1);
		});
	});

	describe('set method should set a User', function() {
		it('the User was set', function() {
			var user = User.get();
			$scope.$apply();
			User.set({"unwrap":function(){}, "content":mockUser2});
			expect(user.content).toEqual(mockUser2);
		});
	});

});
