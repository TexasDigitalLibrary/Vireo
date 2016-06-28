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