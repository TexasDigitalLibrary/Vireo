vireo.model("UserSettings", function UserSettings(UserService) {

    return function UserSettings() {

        var userSettings = this;

        // additional model methods and variables

        UserService.userEvents().then(null, null, function () {
            userSettings.fetch();
        });

        return this;
    };

});
