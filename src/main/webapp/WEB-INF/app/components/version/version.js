'use strict';

angular.module('vireo.version', [
  'vireo.version.interpolate-filter',
  'vireo.version.version-directive'
])

.value('version', appConfig.version);
