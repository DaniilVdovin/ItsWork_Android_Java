package com.daniilvdovin.iswork;

import android.text.InputFilter;

public class Filters {
    public static InputFilter main_filter = (source, start, end, dest, dstart, dend) -> {
        if(source.equals("")){ // for backspace
            return source;
        }
        if(source.toString().matches("[a-zA-Zа-яА-Я0-9\\n\\t/\\w/U ,+=%$#^*&~`;:±<>()\\\"\\'\\{\\}\\[\\].\\\\!?@_-]+")){
            return source;
        }
        return "";
    };
    public static InputFilter price_filter = (source, start, end, dest, dstart, dend) -> {
        if(source.equals("")){ // for backspace
            return source;
        }
        if(source.toString().matches("[0-9]+")){
            return source;
        }
        return "";
    };
}
