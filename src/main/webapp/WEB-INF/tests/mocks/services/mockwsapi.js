angular.module('mock.wsApi', []).
    service('WsApi', function($q) {

        var WsApi = this;

        WsApi.fetch = function(apiReq) {

          var defer = $q.defer();


            return defer.promise;
        }

        WsApi.listen = function(apiReq) {
          var defer = $q.defer();
            return defer.promise;
        }

});
