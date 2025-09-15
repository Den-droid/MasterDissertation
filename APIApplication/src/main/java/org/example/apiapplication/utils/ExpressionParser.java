package org.example.apiapplication.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ExpressionParser.java
 * <p>
 * Single-file recursive-descent parser + AST evaluator for mathematical expressions.
 * Supports parentheses, + - * / ^, unary +/- , functions, variables, and decimals.
 * <p>
 * Example:
 * ExpressionParser.Parser p = new ExpressionParser.Parser("3 + 4*2/(1-5)^2^3");
 * Node ast = p.parse();
 * double value = ast.evaluate(Collections.emptyMap());
 */
public class ExpressionParser {

    // --------------------
    // Lexer / Tokenizer
    // --------------------
    enum TokenType {NUMBER, IDENT, PLUS, MINUS, MUL, DIV, POW, LPAREN, RPAREN, COMMA, EOF}

    static class Token {
        final TokenType type;
        final String text;

        Token(TokenType type, String text) {
            this.type = type;
            this.text = text;
        }

        public String toString() {
            return type + (text == null ? "" : "(" + text + ")");
        }
    }

    static class Lexer {
        private final String s;
        private int pos = 0;

        Lexer(String s) {
            this.s = s;
        }

        Token next() {
            skipWhitespace();
            if (pos >= s.length()) return new Token(TokenType.EOF, null);
            char c = s.charAt(pos);

            // operators and punctuation
            switch (c) {
                case '+':
                    pos++;
                    return new Token(TokenType.PLUS, "+");
                case '-':
                    pos++;
                    return new Token(TokenType.MINUS, "-");
                case '*':
                    pos++;
                    return new Token(TokenType.MUL, "*");
                case '/':
                    pos++;
                    return new Token(TokenType.DIV, "/");
                case '^':
                    pos++;
                    return new Token(TokenType.POW, "^");
                case '(':
                    pos++;
                    return new Token(TokenType.LPAREN, "(");
                case ')':
                    pos++;
                    return new Token(TokenType.RPAREN, ")");
                case ',':
                    pos++;
                    return new Token(TokenType.COMMA, ",");
            }

            // number (integer or decimal)
            if (Character.isDigit(c) || c == '.') {
                int start = pos;
                boolean hasDot = false;
                if (c == '.') {
                    hasDot = true;
                    pos++;
                }
                while (pos < s.length()) {
                    char cc = s.charAt(pos);
                    if (Character.isDigit(cc)) pos++;
                    else if (cc == '.' && !hasDot) {
                        hasDot = true;
                        pos++;
                    } else break;
                }
                // optional exponent part
                if (pos < s.length() && (s.charAt(pos) == 'e' || s.charAt(pos) == 'E')) {
                    pos++;
                    if (pos < s.length() && (s.charAt(pos) == '+' || s.charAt(pos) == '-')) pos++;
                    while (pos < s.length() && Character.isDigit(s.charAt(pos))) pos++;
                }
                return new Token(TokenType.NUMBER, s.substring(start, pos));
            }

            // identifier (function names, variable names)
            if (Character.isLetter(c) || c == '_') {
                int start = pos;

                do pos++;
                while (pos < s.length() && (Character.isLetterOrDigit(s.charAt(pos)) || s.charAt(pos) == '_'));

                return new Token(TokenType.IDENT, s.substring(start, pos));
            }

            throw new RuntimeException("Unexpected character at pos " + pos + ": '" + c + "'");
        }

