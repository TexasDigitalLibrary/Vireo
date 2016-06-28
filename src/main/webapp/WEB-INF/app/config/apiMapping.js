var apiMapping = {
		languages: {
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
			add: {
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