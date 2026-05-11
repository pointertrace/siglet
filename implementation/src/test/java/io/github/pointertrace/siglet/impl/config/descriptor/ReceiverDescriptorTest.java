package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.grpc.OtelGrpcReceiverConfig;
import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReceiverDescriptorTest {

	private ReceiverTypeRegistry receiverTypeRegistry;

	@BeforeEach
	void setUp() {
		receiverTypeRegistry = new ReceiverTypeRegistry();
	}

	@Test
	void parseGrpcReceiver() {
		var config = """
				grpc: receiver-name
				config:
				  address: 0.0.0.0:8081
				""";

		Schema schema = ReceiverDescriptor.descriptorSchemaBuilder(receiverTypeRegistry).build();

		Node node = Parser.DEFAULT.parse(config);

		Factory factory = schema.validate(node);

		ReceiverDescriptor receiverDescriptor = factory.create(ReceiverDescriptor.class);

		assertEquals("grpc", receiverDescriptor.getType().getValue());
		assertEquals(Location.of(1,1), receiverDescriptor.getType().getLocation());
		assertEquals("receiver-name", receiverDescriptor.getName().getValue());
		assertEquals(Location.of(1, 7), receiverDescriptor.getName().getLocation());

		OtelGrpcReceiverConfig grpcConfig = assertInstanceOf(OtelGrpcReceiverConfig.class, receiverDescriptor.getConfig());
		assertEquals("0.0.0.0", grpcConfig.getAddress().getInetSocketAddress().getHostString());
		assertEquals(8081, grpcConfig.getAddress().getInetSocketAddress().getPort());
	}

	@Test
	void parseDebugReceiver() {
		var config = """
				debug: receiver-name
				""";

		Schema schema = ReceiverDescriptor.descriptorSchemaBuilder(receiverTypeRegistry).build();

		Node node = Parser.DEFAULT.parse(config);

		Factory factory = schema.validate(node);

		ReceiverDescriptor receiverDescriptor = factory.create(ReceiverDescriptor.class);

		assertEquals("debug", receiverDescriptor.getType().getValue());
		assertEquals(Location.of(1, 1), receiverDescriptor.getType().getLocation());
		assertEquals("receiver-name", receiverDescriptor.getName().getValue());
		assertEquals(Location.of(1, 8), receiverDescriptor.getName().getLocation());
		assertNull(receiverDescriptor.getConfig());
	}

}