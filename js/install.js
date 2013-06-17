// If on true native mobile platform,
if (!storeme || !storeme.read) {
	logger.log("{storeme} Installing JS component for native");

	var onSuccess = [];
	var onFail = [];

	NATIVE.events.registerHandler('storeme', function(evt) {
		logger.log("Got STOREME event -cat");
		if (evt.failed) {
			// Create HTML5-like error object
			var err = {
				PERMISSION_DENIED: "Permission Denied",
				POSITION_UNAVAILABLE: "Position Unavailable",
				TIMEOUT: "Timeout",
				UNKNOWN_ERROR: "Unknown Error",

				code: "Position Unavailable"
			};

			// Invoke all the callbacks
			for (var ii = 0; ii < onSuccess.length; ++ii) {
				onFail[ii](err);
			}
		} else {
			// Create HTML5-like position object
			var result = {
    		 filename: evt.Filename,
             output: evt.result
			};
           

			// Invoke all the callbacks
			for (var ii = 0; ii < onSuccess.length; ++ii) {
				onSuccess[ii](result);
			}
		}
		// Clear callbacks array
		onSuccess.length = 0;
		onFail.length = 0;
	});

	// The navigator global already exists, but the geolocation property does not.
	// So add it:

	GLOBAL.storeme = {
		read: function(filename, cbSuccess, cbFail) {
			// Post a new request
             var cmd = {method: "read", filename: filename, content: ""};
			NATIVE.plugins.sendEvent("StoreMePlugin", "onRequest", JSON.stringify(cmd));
			// Insert callbacks into waiting lists
			if (typeof(cbSuccess) == "function") {
				onSuccess.push(cbSuccess);
			}
			if (typeof(cbFail) == "function") {
				onFail.push(cbFail);
			}
		},
        write: function(filename, content, cbSuccess, cbFail) {
    		// Post a new request
            var cmd = {method: "write", filename: filename, content: content};
            
			NATIVE.plugins.sendEvent("StoreMePlugin", "onRequest", JSON.stringify(cmd));
			// Insert callbacks into waiting lists
			if (typeof(cbSuccess) == "function") {
				onSuccess.push(cbSuccess);
			}
			if (typeof(cbFail) == "function") {
				onFail.push(cbFail);
			}
		}
	}
} else {
	logger.log("{storeme} Skipping installing JS wrapper on non-native target");
}
