vireo.service("ModalService", function ($timeout) {

    var ModalService = this;

    var bodyElement = angular.element('body');

    bodyElement.on('keydown', function (event) {
        if (modalElement) {
            if (event.code === 'Tab') {
                $timeout(function () {
                    if (!modalElement.has(angular.element(':focus')).length) {
                        modalElement.find(':input:first').focus();
                    }
                });
            } else if (event.code === 'Escape') {
                event.preventDefault();
                ModalService.closeModal();
            }
        }
    });

    var modalElement;

    ModalService.openModal = function (id) {
        modalElement = angular.element(id);
        modalElement.modal('show');
        modalElement.on('shown.bs.modal', function (e) {
            modalElement.find(':input:not(:button):visible:enabled:not([readonly]):first, :input:first').focus();
        });
    };

    ModalService.closeModal = function () {
        if (!!modalElement) {
            modalElement.modal('hide');
            modalElement.off('shown.bs.modal');
            modalElement = undefined;
        }

        // forcefully restore the body element
        bodyElement.removeClass('modal-open');
        bodyElement.css('padding-right', '');

        // forcefully remove modal-backdrop div with
        var backdropElement = angular.element('div.modal-backdrop');
        if (!!backdropElement) {
            backdropElement.remove();
        }
    };

    return ModalService;

});
