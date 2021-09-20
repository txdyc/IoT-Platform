package com.meslean.iotmessage.configuration;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@MessagingGateway(defaultRequestChannel = MqttSenderConfiguration.CHANNEL_NAME_OUT)
public interface IMqttSender {
    /**
     * 发送信息到MQTT服务器
     * @param data 发送的文本
     */
    void sendToMqtt(String data);

    /**
     * 发送信息到MQTT服务器
     * @param topic 主题
     * @param payload 消息主体
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);

    /**
     * 发送消息到MQTT服务器
     * @param topic 主题
     * @param qos 消息处理机制，0，1，2
     * @param payload 消息主体
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic,
                    @Header(MqttHeaders.QOS) int qos,
                    String payload);
}
