#!/usr/bin/env ruby

begin
	gem 'nokogiri'
rescue Gem::LoadError
	puts "Nokogiri not installed, install it via following command:"
	puts " sudo gem install nokogiri"
	exit 1
end

require 'nokogiri'

debug = ARGV[0] == "-d"

count = 0

puts "Parsing XML"

doc = Nokogiri::XML(File.open("stops.xml"))
sql = ""

puts "Parsed XML"

doc.css("Stops").each do |node|
	next if node.css("Type").first.content != "bus"
	id = node.css("StopId").first.content
	name = node.css("Name").first.content
	name = name.split(" - ")[0]
	lat = node.css("Lat").first.content
	lon = node.css("Lon").first.content
	has_board = (node.css("HasBoard").first.content == "true") ? 1 : 0
	has_data = (node.css("Virtual").first.content == "true") ? 1 : 0
    
    sql_record = "INSERT INTO \"stops\" VALUES('#{name}',#{has_data},#{lat},#{lon},#{id},#{has_board});\n"
    sql += sql_record

	puts "#{id} #{name} #{lat} #{lon}" if debug
	puts sql_record if debug
	count += 1
end

puts "Processed #{count} records"
puts "Writing SQL file"
File.open "db.sql", "w+" do |file|
	file.write sql
end
puts "Success! db.sql generated."