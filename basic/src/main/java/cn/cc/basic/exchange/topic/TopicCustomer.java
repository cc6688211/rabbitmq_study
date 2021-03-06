/**
 * @模块名：basic
 * @包名：cn.cc.basic.exchange.topic
 * @描述：TopicCutomer.java
 * @版本：1.0
 * @创建人：cc
 * @创建时间：2018年12月12日下午4:16:05
 */

package cn.cc.basic.exchange.topic;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * @模块名：basic
 * @包名：cn.cc.basic.exchange.topic
 * @类名称： TopicCutomer
 * @类描述：【类描述】
 * @版本：1.0
 * @创建人：cc
 * @创建时间：2018年12月12日下午4:16:05
 */

public class TopicCustomer {

    // 连接信息
    public final static String USER_NAME = "test";

    public final static String PASSWORD = "123456";

    public final static String VHOST = "%2Ftest";

    public final static String HOST = "47.99.159.236";

    public final static Integer PORT = 5672;

    // 队列信息
    // 交换机名称
    public final static String EXCHANGE_NAME = "test-exchangeName_topic_1";

    // 队列名称
    public final static String QUEUENAME = "test-queueName_topic_1";

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
            // BuiltinExchangeType.topic
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", false);

            // 声明一个随机队列
            // String queueName = channel.queueDeclare().getQueue();
            // channel.queueDeclare(QUEUENAME, false, false, false, null);
            /**
             * 声明一个队列
             */
            // 指定队列名称+是否持久化+是否独占+非自动删除
            channel.queueDeclare(QUEUENAME, false, false, false, null);

            /**
             * 绑定，将队列和交换器通过路由键进行绑定
             * 
             * 注意多重绑定后是不会覆盖前面的
             */
            // #接收所有路由键信息
            // channel.queueBind(QUEUENAME, EXCHANGE_NAME, "#");
            // 若只匹配前后用#，其他用*
            channel.queueUnbind(QUEUENAME, EXCHANGE_NAME, "#");
            channel.queueBind(QUEUENAME, EXCHANGE_NAME, "test-A.#");
            // channel.queueBind(QUEUENAME, EXCHANGE_NAME, "#.test-1");
            // channel.queueBind(QUEUENAME, EXCHANGE_NAME, "*.test-a.*");

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
