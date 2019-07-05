package com.github.marcoral.versatia.core.impl.algorithms;

import java.util.*;
import java.util.function.BiConsumer;

public class TextualNodeDependencyResolver<K> {
    //Building graph
    private Map<K, Node> nodes = new HashMap<>();
    public void newEntry(K key, String value, String referenceKey) {
        if(nodes.put(key, new Node(key, value, referenceKey)) != null)
            throw new RuntimeException(String.format("There is more than 1 template which uses key %s!", key));
    }

    //Solving graph
    public void resolve(BiConsumer<K, String> resultConsumer) {
        buildDependencies();
        nodes.forEach((key, value) -> resolveRecurrent(value));
        nodes.forEach((key, value) -> resultConsumer.accept(key, value.value));
    }

    private void buildDependencies() {
        for (Node thisNode : nodes.values()) {
            for(Node secondNode : nodes.values())
                thisNode.checkDependency(secondNode);
        }
    }

    private void resolveRecurrent(Node node) {
        node.referenced = true;
        for(Node nodeRec : node.dependencies)
            if (!nodeRec.solved)
                if(nodeRec.referenced)
                    throw new RuntimeException(String.format("Circular dependency found for keys: %s and %s", node.ref, nodeRec.ref));
                else
                    resolveRecurrent(nodeRec);
        node.solve();
    }

    private class Node {
        private final K key;
        private final String ref;
        private final List<Node> dependencies = new LinkedList<>();
        private String value;
        private boolean referenced;
        private boolean solved;

        public Node(K key, String value, String ref) {
            this.key = key;
            this.value = value;
            this.ref = ref;
        }

        public void checkDependency(Node second) {
            if(value.contains(second.ref))
                dependencies.add(second);
        }

        public void solve() {
            for(Node dependency : dependencies)
                value = value.replace(dependency.ref, dependency.value);
            solved = true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            @SuppressWarnings("unchecked")
			Node node = (Node) o;
            return key.equals(node.key) &&
                    dependencies.equals(node.dependencies);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
}