describe('model: ControlledVocabularyRepo', function() {
	
	var ControlledVocabularyRepo, WsApi, $rootScope, $scope;

	beforeEach(module('core'));
	
	beforeEach(module('vireo'));
	
	beforeEach(module('mock.wsApi'));
	
	beforeEach(inject(function(_ControlledVocabularyRepo_, _WsApi_, _$rootScope_) {
        ControlledVocabularyRepo = _ControlledVocabularyRepo_;
        WsApi = _WsApi_; 
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
    }));

	describe('model is defined', function() {
		it('should be defined', function() {
			expect(ControlledVocabularyRepo).toBeDefined();
		});
	});
	
	describe('get method should return a ControlledVocabularyRepo', function() {
		it('the ControlledVocabularyRepo was returned', function() {
			var controlledVocabularyRepo = ControlledVocabularyRepo.get();
			$scope.$apply();
			expect(controlledVocabularyRepo.content).toEqual(mockControlledVocabularyRepo1);
		});
	});

	describe('set method should set a ControlledVocabularyRepo', function() {
		it('the ControlledVocabularyRepo was set', function() {
			var controlledVocabularyRepo = ControlledVocabularyRepo.get();
			$scope.$apply();
			ControlledVocabularyRepo.set({"unwrap":function(){}, "content":mockControlledVocabularyRepo2});
			expect(controlledVocabularyRepo.content).toEqual(mockControlledVocabularyRepo2);
		});
	});

});
