var mockDepositLocationRepo1 = [
    {
        "id": 1,
        "position": 1,
        "name": "Test0",
        "repository": "Dspace",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "TDL",
        "packager": "VireoExport",
        "depositor": "Sword1Deposit",
        "timeout": 100
    },
    {
        "id": 2,
        "position": 2,
        "name": "Test1",
        "repository": "Fedora",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "Texas A&M",
        "packager": "VireoExport",
        "depositor": "Sword1Deposit",
        "timeout": 200
    },
    {
        "id": 3,
        "position": 3,
        "name": "Test2",
        "repository": "Nuxio",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "Texas A&M",
        "packager": "VireoExport",
        "depositor": "FileDeposit",
        "timeout": 300
    }
];

var mockDepositLocationRepo2 = [
    {
        "id": 1,
        "position": 1,
        "name": "Test3",
        "repository": "Dspace",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "TDL",
        "packager": "VireoExport",
        "depositor": "Sword1Deposit",
        "timeout": 100
    },
    {
        "id": 2,
        "position": 2,
        "name": "Test4",
        "repository": "Fedora",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "Texas A&M",
        "packager": "VireoExport",
        "depositor": "Sword1Deposit",
        "timeout": 200
    },
    {
        "id": 3,
        "position": 3,
        "name": "Test5",
        "repository": "Nuxio",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "Texas A&M",
        "packager": "VireoExport",
        "depositor": "FileDeposit",
        "timeout": 300
    }
];

var mockDepositLocationRepo3 = [
    {
        "id": 1,
        "position": 1,
        "name": "Test3",
        "repository": "Dspace",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "TDL",
        "packager": "VireoExport",
        "depositor": "Sword1Deposit",
        "timeout": 100
    },
    {
        "id": 2,
        "position": 2,
        "name": "Test2",
        "repository": "Fedora",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "Texas A&M",
        "packager": "VireoExport",
        "depositor": "Sword1Deposit",
        "timeout": 200
    },
    {
        "id": 3,
        "position": 3,
        "name": "Test1",
        "repository": "Nuxio",
        "collection": null,
        "username": "test@tdl.org",
        "password": "abc123",
        "onBehalfOf": "Texas A&M",
        "packager": "VireoExport",
        "depositor": "FileDeposit",
        "timeout": 300
    }
];


angular.module('mock.depositLocationRepo', []).
    service('DepositLocationRepo', function($q) {
    	
    	var self;
    	
        
        return DepositLocationRepo;
});