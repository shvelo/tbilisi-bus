#!/usr/bin/env node

var fs = require('fs'),
  data = fs.readFileSync("stops.json"),
  data_en = fs.readFileSync("stops-en.json"),
  json_data = JSON.parse(data),
  json_data_en = JSON.parse(data_en),
  stops_map = {},
  out_data = [];

for (var i in json_data.Stops) {
  stops_map[json_data.Stops[i].StopId] = json_data.Stops[i];
}
for (var i in json_data_en.Stops) {
  var stop = json_data_en.Stops[i];
  if (stops_map[stop.StopId]) {
    stops_map[stop.StopId].Name_en = stop.Name;
  }
}

for (var i in stops_map) {
  var stop = stops_map[i];

  if (stop.Type != "bus" || !parseInt(stop.StopId)) continue;

  var name = stop.Name.replace(/ ?- \[[0-9]+\]/ig, "");
  console.log(name);

  out_data.push({
    lat: parseFloat(stop.Lat),
    lon: parseFloat(stop.Lon),
    name: name,
    name_en: stop.Name_en,
    id: stop.StopId
  });
}

fs.writeFileSync("db.json", JSON.stringify(out_data));
