package com.leyou.listener;

import com.aliyuncs.exceptions.ClientException;
import com.leyou.pojo.Sms;
import com.leyou.pojo.SmsUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {
    @Autowired
    SmsUtils smsUtils;
    @Autowired
    Sms sms;
    //接受信息
    //这个方法用来监视是否接收到了信息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.sms.queue", durable = "true"),
            exchange = @Exchange(value = "leyou.sms.exchange",
                    ignoreDeclarationExceptions = "true",
            type = ExchangeTypes.TOPIC),
            key = {"sms.verify.code"}))
    public void send(Map<String,String> map) throws ClientException {
       smsUtils.sendSms(map.get("phone"),map.get("code"),sms.getSignName(),sms.getTemplateCode());
    }
}
