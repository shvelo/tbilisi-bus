#!/usr/bin/env ruby

require './translit.rb'
require 'json'

debug = ARGV[0] == "-d"

count = 0

puts "Parsing JSON"

doc = JSON.parse(File.read("stops.json"))
sql = ""

puts "Parsed JSON"

doc["Stops"].each do |node|
	next if node["Type"] != "bus"
	id = node["StopId"]
	name = node["Name"]
	name = name.split(" - ")[0]
	name_en = translit name
	lat = node["Lat"]
	lon = node["Lon"]
	has_board = (node["HasBoard"] == "true") ? 1 : 0
	has_data = (node["Virtual"] == "true") ? 1 : 0
    
    sql_record = "INSERT INTO stops(id,name,name_en,lat,lon,hasData,hasBoard) \
VALUES(#{id},'#{name}','#{name_en}',#{lat},#{lon},#{has_data},#{has_board});\n"
    sql += sql_record

	puts "#{id} #{name} #{name_en}" if debug
	puts sql_record if debug
	count += 1
end

puts "Processed #{count} records"
puts "Writing SQL file"
File.open "db.sql", "w+" do |file|
	file.write sql
end
puts "Success! db.sql generated."