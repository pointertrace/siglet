package io.github.pointertrace.siglet.api;

public interface Result {

        <T extends Signal> Result andSend(T signal, String destination);

}
