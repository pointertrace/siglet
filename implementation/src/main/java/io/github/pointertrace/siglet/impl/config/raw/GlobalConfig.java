package io.github.pointertrace.siglet.impl.config.raw;


import io.github.pointertrace.siglet.parser.Describable;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;

public class GlobalConfig implements Located, Describable, QueueSizeConfig, ThreadPoolSizeConfig {

    private Location location;

    private Integer queueSize;

    private Location queueSizeLocation;

    private Integer threadPoolSize;

    private Location threadPoolSizeLocation;

    private Integer spanObjectPoolSize;

    private Location spanObjectPoolSizeLocation;

    private Integer metricObjectPoolSize;

    private Location metricObjectPoolSizeLocation;

    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(Integer treadPoolSize) {
        this.threadPoolSize = treadPoolSize;
    }

    public Integer getSpanObjectPoolSize() {
        return spanObjectPoolSize;
    }

    public void setSpanObjectPoolSize(Integer spanObjectPoolSize) {
        this.spanObjectPoolSize = spanObjectPoolSize;
    }

    public Integer getMetricObjectPoolSize() {
        return metricObjectPoolSize;
    }

    public void setMetricObjectPoolSize(Integer metricObjectPoolSize) {
        this.metricObjectPoolSize = metricObjectPoolSize;
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(Describable.prefix(level));
        sb.append(getLocation().describe());
        sb.append("  GlobalConfig");

        if (queueSize != null) {
            sb.append("\n");
            sb.append(Describable.prefix(level + 1));
            sb.append(queueSizeLocation.describe());
            sb.append("  queue-size: ");
            sb.append(queueSize);
        }


        if (threadPoolSize != null) {
            sb.append("\n");
            sb.append(Describable.prefix(level + 1));
            sb.append(threadPoolSizeLocation.describe());
            sb.append("  thread-pool-size: ");
            sb.append(threadPoolSize);
        }

        if (spanObjectPoolSize != null) {
            sb.append("\n");
            sb.append(Describable.prefix(level + 1));
            sb.append(spanObjectPoolSizeLocation.describe());
            sb.append("  span-object-pool-size: ");
            sb.append(spanObjectPoolSize);
        }

        if (metricObjectPoolSize != null) {
        sb.append("\n");
        sb.append(Describable.prefix(level + 1));
        sb.append(metricObjectPoolSizeLocation.describe());
        sb.append("  metric-object-pool-size: ");
        sb.append(metricObjectPoolSize);
        }

        return sb.toString();

    }

    public Location getQueueSizeLocation() {
        return queueSizeLocation;
    }

    public void setQueueSizeLocation(Location queueSizeLocation) {
        this.queueSizeLocation = queueSizeLocation;
    }

    public Location getThreadPoolSizeLocation() {
        return threadPoolSizeLocation;
    }

    public void setThreadPoolSizeLocation(Location threadPoolSizeLocation) {
        this.threadPoolSizeLocation = threadPoolSizeLocation;
    }

    public Location getSpanObjectPoolSizeLocation() {
        return spanObjectPoolSizeLocation;
    }

    public void setSpanObjectPoolSizeLocation(Location spanObjectPoolSizeLocation) {
        this.spanObjectPoolSizeLocation = spanObjectPoolSizeLocation;
    }

    public Location getMetricObjectPoolSizeLocation() {
        return metricObjectPoolSizeLocation;
    }

    public void setMetricObjectPoolSizeLocation(Location metricObjectPoolSizeLocation) {
        this.metricObjectPoolSizeLocation = metricObjectPoolSizeLocation;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

}
