package com.siglet.config.builder;

import com.siglet.SigletError;
import com.siglet.utils.Joining;
import com.siglet.utils.StringUtils;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UniqueNameValidator implements Validator {

    private static String apply(String s) {
        return s;
    }

    @Override
    public void validate(GlobalConfigBuilder globalConfigBuilder) throws SigletError {
        String notUniqueNames = Stream.of(
                        globalConfigBuilder.getReceivers().stream()
                                .map(ReceiverBuilder::getName),
                        globalConfigBuilder.getExporters().stream()
                                .map(ExporterBuilder::getName),
                        globalConfigBuilder.getPipelines().stream()
                                .map(TracePipelineBuilder::getName),
                        globalConfigBuilder.getPipelines().stream()
                                .flatMap(t -> t.getSpanletBuilders().stream())
                                .map(SpanletBuilder::getName)
                )
                .flatMap(s -> s)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(e -> "'" + e.getKey() + "' appears " + StringUtils.frequency(e.getValue()))
                .collect(Joining.twoDelimiters(", ", " and ",
                        "Names must be unique within the configuration file but: ", "!"));

        if (notUniqueNames != null) {
            throw new SigletError(notUniqueNames);
        }
    }


}