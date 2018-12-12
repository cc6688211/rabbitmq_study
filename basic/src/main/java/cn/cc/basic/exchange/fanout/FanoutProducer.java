/**
 * @模块名：basic
 * @包名：cn.cc.basic.exchange.fanout
 * @描述：FanoutProducer.java
 * @版本：1.0
 * @创建人：cc
 * @创建时间：2018年12月12日下午3:59:57
 */

package cn.cc.basic.exchange.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @模块名：basic
 * @包名：cn.cc.basic.exchange.fanout
 * @类名称： FanoutProducer
 * @类描述：【类描述】 Fanout类型交换机消息生产类
 * @版本：1.0
 * @创建人：cc
 * @创建时间：2018年12月12日下午3:59:57
 */

public class FanoutProducer {

    // 连接信息
    public final static String USER_NAME = "test";

    public final static String PASSWORD = "123456";

    public final static String VHOST = "/test";

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
            // BuiltinExchangeType.fanout
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout", false);

            /**
             * 推送消息
             */
            for (int i = 0; i < 9; i++) {
                // 指明推送的路由键
                String routingkey = ROUTINGKEYS[i % 3];
                // 推送消息
                String msg = "Hellol,RabbitMq_" + routingkey;
                // 发布消息，需要参数：交换器，路由键，
                channel.basicPublish(EXCHANGE_NAME, routingkey, null, msg.getBytes());
                System.out.println("Sent " + EXCHANGE_NAME + ":" + routingkey + ":" + msg);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
