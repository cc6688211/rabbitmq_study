/**
 * @模块名：basic
 * @包名：cn.cc.basic.exchange.direct
 * @描述：MulitConsumerOneQueue.java
 * @版本：1.0
 * @创建人：cc
 * @创建时间：2018年12月12日下午3:49:17
 */

package cn.cc.basic.exchange.direct;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * @模块名：basic
 * @包名：cn.cc.basic.exchange.direct
 * @类名称： MulitConsumerOneQueue
 * @类描述：【类描述】一个队列多个消费者，则会表现出消息在消费者之间的轮询发送。
 * @版本：1.0
 * @创建人：cc
 * @创建时间：2018年12月12日下午3:49:17
 */

public class MulitConsumerOneQueue {
    // 连接信息
    public final static String USER_NAME = "test";

    public final static String PASSWORD = "123456";

    public final static String VHOST = "%2Ftest";

    public final static String HOST = "47.99.159.236";

    public final static Integer PORT = 5672;

    // 队列信息
    // 交换机名称
    public final static String EXCHANGE_NAME = "test-exchangeName_direct_1";

    // 路由key
    public final static String[] ROUTINGKEYS = { "test-routingKey_direct_1", "test-routingKey_direct_2",
            "test-routingKey_direct_3" };

    // 队列名称
    public final static String QUEUENAME = "test-queueName_direct_1";

    /**
     * 
     * @模块名：basic
     * @包名：cn.cc.basic.exchange.direct
     * @类名称： ConsumerWorker
     * @类描述：【类描述】创建消费者
     * @版本：1.0
     * @创建人：cc
     * @创建时间：2018年12月12日下午3:50:21
     */
    private static class ConsumerWorker implements Runnable {
        final Connection connection;

        final String queueName;

        public ConsumerWorker(Connection connection, String queueName) {
            this.connection = connection;
            this.queueName = queueName;
        }

        public void run() {
            try {
                /* 创建一个信道，意味着每个线程单独一个信道 */
                final Channel channel = connection.createChannel();
                channel.exchangeDeclare(EXCHANGE_NAME, "direct");

                /* 声明一个队列,rabbitmq，如果队列已存在，不会重复创建 */
                channel.queueDeclare(queueName, false, false, false, null);

                // 消费者名字，打印输出用
                final String consumerName = Thread.currentThread().getName();
                for (String routingkey : ROUTINGKEYS) {
                    // （多重绑定）
                    channel.queueBind(queueName, DirectProducer.EXCHANGE_NAME, routingkey);
                }
                System.out.println(" [" + consumerName + "] Waiting for messages:");

                // 创建队列消费者
                final Consumer consumerA = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                            byte[] body) throws IOException {
                        String message = new String(body, "UTF-8");
                        System.out.println(consumerName + " Received " + envelope.getRoutingKey() + ":'" + message
                                + "'");
                    }
                };
                channel.basicConsume(queueName, true, consumerA);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] argv) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri("amqp://" + USER_NAME + ":" + PASSWORD + "@" + HOST + ":" + PORT + "/" + VHOST);
            // 打开连接和创建频道，与发送端一样
            Connection connection = factory.newConnection();

            // 3个线程，线程之间共享队列,一个队列多个消费者
            for (int i = 0; i < 3; i++) {
                /* 将队列名作为参数，传递给每个线程 */
                Thread worker = new Thread(new ConsumerWorker(connection, EXCHANGE_NAME));
                worker.start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
