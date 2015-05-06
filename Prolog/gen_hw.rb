#!/bin/env ruby
# Generates new homework file.

# Get new file number:
num = 1
fname = "HW0" + num.to_s + ".pl"
while( File.exist? fname) do
  num = num + 1
  fname = "HW"
  if (num < 10)
    fname = fname + "0"
  end
  fname = fname + num.to_s + ".pl"
end

# Contents of the file:
header = "% Homework #" + num.to_s + ":"
reload = "reload :- consult('" + fname + "') % Reloads this file."
body = "% Homework solution:"
tests = "% Tests:"
output = "correct(X) :- write('Correct: '), write(X). % Correct output."

file = File.new(fname,"w")
file.puts header
file.puts reload
file.puts
file.puts body
file.puts
file.puts tests
file.close
