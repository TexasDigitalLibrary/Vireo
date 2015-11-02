module.exports = function(grunt) {

	// Configurable paths
	var build = {
			app: 'app',
			dist: 'dist'
	};


	grunt.initConfig({

		// Project settings
		build: build,

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
			      '<%= build.app %>/bower_components/core/**/*',
			      '<%= build.app %>/bower_components/core/components/**/*',
			      '<%= build.app %>/bower_components/core/resources/**/*',
			      '!<%= build.app %>/bower_components/**/*',
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
						'<%= build.app %>/bower_components/core/**/*.js',
						'!<%= build.app %>/bower_components/core/app/core.js',
						'!<%= build.app %>/bower_components/core/config/coreConfig.js',
				      	'!<%= build.app %>/bower_components/core/components/**/*',
				      	'!<%= build.app %>/bower_components/core/resources/**/*'
					 ],
				dest: '<%= build.app %>/resources/scripts/core_concat.js'
			},			
			angular: {
				src: [
				      	'<%= build.app %>/**/*.js',
				      	'!<%= build.app %>/config/appConfig.js',
				      	'!<%= build.app %>/config/appConfig_sample.js',
				      	'!<%= build.app %>/bower_components/**/*',
				      	'!<%= build.app %>/components/**/*',
				      	'!<%= build.app %>/resources/**/*',
				      	'!<%= build.app %>/resources/scripts/app_contact.js'
				     ],
				dest: '<%= build.app %>/resources/scripts/app_concat.js'
			}
		},

		uglify: {
			options: {
				mangle: false
			},
			core: {
				src:  '<%= build.app %>/resources/scripts/core_concat.js',
				dest: '<%= build.app %>/resources/scripts/core_concat.js'
			},
			angular: {
				src:  '<%= build.app %>/resources/scripts/app_concat.js',
				dest: '<%= build.app %>/resources/scripts/app_concat.js'
			}
		},

		usemin: {
			html: '<%= build.app %>/index.html',
			options: {
				assetsDirs: ['<%= build.app %>/resources/scripts']
			}
		},
		
		compass: {
			dist: {
				options: {
					sassDir: '<%= build.app %>/resources/styles/sass',
					cssDir: '<%= build.app %>/resources/styles'
				}
			}
		},
		watch: {
			css: {
				files: '**/*.scss',
				tasks: ['compass']
			}
		}

	});

	grunt.loadNpmTasks('grunt-contrib-jshint');
	grunt.loadNpmTasks('grunt-usemin');
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-contrib-compass');
	grunt.loadNpmTasks('grunt-contrib-watch');

	grunt.registerTask('default', ['jshint', 'watch']);

	grunt.registerTask('develop', ['jshint', 'useminPrepare', 'concat', 'usemin', 'watch']);
	
	grunt.registerTask('deploy', ['jshint', 'useminPrepare', 'concat', 'uglify', 'usemin', 'compass']);

};