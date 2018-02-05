package com.github.hekonsek.vertx.amqp.rxjava;

import io.reactivex.Completable;

import static io.vertx.proton.ProtonHelper.message;

public class SenderPerReplyStrategy implements ReplyStrategy {

    @Override
    public Completable reply(AmqpEvent event, Object response) {
        return Completable.create(x -> {
            event.getConnection().createSender(event.getMessage().getReplyTo()).open().send(message(response + ""), y -> x.onComplete());
        });
    }

}