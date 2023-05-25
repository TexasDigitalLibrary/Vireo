vireo.filter('isCurrentUsersFilter', function() {
    return function(filters, userSettings) {
        if (typeof filters != 'object' || typeof userSettings != 'object') {
            return filters;
        }

        var usersFilters = [];

        for (var i = 0; i < filters.length; i++) {
            if (filters[i].user == userSettings.id) {
                usersFilters.push(filters[i]);
            }
        }

        return usersFilters;
    };
});
