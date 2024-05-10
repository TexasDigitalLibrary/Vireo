describe("filter: uniqueEmbargoType", function () {
  var $scope, MockedUser, filter;

  var initializeVariables = function () {
    inject(function (_$q_) {
      $q = _$q_;

      MockedUser = new mockUser($q);
    });
  };

  var initializeFilter = function (settings) {
    inject(function (_$filter_, _$rootScope_) {
      $scope = _$rootScope_.$new();

      filter = _$filter_("uniqueEmbargoType");
    });
  };

  beforeEach(function () {
    module("core");
    module("vireo");
    module("mock.user", function ($provide) {
      var User = function () {
        return MockedUser;
      };
      $provide.value("User", User);
    });
    module("mock.userService");

    installPromiseMatchers();
    initializeVariables();
    initializeFilter();
  });

  afterEach(function () {
    $scope.$destroy();
  });

  describe("Is the filter", function () {
    it("defined", function () {
      expect(filter).toBeDefined();
    });
  });

  describe("Does the filter", function () {
    it("return empty array on empty input", function () {
      var result;

      result = filter("", false);

      expect(result).toEqual([]);

      result = filter("", true);

      expect(result).toEqual([]);

      result = filter(null, false);

      expect(result).toEqual([]);

      result = filter(null, true);

      expect(result).toEqual([]);

      result = filter([], false);

      expect(result).toEqual([]);

      result = filter([], true);

      expect(result).toEqual([]);

      result = filter({}, false);

      expect(result).toEqual([]);

      result = filter({}, true);

      expect(result).toEqual([]);
    });

    it("return array with duplicate removed for active", function () {
      var result;
      var duplicated = [ dataEmbargo1, dataEmbargo2, dataEmbargo1, dataEmbargo3 ];

      result = filter(duplicated, true);

      expect(result.length).toBe(2);
    });

    it("return array with duplicate removed for inactive", function () {
      var result;
      var duplicated = [ dataEmbargo3, dataEmbargo2, dataEmbargo3, dataEmbargo1 ];

      result = filter(duplicated, false);

      expect(result.length).toBe(1);
    });
  });
});
