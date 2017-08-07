/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.rcomp.itest;
import static org.apache.karaf.rcomp.bndrun.BndRunOption.bndRun;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

@RunWith(PaxExam.class)
public class EventAdminTest {
    @Inject
    EventAdmin eventAdmin;

    @Configuration
    public Option[] configure() throws Exception {
        return options(bndRun("rcomp-example.bndrun", "cnf/cache/pom-rcomp.xml"));
    }

    @Test
    public void test() {
        Map<String, String> properties = new HashMap<>();
        eventAdmin.sendEvent(new Event("eainput", properties));
    }
}
