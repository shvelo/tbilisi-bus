# encoding: utf-8

def translit text
	out = text

	translit_compound_from = "ჩჭღძშჟ".split ""
	translit_compound_to = "chchghdzshzh".scan /../
	
	translit_single_from = "ქწერტთყუიოპასდფგჰჯკლზხცვბნმ".split ""
	translit_single_to = "qwerttyuiopasdpghjklzxcvbnm".split ""

	translit_compound_from.each_index do |i|
		out = out.gsub(translit_compound_from[i], translit_compound_to[i])
	end
	translit_single_from.each_index do |i|
		out = out.gsub(translit_single_from[i], translit_single_to[i])
	end

	out
end

puts translit ARGV[0] if ARGV[0]