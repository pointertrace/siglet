package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siget.parser.Describable;
import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.raw.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UniqueNameValidator implements RawConfigValidator {


    @Override
    public void Validate(RawConfig config) {
        Map<String, List<BaseConfig>> namedConfigItems = new HashMap<>();

        config.getReceivers().forEach(receiverConfig -> addItem(receiverConfig, namedConfigItems));
        config.getExporters().forEach(exporterConfig -> addItem(exporterConfig, namedConfigItems));
        config.getPipelines().forEach(pipelineConfig -> addItem(pipelineConfig, namedConfigItems));
        config.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getProcessors().stream())
                .forEach(processorConfig -> addItem(processorConfig, namedConfigItems));

        checkUniqueNames(namedConfigItems);

    }

    private void addItem(BaseConfig item, Map<String, List<BaseConfig>> namedConfigItems) {
        List<BaseConfig> alreadyNamedItens = namedConfigItems.get(item.getName());
        if (alreadyNamedItens == null) {
            namedConfigItems.put(item.getName(), new ArrayList<>(List.of(item)));
        } else {
            alreadyNamedItens.add(item);
        }
    }

    private void checkUniqueNames(Map<String, List<BaseConfig>> namedConfigItems) {

        String errors = namedConfigItems.values().stream()
                .filter(namedConfigs -> namedConfigs.size() > 1)
                .map(this::errorMessage)
                .collect(Collectors.joining("\n"));

        if (!errors.isEmpty()) {
            throw new SigletError("Configuration items must have a unique name but The following items have the " +
                                  "same name:\n" + errors);
        }

    }

    private String errorMessage(List<BaseConfig> sameNameItems) {

        return sameNameItems.stream()
                .map(sameNameItem -> Describable.PREFIX.repeat(2) + getItemType(sameNameItem) + " [" +
                                     sameNameItem.getName() + "] at " + sameNameItem.getLocation())
                .collect(Collectors.joining("\n"));

    }

}
