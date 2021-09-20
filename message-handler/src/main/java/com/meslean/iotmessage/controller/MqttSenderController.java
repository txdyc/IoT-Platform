package com.meslean.iotmessage.controller;

import com.meslean.iotmessage.configuration.IMqttSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqttSenderController {
    @Autowired
    private IMqttSender iMqttSender;

    /**
     * 发送自定义消息内容，使用默认主题
     * @param data 消息
     */
    @RequestMapping("/test1/{data}")
    public void test1(@PathVariable("data") String data) {
        iMqttSender.sendToMqtt(data);
    }

    /**
     * 发送自定义消息内容，指定主题
     * @param topic 主题
     * @param data 消息
     */
    @RequestMapping("/test2/{topic}/{data}")
    public void test2(@PathVariable("topic") String topic,
                      @PathVariable("data") String data) {
        iMqttSender.sendToMqtt(topic, data);
    }
}
