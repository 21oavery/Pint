package com.pint.lib;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DependencyTree {
    private static class Node {
        Dependency dep;
        ArrayList<Node> lower;
        ArrayList<Node> lowerOpt;

        Node(Dependency dIn) {
            this(dIn, new HashSet<>(), new HashMap<>());
        }

        Node(Dependency dIn, HashSet<Dependency> blockedDeps, HashMap<Dependency, Node> usedMap) throws OperationNotSupportedException {
            dep = dIn;
            lower = new ArrayList<>();
            blockedDeps.add(dIn);
            Dependency[] deps;
            try {
                deps = dIn.getDependencies();
            } catch (OperationNotSupportedException e) {
                System.err.println("[ERROR] Dependency " + dIn + " failed: could not be found");
            }
            for (Dependency d : deps) {
                if (blockedDeps.contains(d)) {
                    System.err.println("[ERROR] Circular dependency detected, failed");
                    throw new OperationNotSupportedException("Circular dependency");
                }
                lower.add(usedMap.computeIfAbsent(d, d -> {
                    try {
                        return new Node()
                    } catch (OperationNotSupportedException e) {
                        System.err.println("[ERROR] ")
                    }
                }))
            }
            lowerOpt = new ArrayList<>();
        }

        @Override
        public int hashCode() {
            return dep.hashCode();
        }
    }

    public DependencyTree(Dependency d) {
        HashMap<Dependency, Node> nodeMap;
    }
}