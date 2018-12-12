/**
 * @模块名：basic
 * @包名：cn.cc.basic.exchange.topic
 * @描述：topicProducer.java
 * @版本：1.0
 * @创建人：cc
 * @创建时间：2018年12月12日下午3:59:57
 */

package cn.cc.basic.exchange.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @模块名：basic
 * @包名：cn.cc.basic.exchange.topic
 * @类名称： topicProducer
 * @类描述：【类描述】 topic类型交换机消息生产类
 * @版本：1.0
 * @创建人：cc
 * @创建时间：2018年12月12日下午3:59:57
 */

public class TopicProducer {

    // 连接信息
    public final static String USER_NAME = "test";

    public final static String PASSWORD = "123456";

    public final static String VHOST = "/test";

    public final static String HOST = "47.99.159.236";

    public final static Integer PORT = 5672;

    // 队列信息
    // 交换机名称
    public final static String EXCHANGE_NAME = "test-exchangeName_topic_1";

    // 路由key
    public final static String[] ROUTINGKEYS1 = { "test-A", "test-B", "test-C" };

    public final static String[] ROUTINGKEYS2 = { "test-a", "test-b", "test-c" };

    public final static String[] ROUTINGKEYS3 = { "test-1", "test-2", "test-3" };

    // 队列名称
    public final static String QUEUENAME = "test-queueName_topic_1";

    public static void main(String[] args) {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            // “guest”/“guest”默认情况下，仅限于localhost连接
            factory.setUsername(USER_NAME);// 用户名
            factory.setPassword(PASSWORD);// 用户密码
            factory.setVirtualHost(VHOST);// 虚拟主机
            factory.setHost(HOST);// 主机名
            factory.setPort(PORT);// 端口号
            // factory.setUri("amqp://" + USER_NAME + ":" + PASSWORD + "@" + HOST + ":" + PORT +"/"+ VHOST);
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

            /**
             * 推送消息
             */
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 3; k++) {
                        // 发送的消息
                       
                        String routeKey = ROUTINGKEYS1[i % 3] + "." + ROUTINGKEYS2[j % 3] + "." + ROUTINGKEYS3[k % 3];
                        String message = "Hello Topic_[" + i + "," + j + "," + k + "]";
                        channel.basicPublish(EXCHANGE_NAME, routeKey, null, message.getBytes());
                        System.out.println(" [x] Sent '"+routeKey+":" + message + "'");
                    }
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
