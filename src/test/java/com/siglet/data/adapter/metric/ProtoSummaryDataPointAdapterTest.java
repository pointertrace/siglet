package com.siglet.data.adapter.metric;

import com.siglet.data.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProtoSummaryDataPointAdapterTest {

    private SummaryDataPoint summaryDataPoint;

    private List<KeyValue> attributes;

    private List<SummaryDataPoint.ValueAtQuantile> valueAtQuantiles;

    private ProtoSummaryDataPointAdapter protoSummaryDataPointAdapter;

    @BeforeEach
    void setUp() {

        attributes = List.of(
                KeyValue.newBuilder()
                        .setKey("first-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("first-attribute-value").build())
                        .build(),
                KeyValue.newBuilder()
                        .setKey("second-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("second-attribute-value").build())
                        .build());

        valueAtQuantiles = List.of(
                SummaryDataPoint.ValueAtQuantile.newBuilder()
                        .setValue(1.2)
                        .setQuantile(3.4)
                        .build(),
                SummaryDataPoint.ValueAtQuantile.newBuilder()
                        .setValue(5.6)
                        .setQuantile(7.8)
                        .build());

        summaryDataPoint = SummaryDataPoint.newBuilder()
                .setFlags(1)
                .setTimeUnixNano(2)
                .setStartTimeUnixNano(3)
                .setCount(4)
                .setSum(5.5)
                .addAllQuantileValues(valueAtQuantiles)
                .addAllAttributes(attributes)
                .build();

        protoSummaryDataPointAdapter = new ProtoSummaryDataPointAdapter(summaryDataPoint, true);
    }

    @Test
    public void get() {
        assertEquals(1, protoSummaryDataPointAdapter.getFlags());
        assertEquals(2, protoSummaryDataPointAdapter.getTimeUnixNano());
        assertEquals(3, protoSummaryDataPointAdapter.getStartTimeUnixNano());
        assertEquals(4, protoSummaryDataPointAdapter.getCount());
        assertEquals(5.5, protoSummaryDataPointAdapter.getSum());

        ProtoAttributesAdapter attributes = protoSummaryDataPointAdapter.getAttributes();
        assertEquals("first-attribute-value", attributes.getAsString("first-attribute"));
        assertEquals("second-attribute-value", attributes.getAsString("second-attribute"));

        ProtoValueAtQuantilesAdapter valueAtQuantilesAdapter = protoSummaryDataPointAdapter.getQuantileValues();

        assertEquals(2, valueAtQuantilesAdapter.getSize());
        assertEquals(1.2, valueAtQuantilesAdapter.getAt(0).getValue());
        assertEquals(3.4, valueAtQuantilesAdapter.getAt(0).getQuantile());
        assertEquals(5.6, valueAtQuantilesAdapter.getAt(1).getValue());
        assertEquals(7.8, valueAtQuantilesAdapter.getAt(1).getQuantile());

        assertFalse(protoSummaryDataPointAdapter.isUpdated());

    }

    @Test
    public void setAndGet() {

        protoSummaryDataPointAdapter
                .setFlags(10)
                .setTimeUnixNano(20)
                .setStartTimeUnixNano(30)
                .setCount(40)
                .setSum(50.5);


        assertEquals(10, protoSummaryDataPointAdapter.getFlags());
        assertEquals(20, protoSummaryDataPointAdapter.getTimeUnixNano());
        assertEquals(30, protoSummaryDataPointAdapter.getStartTimeUnixNano());
        assertEquals(40, protoSummaryDataPointAdapter.getCount());
        assertEquals(50.5, protoSummaryDataPointAdapter.getSum());
    }

    @Test
    public void attributesChangeAndGet() {

        protoSummaryDataPointAdapter.getAttributes().set("first-attribute", "new-value");
        protoSummaryDataPointAdapter.getAttributes().set("extra-attribute", "extra-attribute-value");

        ProtoAttributesAdapter attributes = protoSummaryDataPointAdapter.getAttributes();

        assertEquals(3, attributes.getSize());
        assertEquals("new-value", attributes.getAsString("first-attribute"));
        assertEquals("extra-attribute-value", attributes.getAsString("extra-attribute"));

        assertTrue(protoSummaryDataPointAdapter.isUpdated());
    }

    @Test
    public void exemplarsChangeAndGet() {

        protoSummaryDataPointAdapter.getQuantileValues().add()
                .setValue(9.10)
                .setQuantile(11.12);

        assertEquals(3, protoSummaryDataPointAdapter.getQuantileValues().getSize());

        ProtoValueAtQuantilesAdapter valueAtQuantilesAdapter = protoSummaryDataPointAdapter.getQuantileValues();

        ProtoValueAtQuantileAdapter valueAtQuantileAdapter = valueAtQuantilesAdapter.getAt(0);

        assertTrue(protoSummaryDataPointAdapter.isUpdated());

    }

    @Test
    public void getUpdated_notUpdatable() {

        protoSummaryDataPointAdapter = new ProtoSummaryDataPointAdapter(summaryDataPoint, false);

        assertSame(summaryDataPoint, protoSummaryDataPointAdapter.getUpdated());

    }

    @Test
    public void getUpdated_nothingUpdated() {

        assertSame(summaryDataPoint, protoSummaryDataPointAdapter.getUpdated());
    }

    @Test
    public void getUpdated_onlyAttributesUpdated() {

        ProtoAttributesAdapter attributes = protoSummaryDataPointAdapter.getAttributes();

        attributes.set("first-attribute", "new-first-key-value");
        attributes.set("new-key", "new-key-value");


        SummaryDataPoint actual = protoSummaryDataPointAdapter.getUpdated();

        assertEquals(1, actual.getFlags());
        assertEquals(2, actual.getTimeUnixNano());
        assertEquals(3, actual.getStartTimeUnixNano());
        assertEquals(4, actual.getCount());
        assertEquals(5.5, actual.getSum());

        List<KeyValue> actualAttributes = actual.getAttributesList();

        assertEquals(3, actualAttributes.size());

        assertEquals("first-attribute", actualAttributes.get(0).getKey());
        assertEquals("new-first-key-value", actualAttributes.get(0).getValue().getStringValue());

        assertEquals("second-attribute", actualAttributes.get(1).getKey());
        assertEquals("second-attribute-value", actualAttributes.get(1).getValue().getStringValue());

        assertEquals("new-key", actualAttributes.get(2).getKey());
        assertEquals("new-key-value", actualAttributes.get(2).getValue().getStringValue());

        List<SummaryDataPoint.ValueAtQuantile> values = actual.getQuantileValuesList();

        assertEquals(2, values.size());
        assertEquals(1.2, values.get(0).getValue());
        assertEquals(3.4, values.get(0).getQuantile());
        assertEquals(5.6, values.get(1).getValue());
        assertEquals(7.8, values.get(1).getQuantile());


    }

    @Test
    public void getUpdated_onlyQuantileValuesUpdated() {

        ProtoValueAtQuantilesAdapter quantileValues = protoSummaryDataPointAdapter.getQuantileValues();

        quantileValues.remove(0);
        quantileValues.getAt(0).setValue(9.10).setQuantile(11.12);
        quantileValues.add().setValue(13.14).setQuantile(15.16);

        SummaryDataPoint actual = protoSummaryDataPointAdapter.getUpdated();

        assertEquals(1, actual.getFlags());
        assertEquals(2, actual.getTimeUnixNano());
        assertEquals(3, actual.getStartTimeUnixNano());
        assertEquals(4, actual.getCount());
        assertEquals(5.5, actual.getSum());

        List<KeyValue> actualAttributes = actual.getAttributesList();

        assertEquals(2, actualAttributes.size());

        assertEquals("first-attribute", actualAttributes.get(0).getKey());
        assertEquals("first-attribute-value", actualAttributes.get(0).getValue().getStringValue());

        assertEquals("second-attribute", actualAttributes.get(1).getKey());
        assertEquals("second-attribute-value", actualAttributes.get(1).getValue().getStringValue());

        List<SummaryDataPoint.ValueAtQuantile> values = actual.getQuantileValuesList();

        assertEquals(2, values.size());
        assertEquals(9.10, values.get(0).getValue());
        assertEquals(11.12, values.get(0).getQuantile());
        assertEquals(13.14, values.get(1).getValue());
        assertEquals(15.16, values.get(1).getQuantile());
    }
}