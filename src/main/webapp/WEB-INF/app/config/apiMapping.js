// CONVENTION: must match model name, case sensitive
var apiMapping = {
	AvailableDocumentType: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/document-type',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/document-type',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/document-type',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/document-type',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/document-type',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/document-type',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/document-type',
			'method': ''
		}
	},
	ControlledVocabulary: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/controlled-vocabulary',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/controlled-vocabulary',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/controlled-vocabulary',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/controlled-vocabulary',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/controlled-vocabulary',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/controlled-vocabulary',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/controlled-vocabulary',
			'method': ''
		},
		change: {
			endpoint: '/channel', 
			controller: 'settings/controlled-vocabulary', 
			method: 'change'
		},
		downloadCSV: {
			'endpoint': '/private/queue', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'export'
		},
		uploadCSV: {
			'endpoint': '/private/queue', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'import'
		},
		confirmCSV: {
			'endpoint': '', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'compare',
			'file': null
		},
		cancel: {
			'endpoint': '/private/queue', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'cancel'
		},
		status: {
			'endpoint': '/private/queue', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'status'
		}
	},
	CustomActionSetting: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/custom-action',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/custom-action',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/custom-action',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/custom-action',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/custom-action',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/custom-action',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/custom-action',
			'method': ''
		}
	},
	ConfigurableSetting: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/configurable',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/configurable',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/configurable',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/configurable',
			'method': 'update'
		},
		reset: {
			endpoint: '/private/queue',
			controller: 'settings/configurable',
			method: 'reset'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/configurable',
			'method': 'remove'
		}
	},
	DepositLocation: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/deposit-location',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/deposit-location',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/deposit-location',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/deposit-location',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/deposit-location',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/deposit-location',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/deposit-location',
			'method': ''
		}
	},
	Embargo: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/embargo',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/embargo',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/embargo',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/embargo',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/embargo',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/embargo',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/embargo',
			'method': ''
		}
	},
	EmailTemplate: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/email-template',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/email-template',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/email-template',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/email-template',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/email-template',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/email-template',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/email-template',
			'method': ''
		}
	},
	GraduationMonth: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/graduation-month',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/graduation-month',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/graduation-month',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/graduation-month',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/graduation-month',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/graduation-month',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/graduation-month',
			'method': ''
		}
	},
	Language: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/language',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/language',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/language',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/language',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/language',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/language',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/language',
			'method': ''
		},
		proquest: {
			'endpoint': '/private/queue',
			'controller': 'settings/language',
			'method': 'proquest'
		}
	},
	UserSettings: {
		instantiate: {
			'endpoint': '/private/queue',
			'controller': 'user',
			'method': 'settings',
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'user',
			'method': 'settings/update',
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'user/settings',
			'method': ''
		}
	}
}