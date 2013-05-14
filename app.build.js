({
	appDir : "./www",
	baseUrl : "./",
	dir : "./bin/www",
	optimize : "closure",
	closure : {
		CompilerOptions : {},
		CompilationLevel : 'WHITESPACE_ONLY',
		loggingLevel : 'WARNING'
	},
	inlineText : true,
	mainConfigFile : './www/main.js',
	keepBuildDir : true,
	removeCombined : true,
	skipModuleInsertion : false,
	optimizeAllPluginResources : true,
	findNestedDependencies : true,
	modules : [ {
		name : "index",
		include : [],
		exclude : []
	}, {
		name : "admin",
		include : [],
		exclude : []
	}, {
		name : "registration",
		include : [],
		exclude : []
	} ]
})