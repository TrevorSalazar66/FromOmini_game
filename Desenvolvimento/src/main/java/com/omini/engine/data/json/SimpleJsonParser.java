package com.omini.engine.data.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser de JSON recursivo e desacoplado, sem dependências externas, projetado para
 * processar com segurança arquivos de configuração, materiais e entidades do motor.
 */
public final class SimpleJsonParser {

    private final String source;
    private int cursor = 0;

    public SimpleJsonParser(String json) {
        if (json == null) {
            throw new IllegalArgumentException("O conteúdo JSON não pode ser nulo.");
        }
        this.source = json.trim();
    }

    public static Object parse(String json) {
        return new SimpleJsonParser(json).parseValor();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObjeto(String json) {
        Object resultado = parse(json);
        if (!(resultado instanceof Map)) {
            throw new IllegalArgumentException("O JSON fornecido não é um objeto JSON válido.");
        }
        return (Map<String, Object>) resultado;
    }

    private Object parseValor() {
        pularEspacos();
        if (cursor >= source.length()) {
            return null;
        }

        char c = source.charAt(cursor);
        return switch (c) {
            case '{' -> parseMapa();
            case '[' -> parseLista();
            case '"' -> parseString();
            case 't', 'f' -> parseBooleano();
            case 'n' -> parseNulo();
            default -> {
                if (c == '-' || Character.isDigit(c)) {
                    yield parseNumero();
                }
                throw new IllegalStateException("Caractere inesperado no JSON: '" + c + "' na posição " + cursor);
            }
        };
    }

    private Map<String, Object> parseMapa() {
        consumir('{');
        Map<String, Object> mapa = new LinkedHashMap<>();
        pularEspacos();

        if (olharProximo() == '}') {
            consumir('}');
            return mapa;
        }

        while (cursor < source.length()) {
            pularEspacos();
            String chave = parseString();
            pularEspacos();
            consumir(':');
            Object valor = parseValor();
            mapa.put(chave, valor);
            pularEspacos();

            char proximo = olharProximo();
            if (proximo == ',') {
                consumir(',');
            } else if (proximo == '}') {
                consumir('}');
                break;
            } else {
                throw new IllegalStateException("Esperado ',' ou '}', mas encontrado: '" + proximo + "' na posição " + cursor);
            }
        }
        return mapa;
    }

    private List<Object> parseLista() {
        consumir('[');
        List<Object> lista = new ArrayList<>();
        pularEspacos();

        if (olharProximo() == ']') {
            consumir(']');
            return lista;
        }

        while (cursor < source.length()) {
            pularEspacos();
            lista.add(parseValor());
            pularEspacos();

            char proximo = olharProximo();
            if (proximo == ',') {
                consumir(',');
            } else if (proximo == ']') {
                consumir(']');
                break;
            } else {
                throw new IllegalStateException("Esperado ',' ou ']', mas encontrado: '" + proximo + "' na posição " + cursor);
            }
        }
        return lista;
    }

    private String parseString() {
        consumir('"');
        StringBuilder sb = new StringBuilder();
        while (cursor < source.length()) {
            char c = source.charAt(cursor++);
            if (c == '"') {
                return sb.toString();
            }
            if (c == '\\') {
                if (cursor >= source.length()) {
                    break;
                }
                char escape = source.charAt(cursor++);
                switch (escape) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    default -> sb.append(escape);
                }
            } else {
                sb.append(c);
            }
        }
        throw new IllegalStateException("String JSON não terminada antes do final do arquivo.");
    }

    private Number parseNumero() {
        int inicio = cursor;
        if (source.charAt(cursor) == '-') {
            cursor++;
        }
        boolean ehPontoFlutuante = false;
        while (cursor < source.length()) {
            char c = source.charAt(cursor);
            if (Character.isDigit(c)) {
                cursor++;
            } else if (c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') {
                ehPontoFlutuante = true;
                cursor++;
            } else {
                break;
            }
        }
        String numStr = source.substring(inicio, cursor);
        if (ehPontoFlutuante) {
            return Double.parseDouble(numStr);
        }
        long valLong = Long.parseLong(numStr);
        if (valLong >= Integer.MIN_VALUE && valLong <= Integer.MAX_VALUE) {
            return (int) valLong;
        }
        return valLong;
    }

    private Boolean parseBooleano() {
        if (source.startsWith("true", cursor)) {
            cursor += 4;
            return true;
        }
        if (source.startsWith("false", cursor)) {
            cursor += 5;
            return false;
        }
        throw new IllegalStateException("Literal booleano inválido na posição " + cursor);
    }

    private Object parseNulo() {
        if (source.startsWith("null", cursor)) {
            cursor += 4;
            return null;
        }
        throw new IllegalStateException("Literal nulo inválido na posição " + cursor);
    }

    private void pularEspacos() {
        while (cursor < source.length()) {
            char c = source.charAt(cursor);
            if (Character.isWhitespace(c)) {
                cursor++;
            } else if (c == '/' && cursor + 1 < source.length() && source.charAt(cursor + 1) == '/') {
                // Suporte a comentários de linha única (JSON5 / CDDA)
                cursor += 2;
                while (cursor < source.length() && source.charAt(cursor) != '\n') {
                    cursor++;
                }
            } else {
                break;
            }
        }
    }

    private char olharProximo() {
        pularEspacos();
        if (cursor >= source.length()) {
            return '\0';
        }
        return source.charAt(cursor);
    }

    private void consumir(char esperado) {
        pularEspacos();
        if (cursor >= source.length() || source.charAt(cursor) != esperado) {
            char atual = cursor < source.length() ? source.charAt(cursor) : '\0';
            throw new IllegalStateException("Esperado caractere '" + esperado + "', mas encontrado '" + atual + "' na posição " + cursor);
        }
        cursor++;
    }
}
