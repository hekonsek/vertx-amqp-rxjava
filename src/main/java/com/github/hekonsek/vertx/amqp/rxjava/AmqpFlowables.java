package com.github.hekonsek.vertx.amqp.rxjava;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.vertx.core.Vertx;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonConnection;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.Section;

public class AmqpFlowables {

    public static Flowable<AmqpEvent> amqpFlowable(Vertx vertx, ReplyStrategy replyStrategy, String address) {
        return Flowable.create(x -> {
            ProtonClient client = ProtonClient.create(vertx);
            client.connect("localhost", 5672, res -> {
                if (res.succeeded()) {
                    ProtonConnection connection = res.result();
                    connection.open();
                    connection.createReceiver(address).handler((delivery, msg) -> {
                        Section body = msg.getBody();
                        if (body instanceof AmqpValue) {
                            x.onNext(new AmqpEvent(connection, msg, replyStrategy));
                        }
                    }).open();
                } else {
                    res.cause().printStackTrace();
                }
            });
        }, BackpressureStrategy.BUFFER);
    }

    public static Flowable<AmqpEvent> amqpFlowable(String address) {
        return amqpFlowable(Vertx.vertx(), new SenderPerReplyStrategy(), address);
    }

}
