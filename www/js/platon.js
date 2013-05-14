google.load('visualization', '1.0', {
	'packages' : [ 'corechart', 'gauge' ]
});

$(document).ready(function() {
	$('#gaugeChart_div').gauge({
		data : {
			url : "/plato-server/measurement/Libelium_1",
			interval : 4000,
			properties : [ {
				name : "Temperature",
				type : "number",
				property : "result.field[1].Quantity.value"
			}, {
				name : "Temperature2",
				type : "number",
				property : "result.field[1].Quantity.value"
			} ]
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
	});
	$('#gaugeChart_div').gauge("start");

	$('#lineChart_div').line({
		data : {
			url : "/plato-server/measurement/Libelium_1",
			interval : 3000,
			time : "samplingTime.TimeInstant.timePosition",
			max : 20,
			properties : [ {
				name : "Temperature",
				type : "number",
				property : "result.field[1].Quantity.value"
			}, {
				name : "Temperature2",
				type : "number",
				property : "result.field[1].Quantity.value"
			} ]
		},
		chart : {
			width : 600,
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
	});

	$('#lineChart_div').line("start");

});