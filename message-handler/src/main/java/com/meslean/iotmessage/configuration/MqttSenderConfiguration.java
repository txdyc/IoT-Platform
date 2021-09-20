package com.meslean.iotmessage.configuration;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * MQTT消息生产者,配置
 */

@Configuration
public class MqttSenderConfiguration {
    /**
     * 发布的Bean名称
     */
    public static final String CHANNEL_NAME_OUT = "mqttOutboundChannel";

    /**
     * 客户端与服务器之间的连接意外中断时，服务器将发布客户端的遗嘱消息
     */
    private static final byte[] WILL_DATA;
    static {
        WILL_DATA = "offline".getBytes(StandardCharsets.UTF_8);
    }

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.url}")
    private String url;

    @Value("${mqtt.sender.clientId}")
    private String clientId;

    @Value("${mqtt.sender.defaultTopic}")
    private String defaultTopic;

    /**
     * MQTT 连接器选项
     * @return MqttConnectOptions
     */
    @Bean
    public MqttConnectOptions getSenderMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置连接用户名
        if (!username.trim().equals("")) {
            options.setUserName(username);
        }
        // 设置连接密码
        options.setPassword(password.toCharArray());
        // 设置连接地址
        options.setServerURIs(StringUtils.split(url, ","));
        // 设置连接的超时时间，单位秒
        options.setConnectionTimeout(10);
        // 设置会话心跳时间，单位秒，服务器会每隔1.5*20秒的时间想客户端发送心跳以判断客户端是否在线
        // 但该方法没有重连机制
        options.setKeepAliveInterval(20);
        // 设置遗嘱消息的topic
        options.setWill("willTopic", WILL_DATA, 2, false);

        return options;
    }

    /**
     * MQTT客户端
     * @return MqttPahoClientFactory
     */
    @Bean
    public MqttPahoClientFactory senderMqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getSenderMqttConnectOptions());

        return factory;
    }

    /**
     * MQTT信息通道 (生产者)
     * @return MessageChannel
     */
    @Bean(name = CHANNEL_NAME_OUT)
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT消息处理器 (生产者)
     * @return MessageHandler
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_OUT)
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                clientId,
                senderMqttClientFactory()
        );
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(defaultTopic);

        return messageHandler;
    }
}
