#!/bin/sh
# Requires: printenv, envsubst, head.

set -e

printenv

echo "Templating appConfig.js"
envsubst < /usr/local/app/templates/appConfig.js.template > appConfig.js
echo "Done"
head -n 20 appConfig.js

echo "Done docker-entrypoint..."

exec "$@"
