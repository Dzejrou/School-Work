#!/bin/env ruby

require_relative "../structs/graph.rb"

class AC
  attr_accessor :case_sensitive
  def initialize(words)
    @state = 0
    @words = if words.respond_to? "[]" # Ensure it is atleast one member array.
               words
             else
               [words] # Its just one word.
             end
    @case_sensitive = false
    construct(@words)
  end
  
  def construct(words)
    @words = words
    @automate = Graph.new # Automate.
    @back = Hash.new # Back function.
    @short = Hash.new # Shortcuts.
    @out = Hash.new # Outputs.

    @automate.add_node(0)
    count = 0
    @words.each do |word|
      count += 1
      @automate.add_edge(0,count,word[0]) # Link the first char.
      word[1,word.length - 1].each_char do |c|
        @automate.add_edge(count,count + 1,c)
        count += 1
      end
      @out[count] = word # Store the out function.
    end

    queue = Queue.new
    @automate.neighbours(0).each do |v|
      queue << v # Add all neighbours of the root to the queue.
      @back[v] = 0
      @short[v] = 0
    end

    until queue.empty?
      v = queue.deq
      @automate.neighbours(v).each do |u|
        z = step(@back[v], @automate.weight_of(v,u))
        @back[u] = z
        if @out[z] != ""
          @short[u] = z
        else
          @short[u] = @short[z]
        end
        queue.enq(u)
      end
    end
  end

  def forward(s,c)
    if @automate.graph[s] == nil and @automate.nodes.include? s
      return 0 # Leaf.
    end
    @automate.graph[s].each_key do |t|
      if @automate.weight_of(s,t) == c
        return t
      end
    end
    return 0
  end

  def step(s,c)
    until forward(s,c) != 0 or s == 0 do
      s = @back[s]
    end

    if forward(s,c) != 0
      s = forward(s,c)
    end
    return s
  end

  def find(t)
    text = t
    text.downcase! unless @case_sensitive
    text.gsub!(/\s+/," ") # Get rid of multiple spaces.
    s = 0
    index = 0
    text.each_char do |c|
      index += 1
      s = step(s,c)
      j = s
      while j != 0
        if @out.has_key? j and @out[j] != ""
          puts "Found '#{@out[j]}' at #{index - @out[j].length}"
        end

        j = @short[j]
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

  puts "Enter words to be searched for: "
  line = $stdin.gets.chomp
  ac = AC.new(line.split(" "))
  puts # Delimiter.
  ac.find(input)
end
