vireo.service("ApplicationSettings", function(AbstractModel, WsApi) {
	var self;

	var ApplicationSettings = function(futureData) {
		self = this;
		angular.extend(self, AbstractModel);		
		self.unwrap(self, futureData, "PersistentMap");		
	};

	ApplicationSettings.data = null;

	ApplicationSettings.promise = null;

	ApplicationSettings.set = function(data) {
		self.unwrap(self, data, "PersistentMap");
	};

	ApplicationSettings.get = function() {
		// if(ApplicationSettings.promise) return ApplicationSettings.data;
		var themecolors = {	background_main_color:'#1b333f',
							background_highlight_color:'#43606e',
							submissionStepButonOn_main_color:'#1b333f',
							submissionStepButonOn_highlight_color:'#43606e',
							submissionStepButonOff_main_color:'#a6a18c',
							submissionStepButonOff_highlight_color:'#c7c2a9'
						};
		return themecolors;
	};

	ApplicationSettings.update = function(type, setting) {
		console.log("IN MODEL"+type+"  and setting =  "+setting);
		var responseObject = JSON.parse(data.body);
		ApplicationSettings.data[setting] = responseObject.payload.PersistentMap[setting];
	};

	return ApplicationSettings;
});

