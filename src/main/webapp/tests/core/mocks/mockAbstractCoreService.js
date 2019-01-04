var mockService = function ($q, mockModelCtor) {
    var service = {};

    service.mockModel = function(toMock) {
        if (typeof mockModelCtor === "function") {
            var mocked = new mockModelCtor($q);
            mocked.mock(toMock);
            return mocked;
        }

        return toMock;
    };

    return service;
};
