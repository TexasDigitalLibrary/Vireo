#!/bin/sh
# Requires: printenv, envsubst, head.

set -e

echo "Templating appConfig.js"
envsubst < /usr/local/app/templates/appConfig.js.template > $APP_PATH/appConfig.js
echo "Done"

echo "Done docker-entrypoint..."

exec "$@"