        private void skipWhitespace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) pos++;
        }
    }

    // --------------------
    // AST Nodes
    // --------------------
    interface Node {
        double evaluate(Map<String, Double> variables);
    }

    static class NumberNode implements Node {
        final double value;

        NumberNode(double value) {
            this.value = value;
        }

        public double evaluate(Map<String, Double> variables) {
            return value;
        }

        public String toString() {
            return Double.toString(value);
        }
    }

    static class VariableNode implements Node {
        final String name;

        VariableNode(String name) {
            this.name = name;
        }

        public double evaluate(Map<String, Double> variables) {
            if (variables != null && variables.containsKey(name)) return variables.get(name);
            throw new RuntimeException("Unknown variable: " + name);
        }

        public String toString() {
            return name;
        }
    }

    static class UnaryNode implements Node {
        final TokenType op; // PLUS or MINUS
        final Node expr;

        UnaryNode(TokenType op, Node expr) {
            this.op = op;
            this.expr = expr;
        }

        public double evaluate(Map<String, Double> variables) {
            double v = expr.evaluate(variables);
            return (op == TokenType.MINUS) ? -v : v;
        }

        public String toString() {
            return "(" + op + " " + expr + ")";
        }
    }

    static class BinaryNode implements Node {
        final TokenType op;
        final Node left, right;

        BinaryNode(TokenType op, Node left, Node right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }

        public double evaluate(Map<String, Double> variables) {
            double a = left.evaluate(variables);
            double b = right.evaluate(variables);
            switch (op) {
                case PLUS:
                    return a + b;
                case MINUS:
                    return a - b;
                case MUL:
                    return a * b;
                case DIV:
                    return a / b;
                case POW:
                    return Math.pow(a, b);
                default:
                    throw new RuntimeException("Bad binary op: " + op);
            }
        }

        public String toString() {
            return "(" + left + " " + op + " " + right + ")";
        }
    }

    static class FunctionNode implements Node {
        final String name;
        final List<Node> args;

        FunctionNode(String name, List<Node> args) {
            this.name = name;
            this.args = args;
        }

        public double evaluate(Map<String, Double> variables) {
            // common math functions - single arg
            if (args.size() == 1) {
                double x = args.get(0).evaluate(variables);
                return switch (name.toLowerCase()) {
                    case "sin" -> Math.sin(x);
                    case "cos" -> Math.cos(x);
                    case "tan" -> Math.tan(x);
                    case "asin" -> Math.asin(x);
                    case "acos" -> Math.acos(x);
                    case "atan" -> Math.atan(x);
                    case "sqrt" -> Math.sqrt(x);
                    case "abs" -> Math.abs(x);
                    case "exp" -> Math.exp(x);
                    case "ln", "log" -> Math.log(x);
                    case "log10" -> Math.log10(x);
                    case "floor" -> Math.floor(x);
                    case "ceil" -> Math.ceil(x);
                    default -> throw new RuntimeException("Unknown function: " + name);
                };
            } else if (args.size() == 2) {
                double a = args.get(0).evaluate(variables);
                double b = args.get(1).evaluate(variables);
                if (name.equalsIgnoreCase("pow")) return Math.pow(a, b);
                if (name.equalsIgnoreCase("max")) return Math.max(a, b);
                if (name.equalsIgnoreCase("min")) return Math.min(a, b);
                throw new RuntimeException("Unknown 2-arg function: " + name);
            } else {
                throw new RuntimeException("Unsupported function '" + name + "' with " + args.size() + " arguments.");
            }
        }

        public String toString() {
            return name + "(" + args + ")";
        }
    }

    // --------------------
    // Parser (recursive descent)
    // Grammar (informal):
    // expression := term (('+'|'-') term)*
    // term       := power (('*'|'/') power)*
    // power      := unary ('^' power)?      // right-associative
    // unary      := ('+'|'-') unary | primary
    // primary    := NUMBER | IDENT | IDENT '(' arglist ')' | '(' expression ')'
    // arglist    := expression (',' expression)*
    // --------------------
    static class Parser {
        private final Lexer lexer;
        private Token cur;

        Parser(String input) {
            lexer = new Lexer(input);
            cur = lexer.next();
        }

        private void eat(TokenType t) {
            if (cur.type == t) cur = lexer.next();
            else throw new RuntimeException("Expected " + t + " but found " + cur.type);
        }

        Node parse() {
            Node node = parseExpression();
            if (cur.type != TokenType.EOF) throw new RuntimeException("Unexpected token: " + cur);
            return node;
        }

        // expression := term (('+'|'-') term)*
        private Node parseExpression() {
            Node node = parseTerm();
            while (cur.type == TokenType.PLUS || cur.type == TokenType.MINUS) {
                TokenType op = cur.type;
                eat(op);
                Node right = parseTerm();
                node = new BinaryNode(op, node, right);
            }
            return node;
        }

        // term := power (('*'|'/') power)*
        private Node parseTerm() {
            Node node = parsePower();
            while (cur.type == TokenType.MUL || cur.type == TokenType.DIV) {
                TokenType op = cur.type;
                eat(op);
                Node right = parsePower();
                node = new BinaryNode(op, node, right);
            }
            return node;
        }

        // power := unary ('^' power)?   // right-associative
        private Node parsePower() {
            Node left = parseUnary();
            if (cur.type == TokenType.POW) {
                eat(TokenType.POW);
                Node right = parsePower(); // recurse -> right-associative
                return new BinaryNode(TokenType.POW, left, right);
            }
            return left;
        }

        // unary := ('+'|'-') unary | primary
        private Node parseUnary() {
            if (cur.type == TokenType.PLUS) {
                eat(TokenType.PLUS);
                return new UnaryNode(TokenType.PLUS, parseUnary());
            }
            if (cur.type == TokenType.MINUS) {
                eat(TokenType.MINUS);
                return new UnaryNode(TokenType.MINUS, parseUnary());
            }
            return parsePrimary();
        }

        // primary := NUMBER | IDENT | IDENT '(' arglist ')' | '(' expression ')'
        private Node parsePrimary() {
            if (cur.type == TokenType.NUMBER) {
                double val = Double.parseDouble(cur.text);
                eat(TokenType.NUMBER);
                return new NumberNode(val);
            }
            if (cur.type == TokenType.IDENT) {
                String name = cur.text;
                eat(TokenType.IDENT);
                if (cur.type == TokenType.LPAREN) {
                    // function call
                    eat(TokenType.LPAREN);
                    List<Node> args = new ArrayList<>();
                    if (cur.type != TokenType.RPAREN) {
                        args.add(parseExpression());
                        while (cur.type == TokenType.COMMA) {
                            eat(TokenType.COMMA);
                            args.add(parseExpression());
                        }
                    }
                    eat(TokenType.RPAREN);
                    return new FunctionNode(name, args);
                } else {
                    // variable
                    return new VariableNode(name);
                }
            }
            if (cur.type == TokenType.LPAREN) {
                eat(TokenType.LPAREN);
                Node inside = parseExpression();
                eat(TokenType.RPAREN);
                return inside;
            }
            throw new RuntimeException("Unexpected token in primary: " + cur);
        }
    }

    public static double parse(String input, Map<String, Double> variables) {
        Parser p = new Parser(input);
        Node ast = p.parse();
        return ast.evaluate(variables == null ? Collections.emptyMap() : variables);
    }
}

