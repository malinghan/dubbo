/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.bootstrap;

import org.apache.dubbo.bootstrap.rest.UserService;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.context.ConfigManager;

/**
 * Dubbo Provider Bootstrap
 *
 * @since 2.7.5
 */
public class ZookeeperDubboServiceConsumerBootstrap {

    public static void main(String[] args) throws Exception {

        new DubboBootstrap()
                .application("zookeeper-dubbo-consumer")
                .registry("zookeeper", builder -> builder.address("zookeeper://127.0.0.1:2181?registry-type=service&subscribed-services=zookeeper-dubbo-provider"))
                .reference("echo", builder -> builder.interfaceClass(EchoService.class).protocol("dubbo"))
                .reference("user", builder -> builder.interfaceClass(UserService.class).protocol("rest"))
                .start()
                .await();

        ConfigManager configManager = ConfigManager.getInstance();

        ReferenceConfig<EchoService> referenceConfig = configManager.getReference("echo");

        EchoService echoService = referenceConfig.get();

        ReferenceConfig<UserService> referenceConfig2 = configManager.getReference("user");

        UserService userService = referenceConfig2.get();

        for (int i = 0; i < 500; i++) {
            Thread.sleep(2000L);
            System.out.println(echoService.echo("Hello,World"));
            System.out.println(userService.getUser(i * 1L));
        }


    }
}