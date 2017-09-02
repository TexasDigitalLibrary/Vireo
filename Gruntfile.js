module.exports = function (grunt) {

    // Configurable paths
    var build = {
        app: 'src/main/webapp/WEB-INF/app'
    };


    grunt.initConfig({

        // Project settings
        build: build,
        
        symlink: {
            options: {
                overwrite: true,
                force: true
            },
            explicit: {
                src: 'node_modules',
                dest: 'src/main/webapp/WEB-INF/app/node_modules'
            }
        },

        useminPrepare: {
            html: '<%= build.app %>/index.html',
            options: {
                dest: '<%= build.app %>'
            }
        },

        jshint: {
            options: {
                jshintrc: '.jshintrc',
                reporter: require('jshint-stylish')
            },
            all: [
                'Gruntfile.js',
                '<%= build.app %>/**/*.js',
                '<%= build.app %>/node_modules/weaver-ui-core/**/*',
                '<%= build.app %>/node_modules/weaver-ui-core/components/**/*',
                '<%= build.app %>/node_modules/weaver-ui-core/resources/**/*',
                '!<%= build.app %>/node_modules/**/*',
                '!<%= build.app %>/components/**/*',
                '!<%= build.app %>/resources/**/*'
            ]
        },

        concat: {
            options: {
                separator: ';'
            },
            core: {
                src: [
                    'node_modules/node_modules/**/*.js',
                    '!node_modules/node_modules/app/core.js',
                    '!node_modules/node_modules/config/coreConfig.js',
                    '!node_modules/node_modules/components/**/*',
                    '!node_modules/node_modules/resources/**/*'
                ],
                dest: '<%= build.app %>/resources/scripts/core_concat.js'
            },
            angular: {
                src: [
                    '<%= build.app %>/**/*.js',
                    '!<%= build.app %>/config/appConfig.js',
                    '!<%= build.app %>/config/appConfig_sample.js',
                    '!node_modules/**/*',
                    '!<%= build.app %>/node_modules/**/*',
                    '!<%= build.app %>/components/**/*',
                    '!<%= build.app %>/resources/**/*',
                    '!<%= build.app %>/resources/scripts/app_concat.js'
                ],
                dest: '<%= build.app %>/resources/scripts/app_concat.js'
            }
        },

        uglify: {
            options: {
                mangle: false
            },
            core: {
                src: '<%= build.app %>/resources/scripts/core_concat.js',
                dest: '<%= build.app %>/resources/scripts/core_concat.js'
            },
            angular: {
                src: '<%= build.app %>/resources/scripts/app_concat.js',
                dest: '<%= build.app %>/resources/scripts/app_concat.js'
            }
        },

        usemin: {
            html: '<%= build.app %>/index.html',
            options: {
                assetsDirs: ['<%= build.app %>/resources/scripts']
            }
        },

        sass: {
        	options: {
                sourceMap: false
            },
            dist: {
                files: [{
                    expand: true,
                    cwd: 'src/main/webapp/WEB-INF/app/resources/styles/sass',
                    src: ['*.scss'],
                    dest: 'src/main/webapp/WEB-INF/app/resources/styles',
                    ext: '.css'
                }]
            }
        },

        watch: {
            css: {
                files: '**/*.scss',
                tasks: ['sass']
            }
        }

    });

    grunt.loadNpmTasks('grunt-sass');
    grunt.loadNpmTasks('grunt-usemin');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-symlink');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-watch');

    grunt.registerTask('default', ['jshint', 'sass', 'symlink']);

    grunt.registerTask('watch', ['watch']);

    grunt.registerTask('develop', ['jshint', 'useminPrepare', 'concat', 'usemin', 'watch']);

    grunt.registerTask('deploy', ['jshint', 'useminPrepare', 'concat', 'uglify', 'usemin', 'sass']);

};
