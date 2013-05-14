// Sets the require.js configuration for your application.
require.config({
	waitSeconds : 30,
	urlArgs : "v=20130319",
	// Alias names
	paths : {
		// Core Libraries
		"jquery" : "lib/jquery/jquery-1.9.1",
		"bootstrap" : "lib/bootstrap/bootstrap",
		"underscore" : "lib/underscore/underscore",
		"backbone" : "lib/backbone/backbone",
		"text" : "lib/require/text",
		// Plugins
		"jquery.ui" : "lib/jquery.ui/jquery-ui",
		"jquery.validate" : "lib/jquery.validate/jquery.validate",
		"jquery.dynatree" : "lib/jquery.dynatree/jquery.dynatree-1.2.4",

		// Platon Application
		"application" : "js/application",
	},
	shim : {
		"underscore" : {
			"exports" : "_"
		},
		"backbone" : {
			"deps" : [ "underscore", "jquery" ],
			"exports" : "Backbone" // attaches "Backbone" to the window object
		},
		"bootstrap" : [ "jquery" ],
		"jquery.ui" : [ "jquery" ],
		"jquery.validate" : [ "jquery" ],
		"jquery.dynatree" :  [ "jquery", "jquery.ui"],
	}

});