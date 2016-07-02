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
			'controller': 'settings/document-type'
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
			'controller': 'settings/document-type'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/document-type'
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
			'controller': 'settings/controlled-vocabulary'
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
			'controller': 'settings/controlled-vocabulary'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/controlled-vocabulary'
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
			'controller': 'settings/custom-action'
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
			'controller': 'settings/custom-action'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/custom-action'
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
			'controller': 'settings/configurable'
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
			'controller': 'settings/deposit-location'
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
			'controller': 'settings/deposit-location'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/deposit-location'
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
			'controller': 'settings/embargo'
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
			'controller': 'settings/embargo'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/embargo'
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
			'controller': 'settings/email-template'
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
			'controller': 'settings/email-template'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/email-template'
		}
	},
	FieldGloss: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-gloss',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/field-gloss'
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-gloss',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-gloss',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-gloss',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-gloss'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-gloss'
		}
	},
	FieldPredicate: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-predicates',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/field-predicates'
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-predicates',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-predicates',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-predicates',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-predicates'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-predicates'
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
			'controller': 'settings/graduation-month'
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
			'controller': 'settings/graduation-month'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/graduation-month'
		}
	},
	InputType: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/input-types',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/input-types'
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/input-types',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/input-types',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/input-types',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/input-types'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/input-types'
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
			'controller': 'settings/language'
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
			'controller': 'settings/language'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/language'
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
			'controller': 'user/settings'
		}
	},
	WorkflowStep: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/workflow-step',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/workflow-step'
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/workflow-step',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/workflow-step',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/workflow-step',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/workflow-step'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/workflow-step'
		},
		addFieldProfile: {
			'endpoint': '/private/queue',
			'controller': 'workflow-step'
		},
		updateFieldProfile: {
			'endpoint': '/private/queue',
			'controller': 'workflow-step'
		},
		removeFieldProfile: {
			'endpoint': '/private/queue', 
			'controller': 'workflow-step'
		},
		reorderFieldProfile: {
			'endpoint': '/private/queue', 
			'controller': 'workflow-step'
		},
		addNote: {
			'endpoint': '/private/queue', 
			'controller': 'workflow-step'
		},
		updateNote: {
			'endpoint': '/private/queue', 
			'controller': 'workflow-step'
		},
		removeNote: {
			'endpoint': '/private/queue', 
			'controller': 'workflow-step'
		},
		reorderNote: {
			'endpoint': '/private/queue', 
			'controller': 'workflow-step'
		}
	},
}