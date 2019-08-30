describe("controller: AdvisorSubmissionReviewController", function () {

    var controller, q, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, _WsApi_) {
            q = $q;

            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($controller, $rootScope, _AdvisorSubmissionRepo_, _EmbargoRepo_, _ModalService_, _RestApi_, _StorageService_) {
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller("AdvisorSubmissionReviewController", {
                $scope: scope,
                $window: mockWindow(),
                AdvisorSubmissionRepo: _AdvisorSubmissionRepo_,
                EmbargoRepo: _EmbargoRepo_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                Submission: mockParameterModel(q, mockSubmission),
                WsApi: WsApi
            });

            // ensure that the isReady() is called.
            if (!scope.$$phase) {
                scope.$digest();
            }
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");
        module("mock.advisorSubmissionRepo");
        module("mock.embargoRepo");
        module("mock.fieldPredicate");
        module("mock.fieldProfile");
        module("mock.modalService");
        module("mock.restApi");
        module("mock.storageService");
        module("mock.submission");
        module("mock.vocabularyWord");
        module("mock.wsApi");

        installPromiseMatchers();
        initializeVariables();
        initializeController();
    });

    describe("Is the controller defined", function () {
        it("should be defined", function () {
            expect(controller).toBeDefined();
        });
    });

    describe("Are the scope methods defined", function () {
        it("addComment should be defined", function () {
            expect(scope.addComment).toBeDefined();
            expect(typeof scope.addComment).toEqual("function");
        });
        it("disableCheck should be defined", function () {
            expect(scope.disableCheck).toBeDefined();
            expect(typeof scope.disableCheck).toEqual("function");
        });
        it("predicateMatch should be defined", function () {
            expect(scope.predicateMatch).toBeDefined();
            expect(typeof scope.predicateMatch).toEqual("function");
        });
        it("required should be defined", function () {
            expect(scope.required).toBeDefined();
            expect(typeof scope.required).toEqual("function");
        });
        it("showVocabularyWord should be defined", function () {
            expect(scope.showVocabularyWord).toBeDefined();
            expect(typeof scope.showVocabularyWord).toEqual("function");
        });
    });

    describe("Do the scope methods work as expected", function () {
        it("addComment should add a comment", function () {
            scope.approval = { updating: null };
            scope.messages = [];
            scope.submission = new mockSubmission(q);

            scope.addComment();
            scope.$digest();

            expect(scope.approval.updating).toBe(false);
            expect(scope.messages.length).toBe(1);
        });
        it("disableCheck should return a boolean", function () {
            var response;
            var approval = {
                embargo: new mockEmbargo(q)
            };

            response = scope.disableCheck(approval);
            expect(response).toBe(true);

            approval.updating = true;
            approval.advisor = {};

            response = scope.disableCheck(approval);
            expect(response).toBe(true);

            approval.message = "test";
            approval.updating = false;

            response = scope.disableCheck(approval);
            expect(response).toBe(false);

            approval.advisor = { approve: true };

            response = scope.disableCheck(approval);
            expect(response).toBe(false);

            approval.embargo = { approve: true };

            response = scope.disableCheck(approval);
            expect(response).toBe(false);
        });
        it("predicateMatch should return a function", function () {
            var response;
            var fieldValue = mockFieldValue(q);
            var aggregateFieldProfile = mockFieldProfile(q);

            response = scope.predicateMatch(fieldValue);
            expect(typeof response).toBe("function");

            response = response(aggregateFieldProfile);
            expect(typeof response).toBe("boolean");
        });
        it("required should return a boolean", function () {
            var response;

            response = scope.required({optional: true});
            expect(response).toBe(false);

            response = scope.required({optional: false});
            expect(response).toBe(true);
        });
        it("showVocabularyWord should return a boolean", function () {
            var response;
            var vocabularyWord = new mockVocabularyWord(q);
            var fieldProfile = new mockFieldProfile(q);
            var fieldPredicate = new mockFieldPredicate(q);
            var embargo = new mockEmbargo(q);

            fieldPredicate.id = 90;

            response = scope.showVocabularyWord(vocabularyWord);
            expect(response).toBe(true);

            delete fieldProfile.fieldPredicate;
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(true);

            fieldProfile.fieldPredicate = fieldPredicate;
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(true);

            fieldPredicate.value = "default_embargos";
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(true);

            fieldPredicate.value = "proquest_embargos";
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(true);

            vocabularyWord.identifier = embargo.id;

            fieldPredicate.value = "default_embargos";
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(true);

            embargo.mock(dataEmbargo3);
            embargo.isActive = false;
            fieldPredicate.id = embargo.id;
            vocabularyWord.identifier = embargo.id;

            fieldPredicate.value = "default_embargos";
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(false);
            vocabularyWord.identifier = embargo.id;
        });
    });
});
