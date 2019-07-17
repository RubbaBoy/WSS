package com.uddernetworks.wss.tree;

import java.util.*;
import java.util.stream.Collectors;

public class HtmlElement {

    private HtmlElement parent;
    private List<HtmlElement> children;
    private String type;
    private String raw;
    private String id;
    private List<String> classes = new ArrayList<>();
    private Map<String, String> properties = new HashMap<>();
    private Map<String, String> attributes = new HashMap<>();

    private static final List<String> RESERVED_PROPERTIES = List.of("content", "content-*", "*-html");

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

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
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
            builder.append(name).append("=\"").append(value).append("\" ");
        });
        return builder.toString().replaceAll("\\s+$",""); // Right trim
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

    public String getStringStyles() {
        var styles = this.properties.keySet().stream().filter(name ->
                RESERVED_PROPERTIES.stream().noneMatch(reserved -> { // True to remove
            if (reserved.equals(name)) return true;
            if (reserved.startsWith("*") && name.endsWith(reserved.replace("*", ""))) return true;
            if (reserved.endsWith("*") && name.startsWith(reserved.replace("*", ""))) return true;
            return false;
        })).collect(Collectors.toList());
        System.out.println("properties = " + properties);

        if (styles.isEmpty()) return "";
        var builder = new StringBuilder(" style=\"");
        styles.forEach(name -> builder.append(name).append(": ").append(this.properties.get(name)).append(";"));
        return builder.append("\"").toString();
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        var allContent = new LinkedList<String>();
        properties.keySet().stream().filter(property -> property.startsWith("content-")).sorted(Comparator.comparingInt(string -> -Integer.parseInt(string.substring(8)))).forEach(allContent::push);

        var childrenStack = new LinkedList<>(children);

        var addingOrder = new ArrayList<>();
        var previous = 0;
        for (var content : allContent) {
            var index = Integer.parseInt(content.substring(8));
            var addElement = index - previous;
            for (; addElement > 0; addElement--) {
                if (childrenStack.isEmpty()) break;
                addingOrder.add(childrenStack.pop());
                previous++;
            }

            previous++;
            addingOrder.add(content);
        }

        while (!childrenStack.isEmpty()) addingOrder.add(childrenStack.pop());

        var addNewlines = !addingOrder.isEmpty();

        builder.append("<").append(type).append(getStringId()).append(getStringClasses()).append(getStringAttributes()).append(getStringStyles()).append(">").append(addNewlines ? "\n" : "");

        if (raw != null) {
            builder.append(raw);
        } else {
            if (properties.containsKey("content"))
                builder.append(getContentWrapper(properties.get("content"), !allContent.isEmpty()));

            for (Object obj : addingOrder) {
                if (obj instanceof String) {
                    builder.append(getContentWrapper(this.properties.get(obj), addNewlines));
                } else {
                    var childHtml = (HtmlElement) obj;
                    var string = childHtml.toString();
                    for (String line : string.split("\n")) {
                        builder.append("  ").append(line).append("\n");
                    }
                }
            }
        }

        builder.append("</").append(type).append(">\n");
        return builder.toString();
    }

    private String getContentWrapper(String content, boolean forceNewline) {
        var hasChildren = !children.isEmpty() || forceNewline;
        return (hasChildren ? "    " : "") + content + (hasChildren ? "\n" : "");
    }
}
