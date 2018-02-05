package com.github.hekonsek.vertx.amqp.rxjava;

import io.reactivex.Completable;

public interface ReplyStrategy {

    Completable reply(AmqpEvent event, Object response);

}