package component.kafka;

import java.util.Dictionary;
import java.util.Properties;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class ConfigMapper {
    private Properties config;
    private Dictionary<String, Object> confSource;
    
    public static Properties mapConsumer(Dictionary<String, Object> conf) {
        ConfigMapper mapper = new ConfigMapper(conf);
        mapper.mapCommon();
        mapper.mapConsumer();
        return mapper.config;
    }
    
    public static Properties mapProducer(Dictionary<String, Object> conf) {
        ConfigMapper mapper = new ConfigMapper(conf);
        mapper.mapCommon();
        mapper.mapProducer();
        return mapper.config;
    }

    private ConfigMapper(Dictionary<String, Object> conf) {
        this.confSource = conf;
        config = new Properties();
    }
    
    private void mapCommon() {
        process("bootstrap.servers", "localhost:9092");
        process("client.id", "");
        process("compression.type", "none");
        process("acks", "all");
        process("retries", "0");
        process("batch.size", "16384");
        process("buffer.memory", "33554432");
        process("max.request.size", "2097152");
        process("security.protocol");
        process("ssl.truststore.location");
        process("ssl.truststore.password");
        process("ssl.keystore.location");
        process("ssl.keystore.password");
        process("ssl.key.password");
        process("ssl.provider");
        process("ssl.cipher.suites");
        process("ssl.enabled.protocols");
        process("ssl.truststore.type");
        process("ssl.keystore.type");
    }
    
    private void mapProducer() {
        config.put("key.serializer", StringSerializer.class);
        config.put("value.serializer", StringSerializer.class);
        process("request.timeout.ms", "15000");
    }
    
    private void mapConsumer() {
        config.put("key.deserializer", StringDeserializer.class);
        config.put("value.deserializer", StringDeserializer.class);
        process("group.id");        
    }
    
    private void process(String key) {
        process(key, null);
    }

    private void process(String key, String defaultValue) {
        String value = (String)confSource.get(key);
        String usedValue = (value != null) ? value : defaultValue;
        if (usedValue != null) {
            config.put(key, usedValue);
        }
    }

}
