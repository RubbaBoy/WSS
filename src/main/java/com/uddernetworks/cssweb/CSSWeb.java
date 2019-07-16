package com.uddernetworks.cssweb;

import com.uddernetworks.cssweb.scss.ScssParser;
import com.uddernetworks.cssweb.tree.HtmlElement;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CSSWeb {

    public static void main(String[] args) throws IOException {
        new CSSWeb().main();
    }

    private void main() throws IOException {
        var input = CharStreams.fromFileName("input\\demo.scss");
        var lex = new com.uddernetworks.cssweb.scss.ScssLexer(input);
        // copy text out of sliding buffer and store in tokens
        lex.setTokenFactory(new CommonTokenFactory(true));
        var tokens = new UnbufferedTokenStream<CommonToken>(lex);
        var parser = new com.uddernetworks.cssweb.scss.ScssParser(tokens);
        parser.setBuildParseTree(true);

        var stylesheet = parser.stylesheet();

        var root = walk((ScssParser.RulesetContext) stylesheet.getChild(0).getChild(0));
        var string = root.toString();
        System.out.println("\n\n\n\n" + string);

        var path = Paths.get("input\\demo.html");
        path.toFile().delete();
        Files.write(path, string.getBytes(), StandardOpenOption.CREATE);
    }

    public HtmlElement walk(ScssParser.RulesetContext t) {
        var thisElement = createElement(t);

        var blockTree = t.getChild(1);
        if (blockTree instanceof ScssParser.BlockContext) {
            var block = (ScssParser.BlockContext) blockTree;

//            block.children.forEach(shit -> System.out.println(shit.getClass().getCanonicalName()));

            for (var child : block.getRuleContexts(ScssParser.StatementContext.class)) {
                thisElement.addChild(walk(child.ruleset()));
            }
        }

        return thisElement;
    }

    private HtmlElement createElement(ScssParser.RulesetContext statement) {
        var element = new HtmlElement();

        var selector = statement.getChild(ScssParser.SelectorsContext.class, 0);
        if (selector != null) {
            selector.selector().stream().skip(1).forEach(idOrClass -> {
                var text = idOrClass.getText();
                var trimmed = text.substring(1);
                if (text.startsWith(".")) {
                    element.addClass(trimmed);
                } else if (text.startsWith("#")) {
                    element.setId(trimmed);
                }
            });

            element.setType(selector.selector(0).getText());
        }

        var block = statement.getChild(ScssParser.BlockContext.class, 0);
        if (block != null) {
            block.children.stream().filter(child -> child instanceof ScssParser.PropertyContext).map(ScssParser.PropertyContext.class::cast).forEach(property -> {
                var value = property.values().getText();
                var finalValue = value.substring(1, value.length() - 1);
                var identifier = property.identifier().getText();
                System.out.println("identifier = " + identifier);
                if (identifier.startsWith("html-")) {
                    element.addAttribute(identifier.substring(5), finalValue);
                } else {
                    element.addProperty(identifier, value.startsWith("'") && value.endsWith("'") ? finalValue : value);
//                    element.addProperty(identifier, value);
                }
            });
        }

        return element;
    }

}
