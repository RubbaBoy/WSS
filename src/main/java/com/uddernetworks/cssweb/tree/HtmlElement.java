package com.uddernetworks.cssweb.tree;

import java.util.*;

public class HtmlElement {

    private HtmlElement parent;
    private List<HtmlElement> children;
    private String type;
    private String id;
    private List<String> classes = new ArrayList<>();
    private Map<String, String> properties = new HashMap<>();
    private Map<String, String> attributes = new HashMap<>();

    public HtmlElement() {
        this(null);
    }

    public HtmlElement(String type) {
        this(null, type);
    }

    public HtmlElement(HtmlElement parent, String type) {
        this(parent, new ArrayList<>(), type);
    }

    public HtmlElement(HtmlElement parent, List<HtmlElement> children, String type) {
        this.parent = parent;
        this.children = children;
        this.type = type;
    }

    public HtmlElement getParent() {
        return parent;
    }

    public List<HtmlElement> getChildren() {
        return children;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void addClass(String clazz) {
        this.classes.add(clazz);
    }

    public void addClasses(List<String> classes) {
        this.classes.addAll(classes);
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void addProperty(String name, String value) {
        this.properties.put(name, value);
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public void addAttribute(String name, String value) {
        this.attributes.put(name, value);
    }

    public void addChild(HtmlElement child) {
        if (child != null) this.children.add(child);
    }

    public String getStringAttributes() {
        if (this.attributes.isEmpty()) return "";
        var builder = new StringBuilder(" ");
        this.attributes.forEach((name, value) -> {
            builder.append(name).append("\"").append(value).append("\" ");
        });
        return builder.toString();
    }

    public String getStringClasses() {
        if (this.classes.isEmpty()) return "";
        var builder = new StringBuilder(" class=\"");
        for (int i = 0; i < this.classes.size(); i++) {
            builder.append(this.classes.get(i));
            if (this.classes.size() - 1 != i) builder.append(" ");
        }
        builder.append("\"");
        return builder.toString();
    }

    public String getStringId() {
        if (this.id == null || this.id.isBlank()) return "";
        return " id=\"" + this.id + "\"";
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        var hasChildren = !children.isEmpty();
        builder.append("<").append(type).append(getStringId()).append(getStringClasses()).append(getStringAttributes()).append(">").append(hasChildren ? "\n" : "");
        if (properties.containsKey("content")) builder.append(hasChildren ? "    " : "").append(properties.get("content")).append(hasChildren ? "\n" : "");

        for (var child : children) {
            var string = child.toString();
            for (String line : string.split("\n")) {
                builder.append("  ").append(line).append("\n");
            }
        }

        builder.append("</").append(type).append(">\n");
        return builder.toString();
    }
}
