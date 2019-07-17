package com.uddernetworks.wss;

import com.uddernetworks.wss.scss.ScssParser;
import com.uddernetworks.wss.tree.HtmlElement;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class WSS {

    private static final String INPUT = "input\\material-kit\\landing-page.scss";

    public static void main(String[] args) throws IOException {
        new WSS().main();
    }

    private String inputString;

    private void main() throws IOException {
        var preprocessed = preprocessCSS(this.inputString = Files.readString(Paths.get(INPUT)));
        System.out.println("preprocessed = \n" + preprocessed);
        var input = CharStreams.fromString(preprocessed);
        var lex = new com.uddernetworks.wss.scss.ScssLexer(input);
        // copy text out of sliding buffer and store in tokens
        lex.setTokenFactory(new CommonTokenFactory(true));
        var tokens = new UnbufferedTokenStream<CommonToken>(lex);
        var parser = new com.uddernetworks.wss.scss.ScssParser(tokens);
        parser.setBuildParseTree(true);

        var stylesheet = parser.stylesheet();

        var root = walk((ScssParser.RulesetContext) stylesheet.getChild(0).getChild(0));
        var string = root.toString();
        System.out.println("\n\n\n\n" + string);

        var path = Paths.get(INPUT.replaceAll("\\.scss$", ".html"));
        path.toFile().delete();
        Files.write(path, string.getBytes(), StandardOpenOption.CREATE);
    }

    public HtmlElement walk(ScssParser.RulesetContext t) {
        var thisElement = createElement(t);

        var blockTree = t.getChild(1);
        if (blockTree instanceof ScssParser.BlockContext) {
            var block = (ScssParser.BlockContext) blockTree;

            for (var child : block.getRuleContexts(ScssParser.StatementContext.class)) {
                thisElement.addChild(walk(child.ruleset()));
            }
        }

        return thisElement;
    }

    private HtmlElement createElement(ScssParser.RulesetContext statement) {
        var element = new HtmlElement();
        var useRaw = false;

        var selector = statement.getChild(ScssParser.SelectorsContext.class, 0);
        if (selector != null) {
            var firstSelector = selector.selector(0).getText();
            element.setType(firstSelector);

//            if (firstSelector.equals("style")) {
//                useRaw = true;
//            } else {
                selector.selector().stream().skip(1).forEach(idOrClass -> {
                    var text = idOrClass.getText();
                    var trimmed = text.substring(1);
                    if (text.startsWith(".")) {
                        element.addClass(trimmed);
                    } else if (text.startsWith("#")) {
                        element.setId(trimmed);
                    }
                });
//            }
        }

        var block = statement.getChild(ScssParser.BlockContext.class, 0);
        if (block != null) {
//            if (useRaw) {
//
//                var raw = getRawCSS(this.inputString, block.getStart().getStartIndex());
//                System.out.println("raw = \n" + raw);
//
//                element.setRaw(raw);
//                return element;
//            }

            block.children.stream().filter(child -> child instanceof ScssParser.PropertyContext).map(ScssParser.PropertyContext.class::cast).forEach(property -> {
                var value = property.values().getText();
                System.out.println("value = " + value);
                var identifier = property.identifier().getText();
                if (identifier.startsWith("html-")) {
                    element.addAttribute(identifier.substring(5), value.substring(1, value.length() - 1));
                } else {
                    element.addProperty(identifier, value.startsWith("'") && value.endsWith("'") ? value.substring(1, value.length() - 1) : value);
                }
            });
        }

        return element;
    }





    // Needed since the lexer doesn't support everything, so it goes in a `content` tag in the style thing
    private String preprocessCSS(String input) {
        for (int index = input.indexOf("style "); index >= 0; index = input.indexOf("style ", index + 1)) {
            var raw = getRawCSS(input, index);
            var style = raw.substring(5).replaceAll("\\s+", " ");
            style = style.replaceAll("(^\\s*\\{|\\}\\s*$)", "");
            System.out.println("style = \n" + style);

            var leftInput = input.substring(0, index);
            var rightInput = input.substring(index + raw.length() + 1);
            System.out.println("leftInput = \n" + leftInput);
            System.out.println("rightInput = \n" + rightInput);
            input = leftInput + "\nstyle{ content: '" + style + "'; }\n" + rightInput;
        }
        return input;
    }




    private String getRawCSS(String file, int start) {
        var depth = 0;
        var end = 0;
        var chars = file.toCharArray();
        for (int i = start; i < chars.length; i++) {
            var curr = chars[i];
            if (curr == '{') depth++;
            if (curr == '}') {
                if (--depth == 0) {
                    end = i;
                    break;
                }
            }
        }

        return file.substring(start, end + 1);
    }

}
