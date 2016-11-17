#!/bin/env ruby

#Brief Implementation of the graph data structure.
#Author Dzejrou 2015
class Graph
  attr_reader :graph, :nodes
  #Brief Constructor.
  #Param Boolean determining if the graph is directed (default = true).
  def initialize(dir = true)
    @graph = Hash.new # {node => {edge => weight}}
    @nodes = Array.new
    @directed = dir
  end

  #Brief Adds edge to the graph. (Also adds nodes if needed.)
  #Param Source node.
  #Param Target node.
  #Param Weight of the edge.
  def add_edge(s,t,w = 0)
    # Add the edge or the weight if edge exists.
    unless @graph.has_key? s
      @graph[s] = {t => w}
    else
      @graph[s][t] = w
    end

    # If the graph is undirected, add the opposing direction.
    unless @directed
      unless @graph.has_key? t
        @graph[t] = {s => w}
      else
        @graph[t][s] = w
      end
    end

    # Add the source node if needed.
    @nodes << s unless @nodes.include? s

    # Add the target node if needed.
    @nodes << t unless @nodes.include? t
  end

  #Brief Adds a node v to the graph.
  #Param Node v.
  def add_node(v)
    @nodes << v
  end

  #Brief Deletes the edge e={s,t}.
  #Param Source node.
  #Param Target node.
  def del_edge(s,t)
    @graph[s].delete t # t is key of the nested hash.
  end

  #Brief Returns the degree of v.
  #Param Node v.
  def deg(v)
    deg = @graph[v].size # All edges will be coming out of it.

    if @directed # Count sources of incoming edges.
      @graph.each_value do |target|
        deg += 1 if target.has_key? v # Edges that have v as target.
      end
    end
    return deg # This should be the deg(v)
  end

  def neighbours(v)
    return [] if @graph[v] == nil
    tmp = Array.new
    @graph[v].each_key do |neighbour|
      tmp << neighbour
    end

    unless @directed # Count sources of incoming edges.
      @graph.each_value do |source|
        tmp << source if source.has_key? v
      end
    end
    return tmp
  end

  #Brief Returns the weight of the edge e={s,t}.
  #Param Source node s.
  #Param Target node t.
  def weight_of(s,t)
    return @graph[s][t]
  end

  #Brief Allows iterating over all nodes.
  def each_node
    @nodes.each { |v| yield v}
  end
end
