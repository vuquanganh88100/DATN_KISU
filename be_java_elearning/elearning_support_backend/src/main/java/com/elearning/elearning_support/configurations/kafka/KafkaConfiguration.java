package com.elearning.elearning_support.configurations.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import com.elearning.elearning_support.constants.KafkaConstants;
import com.elearning.elearning_support.dtos.kafka.KafkaMessageDTO;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;

@Configuration
@EnableKafka
public class KafkaConfiguration {

    @Value("#{'${spring.kafka.bootstrap-servers:}'.split(',')}")
    private List<String> bootstrapServerAddresses;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /*
        ========= PRODUCER CONFIG =========
     */
    @Bean(name = "kafkaProducerFactory")
    public ProducerFactory<String, Object> kafkaProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerAddresses);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "1"); // LEADER
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(){
        return new KafkaTemplate<>(kafkaProducerFactory());
    }

    /*
        ========= CONSUMER CONFIG =========
     */
    @Bean(name = "kafkaConsumerFactory")
    public ConsumerFactory<String, KafkaMessageDTO> kafkaConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerAddresses);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // error handler when serializing
        ErrorHandlingDeserializer<String> keyHandlingDeserializer
            = new ErrorHandlingDeserializer<>(new StringDeserializer());
        ErrorHandlingDeserializer<KafkaMessageDTO> valueHandlingDeserializer
            = new ErrorHandlingDeserializer<>(
            new JsonDeserializer<>(KafkaMessageDTO.class, Jackson2ObjectMapperBuilder.json()
                .visibility(PropertyAccessor.FIELD, Visibility.ANY)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build()));
        return new DefaultKafkaConsumerFactory<>(configProps, keyHandlingDeserializer, valueHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessageDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMessageDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory());
        factory.getContainerProperties().setAckMode(AckMode.RECORD);
        return factory;
    }


//    @Bean
//    public NewTopic defaultKafkaTopic() {
//        return new NewTopic(KafkaConstants.ELS_DEFAULT_TOPIC_NAME, 2, (short)3);
//    }
}
