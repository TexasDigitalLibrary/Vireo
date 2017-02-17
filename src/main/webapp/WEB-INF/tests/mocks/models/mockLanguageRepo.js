var mockLanguageRepo1 = [
    {
        "id": 1,
        "position": null,
        "name": "English"
    },
    {
      "id": 2,
      "position": null,
      "name": "Spanish"
    },
    {
      "id": 3,
      "position": null,
      "name": "French"
    }
];

var mockLanguageRepo2 = [
    {
        "id": 1,
        "position": null,
        "name": "English"
    },
    {
      "id": 2,
      "position": null,
      "name": "Chinese"
    },
    {
      "id": 3,
      "position": null,
      "name": "French"
    }
];

var mockLanguageRepo3 = [
    {
        "id": 1,
        "position": null,
        "name": "English"
    },
    {
      "id": 2,
      "position": null,
      "name": "Spanish"
    },
    {
      "id": 3,
      "position": null,
      "name": "German"
    }
];

angular.module('mock.languageRepo', []).
    service('LanguageRepo', function($q) {

      var self;


        return LanguageRepo;
});
