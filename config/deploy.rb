# config valid for current version and patch releases of Capistrano
lock "~> 3.18.0"

set :application, "Vireo4"
set :repo_url, "https://github.com/medusa-project/Vireo.git"
set :branch, "main"
# Default branch is :master
# ask :branch, `git rev-parse --abbrev-ref HEAD`.chomp

# Default deploy_to directory is /var/www/my_app_name
# set :deploy_to, "/var/www/my_app_name"
set :deploy_to, "/home/vireo/vireo4"

# Default value for :format is :airbrussh.
# set :format, :airbrussh

# You can configure the Airbrussh format using :format_options.
# These are the defaults.
# set :format_options, command_output: true, log_file: "log/capistrano.log", color: :auto, truncate: :auto

# Default value for :pty is false
# set :pty, true

# Default value for :linked_files is []
# append :linked_files, "config/database.yml"

# Default value for linked_dirs is []
# append :linked_dirs, "log", "tmp/pids", "tmp/cache", "tmp/sockets", "public/system"

# Default value for default_env is {}
# set :default_env, { path: "/opt/ruby/bin:$PATH" }

# Default value for local_user is ENV['USER']
# set :local_user, -> { `git config user.name`.chomp }

# Default value for keep_releases is 5
# set :keep_releases, 5

# Uncomment the following to require manually verifying the host key before first deploy.
# set :ssh_options, verify_host_key: :secure

# additional deploy tasks
namespace :deploy do

    task :buildwar do
      puts "running script to build war file in #{deploy_to}"
      # puts "the server is #{server}, the user is #{user}"
      on roles(:app) do
        execute "mkdir -p #{deploy_to}/shared/"
        execute "ln -sf #{deploy_to}/shared/application.yml #{deploy_to}/current/src/main/resources/application.yml"
        execute "ln -sf #{deploy_to}/shared/build-config.js #{deploy_to}/current/.wvr/build-config.js"
        # the cd to current and the sh need to happen in the same execute command.
        # execute "cd #{deploy_to}/current/ && sh create_vireo_war_file.sh"
      end
    end

    after "deploy:cleanup", "deploy:buildwar"

  end
