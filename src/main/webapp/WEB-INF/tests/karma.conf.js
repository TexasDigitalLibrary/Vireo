module.exports = function(config){
    config.set({

        basePath : '../',

        files : [
            'app/config/appConfig.js',

            'app/bower_components/jquery/dist/jquery.js',
            'app/bower_components/angular/angular.js',
            'app/bower_components/angular-sanitize/angular-sanitize.min.js',
            'app/bower_components/angular-mocks/angular-mocks.js',
            'app/bower_components/angular-route/angular-route.js',

            'app/bower_components/tinymce-dist/tinymce.min.js',
            'app/bower_components/angular-ui-tinymce/dist/tinymce.min.js',
            'app/bower_components/ng-sortable/dist/ng-sortable.js',
            'app/bower_components/ng-csv/build/ng-csv.min.js',
            'app/bower_components/ng-file-upload/ng-file-upload-shim.min.js',
            'app/bower_components/ng-file-upload/ng-file-upload.min.js',

            'app/bower_components/core/app/config/coreConfig.js',

            'app/bower_components/core/app/components/**/*.js',

            'app/bower_components/core/app/core.js',

            'app/bower_components/core/app/**/*.js',


            'app/components/**/*.js',

            'tests/testSetup.js',
            
            'app/app.js',
            
            'app/config/runTime.js',
            
            'app/controllers/**/*.js',

            'app/directives/**/*.js',
            
            'app/services/**/*.js',            
            
            'app/model/**/*.js',            
            
            'tests/mocks/**/*.js',
            
            'tests/unit/**/*.js'
            
        ],

        autoWatch : true,

        frameworks: ['jasmine'],

        browsers : ['Firefox', 'Chrome'],

        plugins : [
            'karma-jasmine',
            'karma-chrome-launcher',
            'karma-firefox-launcher',
            'karma-junit-reporter'
            ],

        junitReporter : {
            outputFile: 'test_out/unit.xml',
            suite: 'unit'
        }

    });
};