module.exports = function(config){
  config.set({

    basePath : '../',

    files : [
      'app/js/angular/angular.min.js',
      'app/js/angular-route/angular-route.min.js',
      'app/js/angular-resource/angular-resource.min.js',
      'app/js/angular-animate/angular-animate.min.js',
      'app/js/angular-mocks/angular-mocks.js',
      'app/*.js',
      'test/unit/**/*.js'
    ],

    autoWatch : true,

    frameworks: ['jasmine'],

    browsers : ['Chrome', 'Firefox'],

    plugins : [
            'karma-chrome-launcher',
            'karma-firefox-launcher',
            'karma-jasmine'
            ],

    junitReporter : {
      outputFile: 'test_out/unit.xml',
      suite: 'unit'
    }

  });
};