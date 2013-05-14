// Overrride some backbone functions to provide holders for (1) caching and (2) loading display
BackboneCache = {};

Backbone.cache = function() {
};

Backbone.cache.exists = function(url) {
	var _ref;
	if (((_ref = window.BackboneCache) != null ? _ref[url] : void 0) != null) {
		return true;
	} else {
		return false;
	}
};

Backbone.cache.set = function(url, json, status, xhr) {
	var time;
	time = new Date().getTime();
	window.BackboneCache[url] = {};
	window.BackboneCache[url].url = url;
	window.BackboneCache[url].time = time;
	window.BackboneCache[url].json = json;
	window.BackboneCache[url].status = status;
	return window.BackboneCache[url].xhr = xhr;
};

Backbone.cache.get = function(url) {
	var _ref;
	return (_ref = window.BackboneCache) != null ? _ref[url] : void 0;
};

Backbone.cache.clear = function(url) {
	if (window.BackboneCache[url] != null) {
		return delete window.BackboneCache[url];
	}
};

Backbone.cache.expire = function(url, expiry) {
	var cache, now;
	if (Backbone.cache.exists(url)) {
		now = new Date().getTime();
		cache = Backbone.cache.get(url);
		if ((cache.time + expiry) < now) {
			return Backbone.cache.clear(url);
		}
	}
};

Backbone._sync = Backbone.sync;
Backbone.sync = function(method, model, options) {
	var cache, success, type, url;
	var methodMap = {
		'create' : 'POST',
		'update' : 'PUT',
		'delete' : 'DELETE',
		'read' : 'GET'
	};
	type = methodMap[method];
	if (!(model != null) || !(model['url'] != null)) {
		url = null;
	} else if (_.isFunction(model['url'])) {
		url = model['url']();
	} else {
		url = model['url'];
	}
	
	if (type === 'GET' && (url != null)) {
		if ((options.cache != null) && !options.cache) {
			Backbone.cache.clear(url);
		}
		if (options.expiry != null) {
			Backbone.cache.expire(url, options.expiry);
		}
		if (Backbone.cache.exists(url)) {
			cache = Backbone.cache.get(url);
			options.success(cache.json, cache.status, cache.xhr);
			return;
		}
		success = options.success;
		options.success = function(resp, status, xhr) {
			Backbone.cache.set(url, resp, status, xhr);
			return success(resp, status, xhr);
		};
	}
	return Backbone._sync(method, model, options);
};