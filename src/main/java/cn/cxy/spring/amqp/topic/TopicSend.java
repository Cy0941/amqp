package cn.cxy.spring.amqp.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Function: TODO
 * Reason: TODO ADD REASON(可选).</br>
 * Date: 2017/8/13 12:09 </br>
 *
 * @author: cx.yang
 * @since: Thinkingbar Web Project 1.0
 */
public class TopicSend {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        //String[] routingKeys = new String[]{
        //        "quick.orange.rabbit",
        //        "lazy.orange.elephant",
        //        "quick.orange.fox",
        //        "lazy.brown.fox",
        //        "quick.brown.fox",
        //        "quick.orange.male.rabbit",
        //        "lazy.orange.male.rabbit"};
        String[] routingKeys = {"tt"};
        for (String severity : routingKeys) {
            String message = "From " + severity + " routingKey' s message!";
            channel.basicPublish(EXCHANGE_NAME, severity, null, severity.getBytes("utf-8"));
            System.out.println("TopicSend [x] Sent '" + severity + "':'" + message + "'");
        }
        channel.close();
        connection.close();
    }

}
