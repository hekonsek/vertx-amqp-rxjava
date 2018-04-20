package com.github.hekonsek.vertx.amqp.rxjava.kapua;

import io.reactivex.Single;

public interface DeviceRegistryService {

    Single<Device> create(Single<Device> single);

}
