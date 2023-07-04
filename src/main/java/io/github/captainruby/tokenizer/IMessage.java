package io.github.captainruby.tokenizer;


public interface IMessage {

    String getRole();

    String getContent();

    String getName();

    IFunctionCall getFunction_call();
}
