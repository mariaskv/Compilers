import java.io.InputStream;
import java.io.IOException;

class Parser {
    private InputStream in;

    private int lookahead;

    public Parser(InputStream in) throws IOException {
        this.in = in;
        lookahead = in.read();
    }

    // function to consume next input
    private void consume(int symbol) throws IOException, ParseError{
        if(lookahead != symbol){
            throw new ParseError();
        }
        lookahead = in.read();
    }

    // function to evaluate if a var is digit 0-9 or not
    private boolean isDigit(int c){
        return '0' <= c && c <= '9';
    }

    private int evalDigit(int c){
        return c - '0'; 
    }

    public int eval() throws IOException, ParseError {
        int value = Tern();

        if (lookahead != -1 && lookahead != '\n')
            throw new ParseError();

        return value;
    }

    private int Tern() throws IOException, ParseError{
        int value = Term();
        value = TernTail(value);
        return value;
    }

    private int TernTail(int value) throws IOException, ParseError{
        
        switch(lookahead){
            case '^':
                consume(lookahead); //if we have XOR in input evaluate and continue
                int value2 = Term();
                value2 = value ^ value2;
                int value3 = TernTail(value2);
                return value3;  
            case -1: 
                return value;
            case '\n':
                return value;
            case ')':
                return value;         
        }

        throw new ParseError();
    }


    private int Term() throws IOException, ParseError{

        int value = Factor();
        return Term2(value);
    }

    private int Term2(int value) throws IOException, ParseError{
        switch(lookahead){
            case '&':
                consume(lookahead); //evaluate logical AND
                int value2 = Factor();
                value2 = value & value2;
                int value3 = Term2(value2);
                return value3;  
            case -1: 
                return value;
            case '\n':
                return value;
            case ')':
                return value; 
            case '^':
                return value;        
        }

        throw new ParseError();
    }

    private int Factor() throws IOException, ParseError{
        int value;
        if(isDigit(lookahead)){ //if is digit consume it and continue
            value = evalDigit(lookahead);
            consume(lookahead);
            return value;
        }
        else if(lookahead == '('){ //if we have an open parenthesis consume it and wait for a )
            consume(lookahead);
            value = Tern();
            if(lookahead != ')')
                throw new ParseError();
            consume(')');
            return value;
        }
        else{
            throw new ParseError();
        }
    }
}

