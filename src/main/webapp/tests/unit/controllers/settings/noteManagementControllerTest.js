describe('controller: NoteManagementController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.dragAndDropListenerFactory');
        module('mock.modalService');
        module('mock.note');
        module('mock.noteRepo');
        module('mock.organizationRepo');
        module('mock.restApi');
        module('mock.workflowStepRepo');

        inject(function ($controller, $rootScope, $window, _DragAndDropListenerFactory_, _ModalService_, _Note_, _NoteRepo_, _OrganizationRepo_, _RestApi_, _WorkflowStepRepo_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('NoteManagementController', {
                $scope: scope,
                $window: $window,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                ModalService: _ModalService_,
                Note: _Note_,
                NoteRepo: _NoteRepo_,
                OrganizationRepo: _OrganizationRepo_,
                RestApi: _RestApi_,
                WorkflowStepRepo: _WorkflowStepRepo_
            });

            // ensure that the isReady() is called.
            scope.$digest();
        });
    });

    describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });

});
