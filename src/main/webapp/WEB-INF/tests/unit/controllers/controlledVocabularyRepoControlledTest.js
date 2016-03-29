describe('controller: ControlledVocabularyRepoController', function() {
	
	var controller, scope, LanguageRepo, ControlledVocabularyRepo, DragAndDropListenerFactory;

	beforeEach(module('core'));

	beforeEach(module('vireo'));

	beforeEach(module('mock.languageRepo'));
	
	beforeEach(module('mock.controlledVocabularyRepo'));

	beforeEach(module('mock.dragAndDropListenerFactory'));
	
	beforeEach(inject(function($controller, $rootScope, _LanguageRepo_, _ControlledVocabularyRepo_, _DragAndDropListenerFactory_) {
        scope = $rootScope.$new();
        controller = $controller('ControlledVocabularyRepoController', {
            $scope: scope,
            LanguageRepo: _LanguageRepo_,
            ControlledVocabularyRepo: _ControlledVocabularyRepo_,
            DragAndDropListenerFactory: _DragAndDropListenerFactory_
        });
        LanguageRepo = _LanguageRepo_;
        ControlledVocabularyRepo = _ControlledVocabularyRepo_;
        DragAndDropListenerFactory = _DragAndDropListenerFactory_;
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
	
	describe('Does the scope have a ControlledVocabulary', function() {
		it('ControlledVocabulary should be on the scope', function() {
			expect(scope.controlledVocabulary).toBeDefined();
		});
	});
	
	describe('Does the ControlledVocabulary have expected ControlledVocabulary', function() {
		it('ControlledVocabulary should have expected ControlledVocabulary', function() {
			var controlledVocabularyOnScope = angular.toJson(scope.controlledVocabulary);
			var mockControlledVocabularyRepo = angular.toJson(mockControlledVocabularyRepo1);
			expect(controlledVocabularyOnScope).toEqual(mockControlledVocabularyRepo);
		});
	});
	
	describe('Should be able to set a ControlledVocabulary', function() {
		it('should have set the ControlledVocabulary', function() {			
			ControlledVocabularyRepo.set(mockControlledVocabularyRepo2)			
			var controlledVocabularyOnScope = angular.toJson(scope.controlledVocabulary);
			var mockControlledVocabularyRepo = angular.toJson(mockControlledVocabularyRepo2);
			expect(controlledVocabularyOnScope).toEqual(mockControlledVocabularyRepo);
		});
	});
	
});
