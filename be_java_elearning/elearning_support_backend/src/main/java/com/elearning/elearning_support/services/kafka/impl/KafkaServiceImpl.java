package com.elearning.elearning_support.services.kafka.impl;

import java.util.Objects;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import com.elearning.elearning_support.constants.KafkaConstants;
import com.elearning.elearning_support.constants.message.messageConst.MessageConst;
import com.elearning.elearning_support.dtos.kafka.KafkaMessageDTO;
import com.elearning.elearning_support.services.kafka.KafkaService;
import com.elearning.elearning_support.utils.object.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaServiceImpl implements KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendMessage(String topic, String key, Object data, ListenableFutureCallback<SendResult<String, Object>> callback) {
        try {
            KafkaMessageDTO message = KafkaMessageDTO.builder()
                .key(key)
                .topic(topic)
                .data(data)
                .build();
            ListenableFuture<SendResult<String, Object>> futureResult = kafkaTemplate.send(topic, key, message);
            // handle future result using callback
            if (Objects.nonNull(callback)) {
                futureResult.addCallback(callback);
            }
        } catch (Exception exception) {
            log.error(MessageConst.EXCEPTION_LOG_FORMAT, exception.getMessage(), exception.getCause().toString());
        }
    }


    /**
     * Consume a message from kafka
     */
    @KafkaListener(topics = KafkaConstants.ELS_DEFAULT_TOPIC_NAME, containerFactory = "kafkaListenerContainerFactory")
    public void consumeMessage(@Payload KafkaMessageDTO message, @Header(KafkaHeaders.RECEIVED_TOPIC) String receivedTopic,
        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int receivedPartitionId) {
        log.info("CONSUMED A MESSAGE = {} FROM TOPIC {} AT PARTITION {} ", ObjectMapperUtil.toJsonString(message), receivedTopic,
            receivedPartitionId);
    }

}
