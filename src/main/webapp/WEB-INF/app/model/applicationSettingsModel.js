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
	var themeColors = {		background_main_color:'#1b333f',
							background_highlight_color:'#43606e',
							button_main_color_on:'#1b333f',
							button_highlight_color_on:'#43606e',
							button_main_color_off:'#a6a18c',
							button_highlight_color_off:'#c7c2a9'
						};
	var resetThemeColors = {background_main_color:'#1b333f',
							background_highlight_color:'#43606e',
							button_main_color_on:'#1b333f',
							button_highlight_color_on:'#43606e',
							button_main_color_off:'#a6a18c',
							button_highlight_color_off:'#c7c2a9'
						};

	ApplicationSettings.get = function() {
		return themeColors;
	};

	ApplicationSettings.update = function(type, value) {
		
		themeColors[type] = value;
	};

	ApplicationSettings.reset = function(setting) {
		themeColors[setting] = resetThemeColors[setting];
	};

	return ApplicationSettings;
});