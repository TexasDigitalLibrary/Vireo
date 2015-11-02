'use strict';

describe('seedApp.version module', function() {
  beforeEach(module('seedApp.version'));

  describe('version service', function() {
    it('should return current version', inject(function(version) {
      expect(version).toEqual(appConfig.version);
    }));
  });
});
