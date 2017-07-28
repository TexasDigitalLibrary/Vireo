vireo.controller("EmailTemplateRepoController", function ($controller, $scope, $q, EmailTemplateRepo, DragAndDropListenerFactory, FieldPredicateRepo) {

    angular.extend(this, $controller("AbstractController", {
        $scope: $scope
    }));

    $scope.emailTemplateRepo = EmailTemplateRepo;

    $scope.emailTemplates = EmailTemplateRepo.getAll();

    $scope.fieldPredicates = FieldPredicateRepo.getAllFiltered(function (fp) {
        return !fp.documentTypePredicate;
    });

    $scope.cursorLocation = 0;

    $scope.setCursorLocation = function ($event) {
        $scope.cursorLocation = angular.element($event.target).prop("selectionStart");
    };

    $scope.insertText = function (text) {
        if (!$scope.modalData.message) $scope.modalData.message = "";
        var firstPartOfMessage = $scope.modalData.message.substr(0, $scope.cursorLocation);
        var secondPartOfMessage = $scope.modalData.message.substr($scope.cursorLocation, $scope.modalData.message.length);
        var insertText = " {" + text + "} ";
        $scope.modalData.message = firstPartOfMessage + insertText + secondPartOfMessage;

        $scope.cursorLocation += insertText.length;

    };

    EmailTemplateRepo.listen(function (data) {
        $scope.resetEmailTemplates();
    });

    $scope.ready = $q.all([EmailTemplateRepo.ready()]);

    $scope.dragging = false;

    $scope.trashCanId = 'email-template-trash';

    $scope.sortAction = "confirm";

    $scope.templateToString = function (template) {
        return template.name;
    };

    $scope.forms = {};

    $scope.ready.then(function () {

        $scope.resetEmailTemplates = function () {
            $scope.emailTemplateRepo.clearValidationResults();
            for (var key in $scope.forms) {
                if ($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
                    $scope.forms[key].$setPristine();
                }
            }
            if ($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
                $scope.modalData.refresh();
            }
            $scope.modalData = {
                'name': '',
                'subject': '',
                'message': ''
            };
            $scope.closeModal();
        };

        $scope.resetEmailTemplates();

        $scope.selectEmailTemplate = function (index) {
            $scope.modalData = $scope.emailTemplates[index];
        };

        $scope.createEmailTemplate = function () {
            console.log('create', $scope.modalData);
            $scope.modalData.save();
        };

        $scope.launchEditModal = function (index) {
            $scope.modalData = $scope.emailTemplates[index - 1];
            $scope.openModal('#emailTemplatesEditModal');
        };

        $scope.updateEmailTemplate = function () {
            console.log('update', $scope.modalData);
            $scope.modalData.save();
        };

        $scope.removeEmailTemplate = function () {
            $scope.modalData.delete();
        };

        $scope.reorderEmailTemplates = function (src, dest) {
            EmailTemplateRepo.reorder(src, dest);
        };

        $scope.sortEmailTemplates = function (column) {
            if ($scope.sortAction == 'confirm') {
                $scope.sortAction = 'sort';
            } else if ($scope.sortAction == 'sort') {
                EmailTemplateRepo.sort(column);
                $scope.sortAction = 'confirm';
            }
        };

        $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
            trashId: $scope.trashCanId,
            dragging: $scope.dragging,
            select: $scope.selectEmailTemplate,
            model: $scope.emailTemplates,
            confirm: '#emailTemplatesConfirmRemoveModal',
            reorder: $scope.reorderEmailTemplates,
            container: '#email-templates'
        });

    });

});
