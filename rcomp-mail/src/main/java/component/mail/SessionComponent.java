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
package component.mail;

import java.util.Map;
import java.util.Properties;

import javax.mail.Session;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component
public class SessionComponent {

    private ServiceRegistration<Session> sreg;

    @Activate
    public void activate(Map<String, Object> config, BundleContext context) {
        Session session = create(config);
        sreg = context.registerService(Session.class, session, null);
    }
    
    @Deactivate
    public void deactivate() {
        sreg.unregister();
    }
    
    public static Session create(Map<String, Object> config) {
        Properties props = fromConfig(config);
        return Session.getDefaultInstance(props);
    }
    
    private static Properties fromConfig(Map<String, Object> config) {
        Properties props = new Properties();
        for (String key : config.keySet()) {
            props.put(key, config.get(key));
        }
        return props;
    }
}
