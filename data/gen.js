var fs = require('fs');

fs.readFile("stops.json", function(err, data){
	var json_data = JSON.parse(data);
	var out_data = [];
	json_data.Stops.forEach(function(stop){
		if(stop.Type != "bus") return;

		out_data.push({
			lat: parseFloat(stop.Lat),
			lon: parseFloat(stop.Lon),
			name: stop.Name,
			id: parseInt(stop.StopId)
		});
	});
	fs.writeFile("db.json", JSON.stringify(out_data));
});

