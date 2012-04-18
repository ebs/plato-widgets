google.load('visualization', '1.0', {
	'packages' : [ 'corechart', 'gauge' ]
});

$(document).ready(function() {
	$('.chartDrag').draggable({
		connectToSortable : ".chartSortable",
		helper : "clone",
		revert : "invalid"
	});

	$("#chartColumn1, #chartColumn2, #chartColumn3").sortable({
		connectWith : ".chartSortable",
		placeholder : "ui-sortable-placeholder",
		revert : true,
		receive : function(event, ui) {
			if (ui.item.hasClass("platon-widget")) {
				return true;
			}
			if (ui.item.attr('id') === "gaugeDrag") {
				var newItem = $(this).data().sortable.currentItem;
				newItem.attr("class", "");
				newItem.empty();
				newItem.gauge({
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
				return true;
			} else if (ui.item.attr('id') === "lineDrag") {
				var newItem = $(this).data().sortable.currentItem;
				newItem.attr("class", "");
				newItem.empty();
				newItem.line({
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
				return true;
			}
			return false;
		}

	}).disableSelection();

});