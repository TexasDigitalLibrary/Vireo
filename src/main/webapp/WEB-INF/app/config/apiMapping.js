// CONVENTION: must match model name, case sensitive
var apiMapping = {
	AvailableDocumentType: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/document-types',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/document-types',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/document-types',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/document-types',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/document-types',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/document-types',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/document-types',
			'method': ''
		}
	},
	CustomActionSetting: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/custom-actions',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/custom-actions',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/custom-actions',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/custom-actions',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/custom-actions',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/custom-actions',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/custom-actions',
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
			'controller': 'settings/deposit-locations',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/deposit-locations',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/deposit-locations',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/deposit-locations',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/deposit-locations',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/deposit-locations',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/deposit-locations',
			'method': ''
		}
	},
	EmailTemplate: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/email-templates',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/email-templates',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/email-templates',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/email-templates',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/email-templates',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/email-templates',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/email-templates',
			'method': ''
		}
	},
	GraduationMonth: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/graduation-months',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/graduation-months',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/graduation-months',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/graduation-months',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/graduation-months',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/graduation-months',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/graduation-months',
			'method': ''
		}
	},
	Language: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/languages',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/languages',
			'method': ''
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/languages',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/languages',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/languages',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/languages',
			'method': ''
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/languages',
			'method': ''
		},
		proquest: {
			'endpoint': '/private/queue',
			'controller': 'settings/languages',
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