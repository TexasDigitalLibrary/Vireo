describe("filter: uniqueSubmissionTypeList", function () {
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

      filter = _$filter_("uniqueSubmissionTypeList");
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

      result = filter("");

      expect(result).toEqual([]);

      result = filter(null);

      expect(result).toEqual([]);

      result = filter([]);

      expect(result).toEqual([]);

      result = filter({});

      expect(result).toEqual([]);
    });

    it("return array with duplicate removed", function () {
      var result;
      var duplicated = [ dataFieldValue1, dataFieldValue2, dataFieldValue1 ];

      result = filter(duplicated, true);

      expect(result.length).toBe(2);
    });
  });
});
