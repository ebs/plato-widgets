/*global define */
define([ "jquery", "underscore", "backbone", "bootstrap", "jquery.ui", "jquery.validate", "jquery.dynatree" ], function($, _, Backbone) {

	"use strict";

	if (!window.platon) {
		// Validator Plugin Extensions/Configurations
		$.validator.setDefaults({
			ignore : []
		});
		$.validator.addMethod("onlyLatin", function(value, element) {
			return this.optional(element) || /^[a-zA-Z0-9]*$/.test(value);
		}, "Please type only latin characters");
		$.validator.addMethod("requiredIfOtherGreek", function(value, element, param) {
			var target = $(param);
			if (this.settings.onfocusout) {
				target.unbind(".validate-requiredIfOtherGreek").bind("blur.validate-requiredIfOtherGreek", function() {
					$(element).valid();
				});
			}
			return (/^[a-zA-Z0-9]*$/.test(target.val())) || value;
		}, "Required if other field is in greek characters");

		$.validator.addMethod("pwd", function(value, element) {
			return this.optional(element) || /^[a-zA-Z0-9!@#$%^&*()]*$/.test(value);
		}, "Please type only latin characters");
		$.validator.addMethod("dateAfter", function(value, element, params) {
			var days = params[1] * 86400000; // millisecond in a day
			var beforeDate = params[0].datepicker("getDate");
			var afterDate = $(element).datepicker("getDate");
			if (_.isNull(beforeDate) || _.isNull(afterDate)) {
				return false;
			}
			return (afterDate.getTime() - beforeDate.getTime() >= days);
		}, "Date must be {1} days later than {0}");

		$.datepicker.setDefaults({
			dateFormat : "dd/mm/yy"
		});

		window.platon = {
			utils : {
				dateFromString : function(str) {
					// "dd/mm/yy HH:MM:SS"
					var m = str.match(/(\d+)\/(\d+)\/(\d+)\s+(\d+):(\d+):(\d+)/);
					return new Date(+m[3], +m[2] - 1, +m[1], +m[4], +m[5], +m[6], 0);
				},

				formatFileSize : function(bytes) {
					var precision = 2;
					var sizes = [ 'Bytes', 'KB', 'MB', 'GB', 'TB' ];
					var posttxt = 0;
					if (bytes === undefined || bytes === 0) {
						return 'n/a';
					}
					while (bytes >= 1024) {
						posttxt++;
						bytes /= 1024;
					}
					return bytes.toFixed(precision) + "" + sizes[posttxt];
				},

				// Cookies
				addCookie : function(name, value, days) {
					var date, expires;
					if (days) {
						date = new Date();
						date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
						expires = "; expires=" + date.toGMTString();
					} else {
						expires = "";
					}
					document.cookie = name + "=" + value + expires + "; path=/";
				},

				getCookie : function(name) {
					var nameEQ = name + "=";
					var ca = document.cookie.split(';');
					for ( var i = 0; i < ca.length; i++) {
						var c = ca[i];
						while (c.charAt(0) == ' ') {
							c = c.substring(1, c.length);
						}
						if (c.indexOf(nameEQ) == 0) {
							return c.substring(nameEQ.length, c.length);
						}
					}
					return null;
				},

				removeCookie : function(name) {
					window.App.util.addCookie(name, "", -1);
				}
			}
		};
	}
	return window.platon;
});