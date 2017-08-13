package cn.cxy.spring.amqp.helloworld.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Function: TODO
 * Reason: TODO ADD REASON(可选).</br>
 * Date: 2017/8/11 10:34 </br>
 *
 * @author: cx.yang
 * @since: Thinkingbar Web Project 1.0
 */
public class Producer {

    private static final String QUEUE_NAME = "hello";
    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置RabbitMQ地址
        connectionFactory.setHost("localhost");
        //创建一个新连接
        Connection connection = connectionFactory.newConnection();
        //创建一个频道
        Channel channel = connection.createChannel();
        //cxy 声明一个队列 -- 在RabbitMQ中，队列时幂等性【执行N次的结果 == 执行1次的结果】的 - 如果不存在就创建；如果存在不会对已有队列产生影响
        //TODO CAUTIONS 对于已经定义的队列再次定义是无效的，否则返回异常  --  幂等性
        //建议将队列名称放到配置文件中
        //设置RabbitMQ记住当前状态和内容（对于未来得及持久化的消息仍然会丢失）  --  cxy 消息持久化 & RabbitMQ 不支持实时磁盘同步
        boolean durable = true;
        //queueDeclare 创建一个非持久化、独立、自动删除的队列名称
        //String queue = channel.queueDeclare().getQueue();
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        String msg = "Hello World ";

        //发送消息到队列中
        //1、基础测试
        //channel.basicPublish("", QUEUE_NAME, null, msg.getBytes("UTF-8"));
        //System.out.println("P [x] Sent '" + msg + "'");
        //2、模拟流水线
        for (int i = 0; i < 20; i++) {
            msg = "Hello World " + i;
            //exchange 参数为空，表示使用默认的匿名 exchange - 发送到 routingKey 名称对应的队列
            channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + msg + "'");
        }

        //关闭频道及链接
        channel.close();
        connection.close();
    }

}
