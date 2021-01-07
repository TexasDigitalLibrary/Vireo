module.exports = function(config){
    config.set({

        preprocessors : {
            "app/!(node_modules)/**/*.js": "coverage",
            "app/!(node_modules)/**/*.html": "ng-html2js"
        },

        reporters : ['progress', 'coverage'],

        basePath : '../',

        files : [
            'app/config/appConfig.js',
            'app/config/apiMapping.js',

            'app/node_modules/jquery/dist/jquery.js',
            'app/node_modules/bootstrap/dist/js/bootstrap.js',

            'app/node_modules/sockjs-client/dist/sockjs.js',
            'app/node_modules/stompjs/lib/stomp.js',

            'app/node_modules/angular/angular.js',

            'app/node_modules/angular-sanitize/angular-sanitize.js',
            'app/node_modules/angular-route/angular-route.js',
            'app/node_modules/angular-loader/angular-loader.js',
            'app/node_modules/angular-messages/angular-messages.js',
            'app/node_modules/angular-mocks/angular-mocks.js',

            'app/node_modules/ng-sortable/dist/ng-sortable.js',
            'app/node_modules/ng-csv/build/ng-csv.js',

            'app/node_modules/ng-table/bundles/ng-table.js',

            'app/node_modules/ng-file-upload/dist/ng-file-upload-shim.js',
            'app/node_modules/ng-file-upload/dist/ng-file-upload.js',

            'app/node_modules/jasmine-promise-matchers/dist/jasmine-promise-matchers.js',

            'app/node_modules/tinymce/tinymce.js',
            'app/node_modules/angular-ui-tinymce/dist/tinymce.min.js',

            'app/node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js',

            'app/node_modules/file-saver/dist/FileSaver.js',

            'app/node_modules/@wvr/core/app/config/coreConfig.js',

            'app/node_modules/@wvr/core/app/components/**/*.js',

            'app/node_modules/@wvr/core/app/core.js',

            'app/node_modules/@wvr/core/app/**/*.js',


            'app/components/**/*.js',

            'tests/testSetup.js',

            'app/app.js',

            'app/config/runTime.js',

            'app/constants/**/*.js',

            'app/controllers/**/*.js',

            'app/directives/**/*.js',

            'app/services/**/*.js',

            'app/factories/**/*.js',

            'app/model/**/*.js',

            'app/repo/**/*.js',

            'tests/core/**/*.js',

            'tests/mocks/**/*.js',

            'tests/unit/**/*.js'

        ],

        autoWatch : true,

        frameworks: ['jasmine'],

        browsers : ['Firefox', 'Chrome', 'ChromeHeadless', 'ChromeHeadlessNoSandbox'],

        customLaunchers: {
            ChromeHeadlessNoSandbox: {
                base: 'ChromeHeadless',
                flags: ['--no-sandbox']
            }
        },

        plugins : [
            'karma-chrome-launcher',
            'karma-coverage',
            'karma-firefox-launcher',
            'karma-jasmine',
            'karma-junit-reporter',
            'karma-ng-html2js-preprocessor'
        ],

        coverageReporter: {
          type: "lcov",
          dir: "coverage/"
        },

        junitReporter : {
            outputFile: 'test_out/unit.xml',
            suite: 'unit'
        },
        ngHtml2JsPreprocessor: {
            moduleName: 'templates'
        }

    });
};
