package cn.cxy.spring.amqp.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Function: TODO
 * Reason: TODO ADD REASON(可选).</br>
 * Date: 2017/8/14 17:54 </br>
 *
 * @author: cx.yang
 * @since: Thinkingbar Web Project 1.0
 */
public class RPCClient {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private String replyQueueName;
    private QueueingConsumer consumer;

    public RPCClient() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();

        replyQueueName = channel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(replyQueueName, true, consumer);

    }

    public static void main(String[] args) {
        RPCClient fibonacciRpc = null;
        String response;
        try {
            fibonacciRpc = new RPCClient();
            System.out.println("RPCClient [x] Requesting fib(30)");
            response = fibonacciRpc.call("30");
            System.out.println("RPCClient [.] Got '" + response + "'");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fibonacciRpc) {
                try {
                    fibonacciRpc.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 调用服务端的接口
     * @param msg
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private String call(String msg) throws IOException, InterruptedException {
        String response;
        String corrId = UUID.randomUUID().toString();

        //发送请求后，需要得到一个响应结果  --  cxy 构建回调队列的信息 & client - replayQueueName
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName).build();

        //向服务端发布请求
        channel.basicPublish("", requestQueueName, props, msg.getBytes("utf-8"));
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response = new String(delivery.getBody(), "utf-8");
                break;
            }
        }
        return response;
    }

    private void close() throws IOException {
        connection.close();
    }

}
