'use strict';

angular.module('seedApp.version', [
  'seedApp.version.interpolate-filter',
  'seedApp.version.version-directive'
])

.value('version', appConfig.version);
