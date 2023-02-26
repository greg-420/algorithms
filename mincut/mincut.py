import random

'''
Karger's Minimum Cut Algorithm

Given an undirected graph G = (V,E), compute a cut with the fewest number of crossing edges.
'''


# given a lines of the form (u, v1, v2,..., vn), output a list of vertices and its edges.
def build_graph(lines):
    vertices = []
    edges = []
    for line in lines:
        if line is None:
            continue
        ints = line.split()
        vertex = int(ints[0])
        vertices.append(vertex)
        for i in ints[1:]:
            if int(i) not in vertices:
                edges.append([vertex, int(i)])
    return vertices, edges


def contract(graph):

    vertices, edges = graph

    while len(vertices) > 2:

        # select two vertices to merge uniformly at random
        u, v = edges.pop(random.randrange(len(edges)))
        vertices.pop(vertices.index(v))

        # update references
        for e in edges:
            if e[0] == v:
                e[0] = u
            if e[1] == v:
                e[1] = u

        # remove self-loops
        edges[:] = [e for e in edges if e[0] != e[1]]

    return len(edges)


def min_cut(graph):
    vertices = graph[0]
    results = []
    for i in range(len(vertices) * len(vertices)):

        if i % 100 == 0:
            print('iteration', i)

        # make copies of graph vertices and edges
        graph_copy = (graph[0][:], [edge[:] for edge in graph[1]])
        results.append(contract(graph_copy))

    print('end: ', sorted(results))


with open('kargerMinCut.txt') as f:
    min_cut(build_graph(f.readlines()))