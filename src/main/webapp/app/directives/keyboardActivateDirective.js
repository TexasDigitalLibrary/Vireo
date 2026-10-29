vireo.directive('keyboardActivate', function() {
    return {
        restrict: 'A',
        link: function($scope, element) {

            // Anything exposed as role="button" must also respond to Enter and
            // Space. Native buttons and links already do, so leave those alone
            // rather than activating them twice.
            var nativelyActivated = ['A', 'BUTTON', 'INPUT', 'SELECT', 'TEXTAREA'];

            if (nativelyActivated.indexOf(element[0].nodeName) > -1) {
                return;
            }

            // Bound natively instead of with ng-keydown. ng-keydown would run
            // inside a digest, and the click dispatched below starts its own,
            // which would throw "$apply already in progress".
            element.on('keydown', function(event) {

                if (event.which !== 13 && event.which !== 32) {
                    return;
                }

                // Space would otherwise scroll the page.
                event.preventDefault();

                // A real click rather than the scope expression, so it bubbles
                // to whichever element owns the ng-click. For some of the edit
                // icons that is an ancestor, not the icon itself.
                element[0].click();
            });
        }
    };
});
