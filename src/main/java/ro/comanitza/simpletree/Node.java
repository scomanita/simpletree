package ro.comanitza.simpletree;

import java.util.Map;

class Node {
    private String fieldName;
    private String transportation;
    private Map<String, Node> children;

    Node () {}

    Node (final String transportation) {
        this.transportation = transportation;
    }

    String getFieldName() {
        return fieldName;
    }

    void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    String getTransportation() {
        return transportation;
    }

    void setTransportation(String transportation) {
        this.transportation = transportation;
    }

    Map<String, Node> getChildren() {
        return children;
    }

    void setChildren(Map<String, Node> children) {
        this.children = children;
    }

    public String toString () {
        if (children == null) {
            return '[' + transportation + ']';
        }

        return children.toString();
    }
}
