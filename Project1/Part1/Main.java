import java.io.IOException;

class Main {
    public static void main(String[] args) {
        try {
            System.out.println("The given string is evaluated to:" + (new Parser(System.in)).eval());
        } catch (IOException | ParseError e) {
            System.err.println(e.getMessage());
        }
    }
}

