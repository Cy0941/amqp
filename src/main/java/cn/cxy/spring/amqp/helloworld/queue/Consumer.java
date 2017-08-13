package cn.cxy.spring.amqp.helloworld.queue;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

/**
 * Function: TODO
 * Reason: TODO ADD REASON(可选).</br>
 * Date: 2017/8/11 10:34 </br>
 *
 * @author: cx.yang
 * @since: Thinkingbar Web Project 1.0
 */
public class Consumer {

    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        //声明要关注的队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("C [*] Waiting for Message.To exit press Ctrl + C");
        //cxy DefaultConsumer 实现了 Consumer 接口，传入一个 Channel ，当 Channel 中有消息时会执行回调函数 handleDelivery
        com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, Charset.forName("UTF-8"));
                System.out.println("C [x] Received '" + message + "'");
            }
        };
        //cxy 自动回复队列应答 - RabbitMQ 中的消息确认机制
        channel.basicConsume(QUEUE_NAME,true,consumer);
    }

}
