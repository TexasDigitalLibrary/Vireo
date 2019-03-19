vireo.filter('readableFileSize', function() {
	return function(bytes, precision) {
		if (typeof precision === 'undefined') precision = 0;
		var units = ['bytes', 'KB', 'MB', 'GB'],
            number = Math.floor(Math.log(bytes) / Math.log(1024));
		return Math.floor(bytes / Math.pow(1024, Math.floor(number))).toFixed(precision) +  ' ' + units[number];
	};
});