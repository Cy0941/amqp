package cn.cxy.spring.amqp.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Function: TODO
 * Reason: TODO ADD REASON(可选).</br>
 * Date: 2017/8/13 11:29 </br>
 *
 * @author: cx.yang
 * @since: Thinkingbar Web Project 1.0
 */
public class RoutingSendDirect {

    private static final String EXCHANGE_NAME = "direct_logs";
    // 路由关键字
    private static final String[] routingKeys = new String[]{"info", "warning", "error"};

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明 Exchange 为 direct 直链模式
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        for (String severity : routingKeys) {
            String msg = "Send the msg level: " + severity;
            //绑定 exchange_name 与 routtingKey  -- cxy 不绑定任何 queue - 相当于绑定了所有 queue ，通过 routingKey 进行过滤
            channel.basicPublish(EXCHANGE_NAME,severity,null,msg.getBytes());
            System.out.println(" [x] Sent '" + severity + "':'" + msg + "'");
        }
        channel.close();
        connection.close();
    }
}
