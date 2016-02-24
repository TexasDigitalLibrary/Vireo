describe('controller: DepositLocationRepoController', function() {
	
	var controller, scope, DepositLocationRepo;

	beforeEach(module('core'));

	beforeEach(module('vireo'));
	
	beforeEach(module('mock.depositLocationRepo'));
	
	beforeEach(inject(function($controller, $rootScope, _DepositLocationRepo_) {
        scope = $rootScope.$new(); 
        controller = $controller('DepositLocationRepoController', {
            $scope: scope,
            DepositLocationRepo: _DepositLocationRepo_
        });
        DepositLocationRepo = _DepositLocationRepo_; 
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
	
	describe('Does the scope have a DepositLocationRepo', function() {
		it('DepositLocationRepo should be on the scope', function() {
			expect(scope.depositLocations).toBeDefined();
		});
	});
	
	describe('Does the DepositLocationRepo have expected values', function() {
		it('DepositLocationRepo should have expected values', function() {
			var depositLocationOnScope = angular.toJson(scope.depositLocations);
			var mockDepositLocationRepo = angular.toJson(mockDepositLocationRepo1);
			expect(depositLocationOnScope).toEqual(mockDepositLocationRepo);
		});
	});
	
	describe('Should be able to set a DepositLocationRepo', function() {
		it('should have set the DepositLocationRepo', function() {			
			DepositLocationRepo.set(mockDepositLocationRepo2)			
			var depositLocationOnScope = angular.toJson(scope.depositLocations);
			var mockDepositLocationRepo = angular.toJson(mockDepositLocationRepo2);
			expect(depositLocationOnScope).toEqual(mockDepositLocationRepo);
		});
	});
	
	describe('Should be able to fetch a DepositLocationRepo', function() {		
		it('should have set the fetched DepositLocationRepo', function() {			
			DepositLocationRepo.fetch().then(function(data) {
				DepositLocationRepo.set(data);
				var depositLocationOnScope = angular.toJson(scope.depositLocations);
				var mockDepositLocationRepo = angular.toJson(mockDepositLocationRepo3);
				expect(depositLocationOnScope).toEqual(mockDepositLocationRepo);
			});
		});		
	});	

});
