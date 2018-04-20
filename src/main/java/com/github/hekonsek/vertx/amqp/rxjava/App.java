package com.github.hekonsek.vertx.amqp.rxjava;

import com.github.hekonsek.vertx.amqp.rxjava.kapua.Device;
import com.github.hekonsek.vertx.amqp.rxjava.kapua.DeviceRegistryService;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonHelper;
import org.apache.qpid.proton.message.Message;

import static com.github.hekonsek.vertx.amqp.rxjava.AmqpFlowables.amqpFlowable;
import static io.vertx.core.Vertx.vertx;
import static java.util.concurrent.TimeUnit.SECONDS;

public class App {

    public static void main(String[] args) throws InterruptedException {
        DeviceRegistryService deviceRegistryService = null;
        String devicesUpdates = "deviceUpdates";

        // Generic reply
        amqpFlowable(devicesUpdates).subscribe(event -> event.reply("New device update confirmed.").subscribe());

        // Reply with confirmation
        amqpFlowable(devicesUpdates).subscribe(event ->
                event.reply("New device update confirmed.").
                        subscribe(() -> System.out.print("Confirmation received by client."))
        );

        // Invoking blocking API
        amqpFlowable(devicesUpdates).subscribe(event -> {
            Device dev = syncKapuaService.get();
            event.reply(dev).subscribe();
        });

        // Invoking non-blocking API
        amqpFlowable(devicesUpdates).flatMap( deviceUpdate -> done ->
                deviceRegistryService.create(deviceUpdate.body(Device.class)).subscribe(done::onNext)
        ).subscribe();

        // Tests setup

        SECONDS.sleep(1);

        ProtonClient.create(vertx()).connect("localhost", 5672, con -> {
            con.result().open().createReceiver("foo-response").open().handler((x,y) -> System.out.println(y.getBody()));
        });

        SECONDS.sleep(1);

        ProtonClient.create(vertx()).connect("localhost", 5672, con -> {
            Message message = ProtonHelper.message("xxx");
            message.setReplyTo("foo-response");
            con.result().open().createSender(devicesUpdates).open().send(message);
        });
    }

}
