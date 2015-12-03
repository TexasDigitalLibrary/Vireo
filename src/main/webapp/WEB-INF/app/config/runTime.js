vireo.run(function($rootScope, $http, AuthServiceApi) {
	
	angular.element("body").fadeIn(1000);

	// Add runtime tasks here

	//Override Authservice Refresh
	AuthServiceApi.getRefreshToken = function(cb) {

		var url = appConfig.authService+"/refresh";
		if(appConfig.mockRole) url += "&mock=" + appConfig.mockRole;

		if (!AuthServiceApi.pendingRefresh) {
			AuthServiceApi.pendingRefresh = $http.get(url, {withCredentials: true}).
				then(function(response) {

						StorageService.set('token', response.data.tokenAsString);

						// This timeout ensures that pending request is not nulled to early
						$timeout(function() {
							delete AuthServiceApi.pendingRefresh;
						});

						if(cb) cb();
					},
					function(response) {
						delete sessionStorage.token;

						if(appConfig.mockRole) {
							url += "&referer="+location.href
							window.open(appConfig.authService + "/token?referer="+location.href + "&mock=" + appConfig.mockRole, "_self");
						}
						else {
							url += "?referer="+location.href
							window.open(appConfig.authService + "/token?referer="+location.href, "_self");
						}

				});
		}
		return AuthServiceApi.pendingRefresh;
	};
	
});