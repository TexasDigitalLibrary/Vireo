'use strict';

describe('vireo.version module', function() {
  beforeEach(module('vireo.version'));

  describe('version service', function() {
    it('should return current version', inject(function(version) {
      expect(version).toEqual(appConfig.version);
    }));
  });
});
