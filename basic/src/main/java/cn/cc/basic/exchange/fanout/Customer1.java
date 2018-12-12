/**
 * @模块名：basic
 * @包名：cn.cc.basic.exchange.fanout
 * @描述：Customer1.java
 * @版本：1.0
 * @创建人：cc
 * @创建时间：2018年12月12日下午4:01:29
 */

package cn.cc.basic.exchange.fanout;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * @模块名：basic
 * @包名：cn.cc.basic.exchange.fanout
 * @类名称： Customer1
 * @类描述：【类描述】单个绑定
 * @版本：1.0
 * @创建人：cc
 * @创建时间：2018年12月12日下午4:01:29
 */

public class Customer1 {

    // 连接信息
    public final static String USER_NAME = "test";

    public final static String PASSWORD = "123456";

    public final static String VHOST = "%2Ftest";

    public final static String HOST = "47.99.159.236";

    public final static Integer PORT = 5672;

    // 队列信息
    // 交换机名称
    public final static String EXCHANGE_NAME = "test-exchangeName_fanout_1";

    // 路由key
    public final static String[] ROUTINGKEYS = { "test-routingKey_fanout_1", "test-routingKey_fanout_2",
            "test-routingKey_fanout_3" };

    // 队列名称
    public final static String QUEUENAME = "test-queueName_fanout_1";

    public static void main(String[] args) {

        try {
            /**
             * mq连接
             */
            ConnectionFactory factory = new ConnectionFactory();
            // “guest”/“guest”默认情况下，仅限于localhost连接
            factory.setUri("amqp://" + USER_NAME + ":" + PASSWORD + "@" + HOST + ":" + PORT + "/" + VHOST);
            Connection conn = factory.newConnection();
            /**
             * 创建信道
             */
            Channel channel = conn.createChannel();

            /**
             * 创建交换器
             */
            // 指明交换器名称+类型+是否持久化
            // BuiltinExchangeType.fanout
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout", false);

            /**
             * 声明一个队列
             */
            // 指定队列名称+是否持久化+是否独占+非自动删除
            channel.queueDeclare(QUEUENAME, false, false, false, null);

            /**
             * 绑定，将队列和交换器通过路由键进行绑定
             */
            channel.queueBind(QUEUENAME, EXCHANGE_NAME, ROUTINGKEYS[0]);
            System.out.println("waiting for message........");

            /**
             * 声明了一个消费者
             */
            final Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                        byte[] body) {
                    String message;
                    try {
                        message = new String(body, "UTF-8");
                        System.out.println("Received[" + envelope.getRoutingKey() + "]" + message);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            /**
             * 消费者正式开始消费指定队列上消息
             */
            channel.basicConsume(QUEUENAME, true, consumer);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
