package com.pint.lib;

import com.pint.api.Task;

import java.util.HashMap;

public class DependencyTree {
    private static class Node {
        Dependency dep;
        Node[] lower;
        Node[] lowerOpt;

        public Node(Dependency dIn) {
        }
    }

    public DependencyTree(Dependency d) {
        HashMap<Dependency, Node> nodeMap;
    }

    private static Node buildNode(Dependency d, int cnt) {
        if (cnt == 512) {
            Task t = d.getTask();
            if (t.getDependencies().length != 0) {
                System.err.println("");
            }
        }
    }
}