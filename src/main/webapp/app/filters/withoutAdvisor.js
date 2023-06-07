vireo.filter('withoutAdvisor', function() {
    return function(recipients) {
        if (typeof recipients != 'object') {
            return recipients;
        }

        var reduced = [];

        for (var i = 0; i < recipients.length; i++) {
            if (recipients[i].type != 'ADVISOR') {
                reduced.push(recipients[i]);
            }
        }

        return reduced;
    };
});
