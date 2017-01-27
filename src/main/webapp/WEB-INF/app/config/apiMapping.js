// CONVENTION: must match model name, case sensitive
var apiMapping = {
	AttachmentType: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/attachment-type',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/attachment-type'
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/attachment-type',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/attachment-type',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/attachment-type',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/attachment-type'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/attachment-type'
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
			'endpoint': '/channel', 
			'controller': 'settings/controlled-vocabulary', 
			'method': 'change'
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
	CustomActionDefinition: {
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
	Configuration: {
		lazy: true,
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
			'endpoint': '/private/queue',
			'controller': 'settings/configurable',
			'method': 'reset'
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
		}
	},
	FieldPredicate: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-predicates',
			'method': 'all'
		},
		one: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-predicates'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/field-predicates'
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/field-predicates',
			'method': 'create'
		}
	},
	FieldValue: {
		update: {
			'endpoint': '/private/queue',
			'controller': 'submission'
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
	FieldProfile: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'field-profile',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'field-profile'
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
	Note: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'note',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'note'
		}
	},
	Organization: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'organization',
			'method': 'all'
		},
		addEmailWorkflowRule: {
			'endpoint': '/private/queue',
			'controller': 'organization',
			'metod': 'add-email-workflow-rule'
		},
		editEmailWorkflowRule: {
			'endpoint': '/private/queue',
			'controller': 'organization',
			'metod': 'edit-email-workflow-rule'	
		},
		get: {
			'endpoint': '/private/queue',
			'controller': 'organization'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'organization',
			'method': 'delete'
		},
		removeEmailWorkflowRule: {
			'endpoint': '/private/queue',
			'controller': 'organization',
			'method': 'remove-email-workflow-rule'
		},
		changeEmailWorkflowRuleActivation: {
			'endpoint': '/private/queue',
			'controller': 'organization',
			'method': 'change-email-workflow-rule-activation'	
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'organizations'
		},
		selectiveListen: {
			'endpoint': '/channel',
			'controller': 'organization'
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'organization'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'organization',
			'method': 'update'
		},
		workflow: {
			'endpoint': '/private/queue',
			'controller': 'organization'
		},
		children: {
			'endpoint': '/private/queue',
			'controller': 'organization'
		},
		addWorkflowStep: {
			'endpoint': '/private/queue',
			'controller': 'organization'
		},
		updateWorkflowStep: {
			'endpoint': '/private/queue',
			'controller': 'organization'
		},
		reorderWorkflowStep: {
			'endpoint': '/private/queue',
			'controller': 'organization'
		},
		deleteWorkflowStep: {
			'endpoint': '/private/queue',
			'controller': 'organization'
		}
	},
	OrganizationCategory: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'settings/organization-category',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'settings/organization-category'
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'settings/organization-category',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'settings/organization-category',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'settings/organization-category',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'settings/organization-category'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'settings/organization-category'
		}
	},
	Submission: {
		lazy: true,
		all: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'all'
		},
		assignTo: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'assign'
		},
		changeStatus: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'change-status'
		},
		submitDate: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'submit-date'
		},
		one: {
			'endpoint': '/private/queue',
			'controller': 'submission'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'delete'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'submission'
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'create'
		},
		query: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'query'
		},
		saveFieldValue: {
			'endpoint': '/private/queue',
			'controller': 'submission'
		},
		saveReviewerNotes: {
			'endpoint': '/private/queue',
			'controller': 'submission'
		},
		removeFieldValue: {
			'endpoint': '/private/queue',
			'controller': 'submission'
		},
		batchUpdateSubmissionState: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'batch-update-state'
		},
		batchAssignTo: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'batch-assign-to'
		},
		fileInfo: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'file-info'
		},
		file: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'file'
		},
		renameFile: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'rename-file'
		},
		removeFile: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'remove-file'
		},
		needsCorrection: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'needs-correction'
		}
	},
	StudentSubmission: {
		lazy: true,
		all: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'all-by-user'
		},
		one: {
			'endpoint': '/private/queue',
			'controller': 'submission'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'submission',
			'method': 'user'
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'create'
		},
		saveFieldValue: {
			'endpoint': '/private/queue',
			'controller': 'submission'
		}
	},
	AdvisorSubmission: {
		lazy: true,
		getByHash: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'advisor-review'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'submission',
			'method': 'advisor'
		},
		file: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'file'
		},
		fileInfo: {
			'endpoint': '/private/queue',
			'controller': 'submission',
			'method': 'file-info'
		}
	},
	SubmissionListColumn: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'all-columns'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'submission-list'
		}
	},
	SubmissionState: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'submission-state',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'submission-state'
		}
	},
	ManagerSubmissionListColumn: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'columns-by-user'
		},
		pageSize: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'pagesize-by-user'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'managers-submission-list'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'update-user-columns'
		},
		reset: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'reset-user-columns'
		}
	},
	ManagerFilterColumn: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'filter-columns-by-user'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'filter-columns'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'update-user-filter-columns'
		}
	},
	User: {
		instantiate: {
			'endpoint': '/private/queue', 
			'controller': 'user', 
			'method': 'credentials'
		},
		all: {
			'endpoint': '/private/queue',
			'controller': 'user',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'user'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'user',
			'method': 'update'
		}
	},
	UserSettings: {
		lazy: true,
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
			'controller': 'workflow-step',
			'method': 'all'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'workflow-step'
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'workflow-step',
			'method': 'create'
		},
		update: {
			'endpoint': '/private/queue',
			'controller': 'workflow-step',
			'method': 'update'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'workflow-step',
			'method': 'remove'
		},
		reorder: {
			'endpoint': '/private/queue',
			'controller': 'workflow-step'
		},
		sort: {
			'endpoint': '/private/queue',
			'controller': 'workflow-step'
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
	NamedSearchFilterGroup: {
		instantiate: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'active-filters'
		},
		setFilter: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'set-active-filter'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'active-filters'
		},
		addFilter: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'add-filter-criterion'
		},
		removeFilter: {
			'endpoint': '/private/queue',
			'controller': 'submission-list'
		},
		clearFilters: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'clear-filter-criteria'
		}
	},
	SavedFilter: {
		all: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'all-saved-filter-criteria'
		},
		listen: {
			'endpoint': '/channel',
			'controller': 'saved-filters'
		},
		create: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'save-filter-criteria'
		},
		remove: {
			'endpoint': '/private/queue',
			'controller': 'submission-list',
			'method': 'remove-saved-filter'
		},
	}
}
