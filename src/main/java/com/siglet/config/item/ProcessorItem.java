package com.siglet.config.item;

import java.util.ArrayList;
import java.util.List;

public class ProcessorItem extends Item {

   private String pipeline;

   private List<String> to = new ArrayList<>();


    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public void setToSingleValue(String to)  {
        this.to = List.of(to);
    }
}
