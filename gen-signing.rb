#!/usr/bin/env ruby

print "Keystore path: "
keystore_path = gets.chomp

print "Keystore password: "
keystore_password = gets.chomp

print "Key alias: "
key_alias = gets.chomp

print "Key password: "
key_password = gets.chomp

contents = <<eos
android {
    signingConfigs {
        release {
            storeFile file("#{keystore_path}")
            storePassword "#{keystore_password}"
            keyAlias "#{key_alias}"
            keyPassword "#{key_password}"
        }
    }
}
eos

File.open("signing.gradle", "w+") do |file|
	file.write contents
end

puts "Generated signing.gradle with contents: \n"
puts contents