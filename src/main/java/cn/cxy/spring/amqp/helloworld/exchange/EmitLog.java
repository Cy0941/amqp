package cn.cxy.spring.amqp.helloworld.exchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Function: TODO
 * Reason: TODO ADD REASON(可选).</br>
 * Date: 2017/8/11 18:14 </br>
 *
 * @author: cx.yang
 * @since: Thinkingbar Web Project 1.0
 */
public class EmitLog {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        //TODO 如果没有队列绑定到交换器，消息将会被丢弃，因为没有消费者监听，这条消息将被丢弃
        //不再直接使用queue - 使用 exchange && fanout  --  fanout 会忽略 routingKey 参数
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        for (int i = 0; i < 20; i++) {
            String msg = "Hello World " + i;
            //使用自定义的 exchange fixme routingKey exchange与消息队列或主题对应关系解决？？
            channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + msg + "'");
        }
        channel.close();
        connection.close();
    }

}
