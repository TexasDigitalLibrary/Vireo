describe('model: LanguageRepo', function() {
	
	var LanguageRepo, WsApi, $rootScope, $scope;

	beforeEach(module('core'));
	
	beforeEach(module('vireo'));
	
	beforeEach(module('mock.wsApi'));
	
	beforeEach(inject(function(_LanguageRepo_, _WsApi_, _$rootScope_) {
        LanguageRepo = _LanguageRepo_;
        WsApi = _WsApi_; 
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
    }));

	describe('model is defined', function() {
		it('should be defined', function() {
			expect(LanguageRepo).toBeDefined();
		});
	});
	
	describe('get method should return a LanguageRepo', function() {
		it('the LanguageRepo was returned', function() {
			var languageRepo = LanguageRepo.get();
			$scope.$apply();
			expect(languageRepo.content).toEqual(mockLanguageRepo1);
		});
	});

	describe('set method should set a LanguageRepo', function() {
		it('the LanguageRepo was set', function() {
			var languageRepo = LanguageRepo.get();
			$scope.$apply();
			LanguageRepo.set({"unwrap":function(){}, "content":mockLanguageRepo2});
			expect(languageRepo.content).toEqual(mockLanguageRepo2);
		});
	});
		
});
