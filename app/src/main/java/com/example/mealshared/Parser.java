package com.example.mealshared;

public class Parser {

    String[] Tokens;

    public Parser(String context){
        this.Tokens = context.split(" ");
    }

    public String extractContent(){
        StringBuilder content= new StringBuilder();
        for(String Token:Tokens){
            if (Token.charAt(0)!='#') content.append(Token).append(" ");
        }
        return content.toString();
    }

    public String extractTag(){
        StringBuilder Tag= new StringBuilder();
        for(String Token:Tokens){
            for (int i = 0; i < Token.length(); i++) {
                if (Token.charAt(i) == '#') {
                    boolean flag = false;
                    for (int j = i+1; j < Token.length(); j++) {
                        if (Token.charAt(j) != ' ' && Token.charAt(j) != '#') {
                            flag=true;
                            Tag.append(Token.charAt(j));
                        }else break;
                    }
                    if(flag) Tag.append('/');
                }
            }
        }
        return Tag.toString();
    }
}
