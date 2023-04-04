module.exports = function (config) {
  config.set({

    preprocessors: {
      'src/main/webapp/app/**/*.js': 'coverage',
      'src/main/webapp/app/**/*.html': ['ng-html2js'],
    },

    reporters: ['progress', 'coverage', 'coveralls'],

    basePath: './',

    files: [
      'dist/appConfig.js',
      'src/main/webapp/app/config/apiMapping.js',

      'node_modules/jquery/dist/jquery.js',
      'node_modules/bootstrap/dist/js/bootstrap.js',
      'node_modules/sockjs-client/dist/sockjs.js',
      'node_modules/stompjs/lib/stomp.js',
      'node_modules/angular/angular.js',
      'node_modules/angular-sanitize/angular-sanitize.js',
      'node_modules/angular-route/angular-route.js',
      'node_modules/angular-loader/angular-loader.js',
      'node_modules/angular-messages/angular-messages.js',
      'node_modules/angular-mocks/angular-mocks.js',
      'node_modules/ng-sortable/dist/ng-sortable.js',
      'node_modules/ng-csv/build/ng-csv.js',
      'node_modules/ng-table/bundles/ng-table.js',
      'node_modules/ng-file-upload/dist/ng-file-upload-shim.js',
      'node_modules/ng-file-upload/dist/ng-file-upload.js',
      'node_modules/jasmine-promise-matchers/dist/jasmine-promise-matchers.js',
      'node_modules/tinymce/tinymce.js',
      'node_modules/angular-ui-tinymce/dist/tinymce.min.js',
      'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js',
      'node_modules/file-saver/dist/FileSaver.js',

      'node_modules/@wvr/core/app/config/coreConfig.js',
      'node_modules/@wvr/core/app/components/**/*.js',
      'node_modules/@wvr/core/app/core.js',
      'node_modules/@wvr/core/app/**/*.js',

      'src/main/webapp/tests/testSetup.js',

      'src/main/webapp/app/app.js',
      'src/main/webapp/app/config/runTime.js',
      'src/main/webapp/app/components/**/*.js',
      'src/main/webapp/app/constants/**/*.js',
      'src/main/webapp/app/controllers/**/*.js',
      'src/main/webapp/app/directives/**/*.js',
      'src/main/webapp/app/factories/**/*.js',
      'src/main/webapp/app/filters/**/*.js',
      'src/main/webapp/app/model/**/*.js',
      'src/main/webapp/app/repo/**/*.js',
      'src/main/webapp/app/services/**/*.js',

      'src/main/webapp/tests/core/**/*.js',
      'src/main/webapp/tests/mocks/**/*.js',
      'src/main/webapp/tests/unit/**/*.js'
    ],

    autoWatch: true,

    frameworks: ['jasmine'],

    browsers: ['ChromeHeadless'],

    customLaunchers: {
      ChromeHeadlessNoSandbox: {
        base: 'ChromeHeadless',
        flags: ['--no-sandbox']
      }
    },

    plugins: [
      'karma-chrome-launcher',
      'karma-coverage',
      'karma-coveralls',
      'karma-firefox-launcher',
      'karma-jasmine',
      'karma-junit-reporter',
      'karma-ng-html2js-preprocessor',
    ],

    junitReporter: {
      outputFile: 'test_out/unit.xml',
      suite: 'unit',
    },

    ngHtml2JsPreprocessor: {
      stripPrefix: 'src/main/webapp/app/',
      moduleName: 'templates',
    },

    coverageReporter: {
      type: 'lcov',
      dir: 'coverage/',
      subdir: '.',
    }

  });
};
