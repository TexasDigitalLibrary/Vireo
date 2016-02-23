describe('model: DepositLocationRepo', function() {
	
	var DepositLocationRepo, WsApi, $rootScope, $scope;

	beforeEach(module('core'));
	
	beforeEach(module('vireo'));
	
	beforeEach(module('mock.wsApi'));
	
	beforeEach(inject(function(_DepositLocationRepo_, _WsApi_, _$rootScope_) {
        DepositLocationRepo = _DepositLocationRepo_;
        WsApi = _WsApi_; 
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
    }));

	describe('model is defined', function() {
		it('should be defined', function() {
			expect(DepositLocationRepo).toBeDefined();
		});
	});
	
	describe('get method should return a DepositLocationRepo', function() {
		it('the DepositLocationRepo was returned', function() {
			
		});
	});

	describe('set method should set a DepositLocationRepo', function() {
		it('the DepositLocationRepo was set', function() {
			
		});
	});
	
	describe('update method should udpate a depositLocation in the DepositLocationRepo', function() {
		it('the depositLocation was updated in the DepositLocationRepo', function() {
			
		});
	});
	
	describe('add method should add a depositLocation in the DepositLocationRepo', function() {
		it('the depositLocation was added in the DepositLocationRepo', function() {
			
		});
	});
	
	describe('add method should reorded depositLocation in the DepositLocationRepo', function() {
		it('the depositLocations were reorded in the DepositLocationRepo', function() {
			
		});
	});
	
});
