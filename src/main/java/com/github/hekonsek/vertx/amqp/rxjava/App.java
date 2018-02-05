package com.github.hekonsek.vertx.amqp.rxjava;

import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonHelper;
import org.apache.qpid.proton.message.Message;

import static com.github.hekonsek.vertx.amqp.rxjava.AmqpFlowables.amqpFlowable;
import static io.vertx.core.Vertx.vertx;
import static java.util.concurrent.TimeUnit.SECONDS;

public class App {

    public static void main(String[] args) throws InterruptedException {
        String serviceAddress = "foo";
        amqpFlowable(serviceAddress).subscribe(event -> event.reply("Yo!").subscribe());

        SECONDS.sleep(1);

        ProtonClient.create(vertx()).connect("localhost", 5672, con -> {
            con.result().open().createReceiver("foo-response").open().handler((x,y) -> System.out.println(y.getBody()));
        });

        SECONDS.sleep(1);

        ProtonClient.create(vertx()).connect("localhost", 5672, con -> {
            Message message = ProtonHelper.message("xxx");
            message.setReplyTo("foo-response");
            con.result().open().createSender(serviceAddress).open().send(message);
        });
    }

}
