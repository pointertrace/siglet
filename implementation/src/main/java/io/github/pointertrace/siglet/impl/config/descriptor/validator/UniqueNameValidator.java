package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.BaseDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UniqueNameValidator implements YamlDescriptorValidator {

    @Override
    public void validate(YamlDescriptor yamlDescriptor) {

        Map<String, List<BaseDescriptor>> namedConfigItems = Stream.of(

                        // receivers
                        yamlDescriptor.getReceivers().stream()
                                .map(BaseDescriptor.class::cast)
                                .collect(Collectors.groupingBy(r -> r.getName().getValue())),

                        // pipelines
                        yamlDescriptor.getPipelines().stream()
                                .map(BaseDescriptor.class::cast)
                                .collect(Collectors.groupingBy(p -> p.getName().getValue())),

                        // processors
                        yamlDescriptor.getPipelines().stream().flatMap(p -> p.getProcessors().stream())
                                .map(BaseDescriptor.class::cast)
                                .collect(Collectors.groupingBy(p -> p.getName().getValue())),

                        // exporters
                        yamlDescriptor.getExporters().stream()
                                .map(BaseDescriptor.class::cast)
                                .collect(Collectors.groupingBy(e -> e.getName().getValue()))

                ).flatMap(grouping -> grouping.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                }));

        checkUniqueNames(namedConfigItems);
    }


    private void checkUniqueNames(Map<String, List<BaseDescriptor>> namedConfigItems) {

        String errors = namedConfigItems.values().stream()
                .filter(namedConfigs -> namedConfigs.size() > 1)
                .map(this::errorMessage)
                .collect(Collectors.joining("\n"));

        if (!errors.isEmpty()) {
            throw new SigletError("Configuration items must have a unique name but The following items have the " +
                    "same name:\n" + errors);
        }

    }

    private String errorMessage(List<BaseDescriptor> sameNameItems) {

        Map<String, BaseDescriptor> firstItemByType = new HashMap<>();
        sameNameItems.forEach(item -> firstItemByType.putIfAbsent(getItemType(item), item));

        return firstItemByType.values().stream()
                .map(sameNameItem -> "    " + getItemType(sameNameItem) + " [" +
                        sameNameItem.getName().getValue() + "] at " +
                        "(" + sameNameItem.getLocation().getLine() + "," +
                        sameNameItem.getLocation().getColumn() + ")")
                .collect(Collectors.joining("\n"));

    }

}
