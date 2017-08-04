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
package net.lr.bndrun;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.bundlerepository.Repository;
import org.apache.felix.bundlerepository.Resource;
import org.osgi.framework.Version;

/**
 * Wraps a Repository and allows to get the URI of each resource by
 * symbolic name and version
 */
class BundleUris {
    Map<String, String> bundleUri = new HashMap<>();

    BundleUris(Repository repository) {
        for (Resource res : repository.getResources()) {
            bundleUri.put(getKey(res) , res.getURI());
        }
    }

    private String getKey(Resource res) {
        Version version = res.getVersion();
        Version simpleVersion = new Version(version.getMajor(), version.getMinor(), version.getMicro());
        return res.getSymbolicName() + ";" + simpleVersion.toString();
    }
    
    String getUri(String symbolicName, String version) {
        String uri = bundleUri.get(symbolicName + ";" + version);
        if (uri == null) {
            throw new IllegalArgumentException("Bundle not found in repository " + symbolicName + ";" + version);
        }
        return uri;
    }

}
