/**
 * Makes a non interactive element that carries an ng-click reachable and operable
 * from the keyboard, without changing how it looks. Adds a tab stop and a button
 * role when the element does not already declare them, and activates the element
 * on Enter or Space the way a native button would.
 *
 * Use it on clickable spans, images and list items. Native controls (button, a[href],
 * input, select, textarea) already do this and do not need the attribute.
 */
vireo.directive("keyboardClickable", function () {

    var ENTER = 13;
    var SPACE = 32;

    return {
        restrict: 'A',
        link: function ($scope, element, attrs) {

            if (attrs.tabindex === undefined) {
                element.attr('tabindex', '0');
            }

            if (attrs.role === undefined) {
                element.attr('role', 'button');
            }

            var isDisabled = function () {
                return element.attr('disabled') !== undefined || element.hasClass('disabled');
            };

            var onKeydown = function (event) {
                if (event.which !== ENTER && event.which !== SPACE) {
                    return;
                }

                if (isDisabled()) {
                    return;
                }

                // Space scrolls the page by default and Enter may submit a surrounding form.
                event.preventDefault();

                element[0].click();
            };

            element.on('keydown', onKeydown);

            $scope.$on('$destroy', function () {
                element.off('keydown', onKeydown);
            });
        }
    };
});
