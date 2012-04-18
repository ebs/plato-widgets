(function($) {
	$.widget("platon.line", {
		options : {
			data : {
				url : "",
				interval : 1000,
				time : "",
				max: 0,
				properties : []
			},
			chart : {
				width : 300,
				height : 300,
				redFrom : 90,
				redTo : 100,
				yellowFrom : 75,
				yellowTo : 90,
				minorTicks : 5,
				animation : {
					duration : 1000,
					easing : 'out'
				}
			}
		},
		_create : function() {
			var self = this;
			var i;
			var optionsDialog;
			// Add Class
			self.element.addClass("ui-widget platon-widget");
			// Add Header
			self.element.append("<div class='ui-widget-header platon-widget-header'><button>?</button><div>");
			self.element.find("button").button().click(function() {
				var j;
				// Fill Form
				optionsDialog.find("input[name='url']").val(self.options.data.url);
				optionsDialog.find("input[name='interval']").val(self.options.data.interval);
				optionsDialog.find("input[name='time']").val(self.options.data.time);
				optionsDialog.find("input[name='max']").val(self.options.data.max);
				for (j = 0; j < self.options.data.properties.length; j += 1) {
					optionsDialog.find("input[name='properties[" + j + "].property']").val(self.options.data.properties[j].property);
					optionsDialog.find("input[name='properties[" + j + "].name']").val(self.options.data.properties[j].name);
				}
				// Open Dialog
				optionsDialog.dialog("open");
				return false;
			});
			// Add Configuration Dialog
			self.element.append(
					"<div class='ui-widget-content platon-widget-options'>" + 
					"<form>" + 
					"<fieldset>" + 
					"<legend>Generic</legend>" + 
					"<ol>" + 
					"<li><label for='URL'>URL</label> <input type='text' name='url' /></li>" + 
					"<li><label for='interval'>Interval</label> <input type='text' name='interval' /></li>" + 
					"<li><label for='time'>Time Property</label> <input type='text' name='time' /></li>" + 
					"<li><label for='max'>Max</label> <input type='text' name='max' /></li>" +
					"</ol>" + 
					"</fieldset>" + 
					"<fieldset>" + 
					"<legend>Properties</legend>" + 
					"<ol>" + 
					"<li><label for='properties[0].name'>Name0</label> <input type='text' name='properties[0].name'/></li>" + 
					"<li><label for='properties[0].property'>Property0</label> <input type='text' name='properties[0].property' /></li>" + 
					"<li><label for='properties[1].name'>Name1</label> <input type='text' name='properties[1].name'/></li>" + 
					"<li><label for='properties[1].property'>Property1</label> <input type='text' name='properties[1].property' /></li>" + 
					"</ol>" + 
					"</fieldset>" + 
					"</form>" + 
					"</div>");
			optionsDialog = self.element.find(".platon-widget-options").dialog({
				autoOpen : false,
				modal : true,
				resizable : false,
				buttons : {
					'Save' : function() {
						// Read Form Values
						var pattern = /^(properties)\[(\d+)\]\.(\w+)$/;
						var values = {
							properties : []
						};
						var fields = $(this).find('form').serializeArray();
						$.each(fields, function(i, field) {
							var match, pos, name;
							if (pattern.test(field.name)) {
								match = pattern.exec(field.name);
								pos = match[2];
								name = match[3];
								if (typeof values.properties[pos] === 'undefined') {
									values.properties[pos] = {
										type : 'number'
									};
								}
								values.properties[pos][name] = field.value;
							} else {
								values[field.name] = field.value;
							}
						});
						self._setOption("data", values);
						self.start();
						$(this).dialog('close');
					}
				}
			});
			// Add Chart
			self.element.append("<div class='ui-widget-content platon-widget-chart'></div>");
			self.lineChart = new google.visualization.LineChart(self.element.find('.platon-widget-chart').get(0));

			self.lineData = new google.visualization.DataTable();
			self.lineData.addColumn('date', 'Time');
			for (i = 0; i < self.options.data.properties.length; i += 1) {
				self.lineData.addColumn(self.options.data.properties[i].type, self.options.data.properties[i].name);
			};
			self.lineChart.draw(self.lineData, self.options.chart);
		},
		_refresh : function() {
			var self = this;
			if (typeof self.options.data.url === "") {
				return;
			} else {
				$.get(self.options.data.url, function(response) {
					var json = $.xml2json(response);
					console.log(json);
					
					var numberFormatter = new google.visualization.NumberFormat({formatType: 'fractionDigits'});
					var dateFormatter = new google.visualization.DateFormat({pattern: "dd/MM/yyyy HH:mm:ss"});
					
					var i, property, value, row = [];
					
					//Wed Apr 04 15:51:00 EEST 2012
					var date = new Date(parseInt(eval('json.' + self.options.data.time)));
					row.push(date);
					for (i = 0; i < self.options.data.properties.length; i += 1) {
						property = self.options.data.properties[i].property;
						value = eval('json.' + property);
						row.push(parseFloat(value));
					}
					self.lineData.addRow(row);
					if (self.lineData.getNumberOfRows() > self.options.data.max) {
						self.lineData.removeRow(0);
					}
					//Format
					dateFormatter.format(self.lineData, 0);
					for (i = 0; i < self.options.data.properties.length; i += 1) {
						numberFormatter.format(self.lineData, i + 1);						
					}
					self.lineChart.draw(self.lineData, self.options.chart);
				});
			}
		},
		_setOption : function(key, value) {
			var self = this;
			var i;
			$.Widget.prototype._setOption.apply(this, arguments);
			this.stop();
			switch (key) {
			case "chart":
				self.options.chart = value;
				break;
			case "data":
				self.options.data = value;
				self.lineData = new google.visualization.DataTable();
				self.lineData.addColumn('date', 'Time');
				for (i = 0; i < self.options.data.properties.length; i += 1) {
					self.lineData.addColumn(self.options.data.properties[i].type, self.options.data.properties[i].name);
				};
				break;
			}
			self.lineChart.draw(this.lineData, this.options.chart);
		},
		destroy : function() {
			$.Widget.prototype.destroy.call(this, arguments);
			this.stop();
			this.element.removeClass("platon-widget");
			this.element.children(":first").remove();
			delete this.lineChart;
			delete this.lineData;
			delete this.lineIntervalID;
		},
		start : function() {
			var self = this;
			self._refresh();
			this.lineIntervalID = setInterval(function() {
				self._refresh();
			}, self.options.data.interval);
		},
		stop : function() {
			if (typeof this.lineIntervalID !== 'undefined') {
				clearInterval(this.lineIntervalID);
			}
		}
	});

	$.widget("platon.gauge", {
		options : {
			data : {
				url : "",
				interval : 1000,
				properties : []
			},
			chart : {
				width : 300,
				height : 300,
				redFrom : 90,
				redTo : 100,
				yellowFrom : 75,
				yellowTo : 90,
				minorTicks : 5,
				animation : {
					duration : 1000,
					easing : 'out'
				}
			}
		},
		_create : function() {
			var self = this;
			var i;
			var optionsDialog;
			// Add Class
			self.element.addClass("ui-widget platon-widget");
			// Add Header
			self.element.append("<div class='ui-widget-header platon-widget-header'><button>?</button><div>");
			self.element.find("button").button().click(function() {
				var j;
				// Fill Form
				optionsDialog.find("input[name='url']").val(self.options.data.url);
				optionsDialog.find("input[name='interval']").val(self.options.data.interval);
				for (j = 0; j < self.options.data.properties.length; j += 1) {
					optionsDialog.find("input[name='properties[" + j + "].property']").val(self.options.data.properties[j].property);
					optionsDialog.find("input[name='properties[" + j + "].name']").val(self.options.data.properties[j].name);
				}
				// Open Dialog
				optionsDialog.dialog("open");
				return false;
			});
			// Add Configuration Dialog
			self.element.append("<div class='ui-widget-content platon-widget-options'>" + "<form>" + "<fieldset>" + "<legend>Generic</legend>" + "<ol>" + "<li><label for='URL'>URL</label> <input type='text' name='url' /></li>" + "<li><label for='interval'>Interval</label> <input type='text' name='interval' /></li>" + "</ol>" + "</fieldset>" + "<fieldset>" + "<legend>Properties</legend>" + "<ol>" + "<li><label for='properties[0].name'>Name0</label> <input type='text' name='properties[0].name'/></li>" + "<li><label for='properties[0].property'>Property0</label> <input type='text' name='properties[0].property' /></li>" + "<li><label for='properties[1].name'>Name1</label> <input type='text' name='properties[1].name'/></li>" + "<li><label for='properties[1].property'>Property1</label> <input type='text' name='properties[1].property' /></li>" + "</ol>" + "</fieldset>" + "</form>" + "</div>");
			optionsDialog = self.element.find(".platon-widget-options").dialog({
				autoOpen : false,
				modal : true,
				resizable : false,
				buttons : {
					'Save' : function() {
						// Read Form Values
						var pattern = /^(properties)\[(\d+)\]\.(\w+)$/;
						var values = {
							properties : []
						};
						var fields = $(this).find('form').serializeArray();
						$.each(fields, function(i, field) {
							var match, pos, name;
							if (pattern.test(field.name)) {
								match = pattern.exec(field.name);
								pos = match[2];
								name = match[3];
								if (typeof values.properties[pos] === 'undefined') {
									values.properties[pos] = {
										type : 'number'
									};
								}
								values.properties[pos][name] = field.value;
							} else {
								values[field.name] = field.value;
							}
						});
						self._setOption("data", values);
						self.start();
						$(this).dialog('close');
					}
				}
			});
			// Add Chart
			self.element.append("<div class='ui-widget-content platon-widget-chart'></div>");
			self.gaugeChart = new google.visualization.Gauge(self.element.find('.platon-widget-chart').get(0));
			self.gaugeData = new google.visualization.DataTable();
			self.gaugeData.addRows(1);
			for (i = 0; i < self.options.data.properties.length; i += 1) {
				self.gaugeData.addColumn(self.options.data.properties[i].type, self.options.data.properties[i].name);
				self.gaugeData.setValue(0, i, 0);
			}
			;
			self.gaugeChart.draw(self.gaugeData, self.options.chart);
		},
		_refresh : function() {
			var self = this;
			if (typeof self.options.data.url === "") {
				return;
			} else {
				$.get(self.options.data.url, function(response) {
					var json = $.xml2json(response);
					console.log(json);
					
					var formatter = new google.visualization.NumberFormat({formatType: 'fractionDigits'});
					var i, property, value;
					for (i = 0; i < self.options.data.properties.length; i += 1) {
						property = self.options.data.properties[i].property;
						value = parseFloat(eval('json.' + property));
						self.gaugeData.setValue(0, i, value);
						formatter.format(self.gaugeData, i);
					}
					self.gaugeChart.draw(self.gaugeData, self.options.chart);
				});
			}
		},
		_setOption : function(key, value) {
			var i;
			$.Widget.prototype._setOption.apply(this, arguments);
			this.stop();
			switch (key) {
			case "chart":
				this.options.chart = value;
				break;
			case "data":
				this.options.data = value;
				this.gaugeData = new google.visualization.DataTable();
				this.gaugeData.addRows(1);
				for (i = 0; i < this.options.data.properties.length; i += 1) {
					this.gaugeData.addColumn(this.options.data.properties[i].type, this.options.data.properties[i].name);
					this.gaugeData.setValue(0, i, 0);
				}
				;
				break;
			}
			this.gaugeChart.draw(this.gaugeData, this.options.chart);
		},
		destroy : function() {
			$.Widget.prototype.destroy.call(this, arguments);
			this.stop();
			this.element.removeClass("platon-widget");
			this.element.children(":first").remove();
			delete this.gaugeChart;
			delete this.gaugeData;
			delete this.gaugeIntervalID;
		},
		start : function() {
			var self = this;
			self._refresh();
			this.gaugeIntervalID = setInterval(function() {
				self._refresh();
			}, self.options.data.interval);
		},
		stop : function() {
			if (typeof this.gaugeIntervalID !== 'undefined') {
				clearInterval(this.gaugeIntervalID);
			}
		}
	});

}(jQuery));