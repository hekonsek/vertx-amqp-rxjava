package com.github.hekonsek.vertx.amqp.rxjava;

import io.reactivex.Completable;
import io.vertx.proton.ProtonConnection;
import org.apache.qpid.proton.message.Message;

import static io.vertx.proton.ProtonHelper.message;

public class AmqpEvent {

    private final ProtonConnection connection;

    private final Message message;

    private final ReplyStrategy replyStrategy;

    public AmqpEvent(ProtonConnection connection, Message message, ReplyStrategy replyStrategy) {
        this.connection = connection;
        this.message = message;
        this.replyStrategy = replyStrategy;
    }

    public Completable reply(Object response) {
        return replyStrategy.reply(this, response);
    }

    public ProtonConnection getConnection() {
        return connection;
    }

    public Message getMessage() {
        return message;
    }

}