exports.config = {
  allScriptsTimeout: 11000,

  specs: [
    'e2e/*.js'
  ],

  capabilities: {
    'browserName': 'chrome'
  },
  
  onPrepare: function() {
	  /* global angular: false, browser: false, jasmine: false */

	  // Disable animations so e2e tests run more quickly
	  var disableNgAnimate = function() {
	    angular.module('disableNgAnimate', []).run(['$animate', function($animate) {
	      $animate.enabled(false);
	    }]);
	  };
	  browser.addMockModule('disableNgAnimate', disableNgAnimate);
	  browser.driver.manage().window().maximize();
  },

  chromeOnly: true,

  baseUrl: 'http://localhost:9000/',

  framework: 'jasmine',

  jasmineNodeOpts: {
    defaultTimeoutInterval: 30000
  }
};
