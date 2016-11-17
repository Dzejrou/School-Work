#!/bin/env ruby

# Implementation of the Knuth-Morris-Pratt (KMP) algorithm.
# Athor: Dzejrou 2015
class KMP
  attr_accessor :case_sensitive
  #Brief Initializes all necessary variables.
  def initialize(target)
    @state = 0
    @case_sensitive = false
    construct(target)
  end

  #Brief Constructs a specific automate for given word.
  #Param Word to be searched for.
  def construct(w)
    @word = "~#{w}" # Assimilate!
    @length = @word.length - 1 # Do not count the ~.
    @back = Array.new
    @back << 0 
    @back << 0
    s = 0
    (2..@length).each do |i|
      s = step(s,@word[i - 1])
      @back << s
    end
  end

  #Brief Transforms state based on a found letter.
  #Param Current state of the find function.
  #Param Current character in the text.
  def step(s,c)
    until @word[s + 1] == c or s == 0 do
      s = @back[s] # Backtrack if necessary.
    end

    if @word[s + 1] == c
      s += 1 # Move forvard if match found.
    end
    s # Return the new state.
  end

  #Brief Finds and prints starting indexes of all occurencies
  #      of @word in given text.
  #Param Text to be searched.
  def find(t)
    text = t
    text.downcase! unless @case_sensitive
    text.gsub!(/\s+/,' ') # Get rid of multiple spaces.
    @state = 0
    index = 0
    text.each_char do |char|
      # Incrementing now so that I can announce index - @length.
      index += 1
      @state = step(@state,char)
      if @state == @length # Yay, we've got ourselves a match!
        puts "Match found for #{@word[1,@length]} at #{index - @length}"
        @state = 0
      end
    end
  end
end

if __FILE__ == $0 # Test area.
  input = ""
  if ARGV.length >= 1 # Text file given.
    file = File.open(ARGV[0],"r")
    input = file.read
  else # Just a dummy string.
    puts "No input file given, using a dummy string."
    input = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.
             In vel metus magna. Suspendisse eget nunc tellus.
             Phasellus dapibus pulvinar tristique. Cras dictum mattis pharetra.
             Nullam nunc quam, lacinia vel vestibulum et, tincidunt vel lectus.
             Praesent non ex in turpis tempor sollicitudin id quis justo.
             Suspendisse sodales blandit dui, eu feugiat sapien."
  end

  puts "Enter word to be searched for: "
  word = $stdin.gets.chomp
  kmp = KMP.new(word)
  puts # Delimiter.
  kmp.find(input)
end
