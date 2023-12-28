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
package io.microsphere.micrometer.instrument.binder.system;

import io.microsphere.metrics.micrometer.instrument.binder.system.CGroupMemoryMetrics;
import io.microsphere.micrometer.instrument.binder.AbstractMetricsTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;

/**
 * {@link CGroupMemoryMetrics} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class CGroupMemoryMetricsTest extends AbstractMetricsTest<CGroupMemoryMetrics> {

    @BeforeClass
    public static void prepare() throws Throwable {
        ClassLoader classLoader = CGroupMemoryMetricsTest.class.getClassLoader();
        System.setProperty("cgroup.memory.dir", Paths.get(classLoader.getResource("test-data/").toURI()).toAbsolutePath().toString());
    }

    @Test
    public void test() throws Throwable {
        assertFalse(registry.getMeters().isEmpty());
    }
}
