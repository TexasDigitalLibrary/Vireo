var apiMapping = {
	availableDocumentType: {
		all: {
			endpoint: '/private/queue', 
			controller: 'settings/document-types', 
			method: 'all'
		},
		listen: {
			endpoint: '/channel', 
			controller: 'settings/document-types', 
			method: ''
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
			'controller': 'settings/document-types' 
		},
		reorder: {
			'endpoint': '/private/queue', 
			'controller': 'settings/document-types'
		},
		sort: {
			'endpoint': '/private/queue', 
			'controller': 'settings/document-types'
		}
	},
	customActionSetting: {
		all: {
			endpoint: '/private/queue', 
			controller: 'settings/custom-actions', 
			method: 'all'
		},
		listen: {
			endpoint: '/channel', 
			controller: 'settings/custom-actions', 
			method: ''
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
			'controller': 'settings/custom-actions' 
		},
		reorder: {
			'endpoint': '/private/queue', 
			'controller': 'settings/custom-actions'
		},
		sort: {
			'endpoint': '/private/queue', 
			'controller': 'settings/custom-actions'
		}
	},
	depositLocation: {
		all: {
			endpoint: '/private/queue', 
			controller: 'settings/deposit-locations', 
			method: 'all'
		},
		listen: {
			endpoint: '/channel', 
			controller: 'settings/deposit-locations', 
			method: ''
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
			'controller': 'settings/deposit-locations' 
		},
		reorder: {
			'endpoint': '/private/queue', 
			'controller': 'settings/deposit-locations'
		},
		sort: {
			'endpoint': '/private/queue', 
			'controller': 'settings/deposit-locations'
		}
	},
	language: {
		all: {
			endpoint: '/private/queue', 
			controller: 'settings/languages', 
			method: 'all'
		},
		listen: {
			endpoint: '/channel', 
			controller: 'settings/languages', 
			method: ''
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
			'controller': 'settings/languages' 
		},
		reorder: {
			'endpoint': '/private/queue', 
			'controller': 'settings/languages'
		},
		sort: {
			'endpoint': '/private/queue', 
			'controller': 'settings/languages'
		},
		proquest: {
			'endpoint': '/private/queue', 
			'controller': 'settings/languages', 
			'method': 'proquest'
		}
	}
}