describe('model: FieldValue', function () {
    var model, rootScope, scope, WsApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, FieldValue, _WsApi_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            model = angular.extend(new FieldValue(), dataFieldValue1);
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });

    describe('Are the model methods defined', function () {
        it('addValidationMessage should be defined', function () {
            expect(model.addValidationMessage).toBeDefined();
            expect(typeof model.addValidationMessage).toEqual("function");
        });
        it('getValidationMessages should be defined', function () {
            expect(model.getValidationMessages).toBeDefined();
            expect(typeof model.getValidationMessages).toEqual("function");
        });
        it('isValid should be defined', function () {
            expect(model.isValid).toBeDefined();
            expect(typeof model.isValid).toEqual("function");
        });
        it('setIsValid should be defined', function () {
            expect(model.setIsValid).toBeDefined();
            expect(typeof model.setIsValid).toEqual("function");
        });
        it('setValidationMessages should be defined', function () {
            expect(model.setValidationMessages).toBeDefined();
            expect(typeof model.setValidationMessages).toEqual("function");
        });
    });

    describe('Are the model methods working as expected', function () {
        it('addValidationMessage should add a validation message', function () {
            var messages;
            model.addValidationMessage("test");

            messages = model.getValidationMessages();

            expect(messages.length).toBe(1);
            expect(messages[0]).toEqual("test");
        });
        it('getValidationMessages should return all validation messages', function () {
            var response = model.getValidationMessages();

            expect(typeof response).toBe("object");
            expect(response.length).toBe(0);
        });
        it('isValid should return a boolean', function () {
            var response = model.isValid();

            expect(response).toBe(true);
        });
        it('setIsValid should assign a boolean', function () {
            model.setIsValid(false);

            expect(model.isValid()).toBe(false);

            model.setIsValid(true);

            expect(model.isValid()).toBe(true);
        });
        it('setValidationMessages should set an array of validation messages', function () {
            var messages;
            model.setValidationMessages(["test", "123"]);

            messages = model.getValidationMessages();

            expect(messages.length).toBe(2);
            expect(messages[0]).toEqual("test");
            expect(messages[1]).toEqual("123");
        });
    });
});
