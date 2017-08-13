package cn.cxy.spring.amqp.helloworld.queue;

import com.rabbitmq.client.*;
import com.rabbitmq.client.Consumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Function: TODO
 * Reason: TODO ADD REASON(可选).</br>
 * Date: 2017/8/11 11:46 </br>
 *
 * @author: cx.yang
 * @since: Thinkingbar Web Project 1.0
 */
public class Worker1 {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(TASK_QUEUE_NAME,false,false,false,null);
        System.out.println("Worker1--- [*] Waiting for Message.To exit press Ctrl + C");
        //TODO 每次从队列中获取的数量
        channel.basicQos(1);
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "utf-8");
                System.out.println("Worker1--- [x] Received '" + msg + "'");
                try {
                    doWork();
                } finally {
                    System.out.println("Worker1--- [x] Done");
                    //fixme 消息处理完成确认
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        };
        //fixme 消息消费完成确认
        channel.basicConsume(TASK_QUEUE_NAME,false,consumer);
    }

    private static void doWork(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
