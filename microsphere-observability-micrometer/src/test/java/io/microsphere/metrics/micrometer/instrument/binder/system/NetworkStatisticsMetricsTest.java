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
package io.microsphere.metrics.micrometer.instrument.binder.system;

import io.microsphere.metrics.micrometer.instrument.binder.AbstractMetricsTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static io.microsphere.metrics.micrometer.instrument.binder.system.NetworkStatisticsMetrics.STATS_FILE_PATH;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static org.junit.Assert.assertFalse;

/**
 * {@link NetworkStatisticsMetrics} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class NetworkStatisticsMetricsTest extends AbstractMetricsTest<NetworkStatisticsMetrics> {

    @BeforeClass
    public static void prepare() throws Throwable {
        ClassLoader classLoader = NetworkStatisticsMetricsTest.class.getClassLoader();
        String testFile = Paths.get(classLoader.getResource("test-data/network.stats").toURI()).toAbsolutePath().toString();
        System.setProperty("network.stats.file", testFile);
    }

    @Test
    public void test() throws Throwable {
        assertFalse(registry.getMeters().isEmpty());

        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path dir = STATS_FILE_PATH.getParent();
        dir.register(watchService, ENTRY_MODIFY);

        Thread thread = new Thread(() -> {
            WatchKey key;
            while (true) {
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (key == null) {
                    continue;
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path path = (Path) event.context();
                    if (STATS_FILE_PATH.equals(dir.resolve(path))) {
                        assertFalse(registry.getMeters().isEmpty());
                    }
                }
            }
        });


        File statsFile = STATS_FILE_PATH.toFile();
        statsFile.setLastModified(System.currentTimeMillis());

        thread.join(5000);
    }
}
