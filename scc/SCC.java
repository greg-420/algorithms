package scc;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Kossaraju's two-pass algorithm
 * Computes the strongly connected components of a directed graph
 * Input: text file containing edges, to and fro
 * Output: Largest cuts of the graph
 */

public class SCC {

  public static class Edge {
    int from;
    int to;

    public Edge(int from, int to) {
      this.from = from;
      this.to = to;
    }

    public String toString() {
      return "from: " + from + ", to: " + to;
    }
  }

  public static int MAX_VERTEX;

  public static boolean[] explored;

  public static int[] leader;

  // finishing times
  public static int[] f;

  public static int t = 0;

  public static int s = -1;

  public static void main(String[] args) {

    try {
      run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void printState() {
    System.out.println(String.format("max vertex: %d", MAX_VERTEX));
    System.out.println();
    int[] distinctLead = Arrays.stream(leader).distinct().toArray();
    System.out.println("cuts: " + distinctLead.length);

    // count largest cuts
    List<Integer> cutLengths = new ArrayList<Integer>();
    for (int i = 0; i < distinctLead.length; i++) {
      int comp = distinctLead[i];
      int count = (int) Arrays.stream(leader).filter(n -> n == comp).count();
      cutLengths.add(count);
    }

    cutLengths.sort((x,y) -> y - x);

    while (cutLengths.size() < 5)
      cutLengths.add(0);

    System.out.println(cutLengths.subList(0, 5));
  }

  public static void run() throws IOException {

    Path path = Paths.get("SCC.txt");
    List<String> read = Files.readAllLines(path);
    int max = 0;
    List<Edge> graph = new ArrayList<>();
    List<Edge> graphRev = new ArrayList<>();

    for (String line : read) {
      if (line.isBlank()) {
        continue;
      }
      String[] vertexStrings = line.split(" ");

      // convert to 0-based
      int from = Integer.parseInt(vertexStrings[0]) - 1;
      int to = Integer.parseInt(vertexStrings[1]) - 1;
      Edge edge = new Edge(from, to);
      Edge revEdge = new Edge(to, from);

      max = Math.max(max, Math.max(from, to));

      graph.add(edge);
      graphRev.add(revEdge);
    }
    

    MAX_VERTEX = max;
    explored = new boolean[MAX_VERTEX + 1];
    leader = new int[MAX_VERTEX + 1];
    f = new int[MAX_VERTEX + 1];
    dfsLoop(makeGraphMap(graphRev));
    System.out.println("finished 1st pass");
    printState();
    // run it again
    explored = new boolean[MAX_VERTEX + 1];
    dfsLoopSecond(makeGraphMap(graph));
    printState();
  }


  public static void dfsLoop(Map<Integer, List<Integer>> graph) {
    // difference with the alg: use 0-base instead of 1-base
    for (int i = MAX_VERTEX; i > -1; i--) {
      if (!explored[i]) {
        s = i;
        dfs(graph, i, false);
      }
    }
  }


  public static void dfsLoopSecond(Map<Integer, List<Integer>> graph) {
    int[] fInv = invert(f);
    for (int i = fInv.length - 1; i > -1; i--) {
      int currNode = fInv[i];
      if (!explored[currNode]) {
        s = currNode;
        dfs(graph, currNode, true);
      }
    }
  }

  public static void dfs(Map<Integer, List<Integer>> graph, int i, boolean isSecond) {
    explored[i] = true;
    // leaders in second pass
    if (isSecond)
      leader[i] = s;
    List<Integer> children = graph.get(i);
    if (children != null) {
      for (Integer child : children) {
        if (!explored[child]) {
          dfs(graph, child, isSecond);
        }
      }
    }

    // finishing times in first pass
    if (!isSecond)
      f[i] = t;
    t++;
  }

  private static int[] invert(int[] xs) {
    int[] xsInv = new int[xs.length];
    for (int i = 0; i < xs.length; i++) {
      xsInv[xs[i]] = i;
    }
    return xsInv;
  }

  public static Map<Integer, List<Integer>> makeGraphMap(List<Edge> graph) {
    Map<Integer, List<Integer>> map = new HashMap<>();
    for (Edge e : graph) {
      if (map.containsKey(e.from)) {
        map.get(e.from).add(e.to);
      } else {
        List<Integer> tempList = new ArrayList<>();
        tempList.add(e.to);
        map.put(e.from, tempList);
      }
    }
    return map;
  }


  // optimize child edge (i,j) lookup

}

