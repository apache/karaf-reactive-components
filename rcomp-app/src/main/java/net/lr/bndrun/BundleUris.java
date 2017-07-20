package net.lr.bndrun;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.bundlerepository.Repository;
import org.apache.felix.bundlerepository.Resource;
import org.osgi.framework.Version;

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
