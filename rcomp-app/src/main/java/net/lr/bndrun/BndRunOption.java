package net.lr.bndrun;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.bundlerepository.Repository;
import org.apache.felix.bundlerepository.impl.DataModelHelperImpl;
import org.apache.felix.utils.properties.Properties;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;

import net.lr.bndrun.RunBundles.BundleRef;

public class BndRunOption {
    private List<Option> options = new ArrayList<>();
    private Properties props = new Properties();
    
    public static Option bndRun(String bndRunPath, String repositoryPath) throws Exception {
        URL repoURL = new File(repositoryPath).toURI().toURL();
        File bndRunFile = new File(bndRunPath);
        List<Option> options = new BndRunOption(bndRunFile, repoURL).options;
        return composite(options.toArray(new Option[] {}));
    }

    private BndRunOption(File bndRunFile, URL repoURL) throws Exception {
        props.load(new FileInputStream(bndRunFile));
        DataModelHelperImpl dataModelHelper = new DataModelHelperImpl();
        Repository repository = dataModelHelper.repository(repoURL);
        addRunBundles(repository);
        addSystemPackages(options, props);
    }

    private void addRunBundles(Repository repository) {
        BundleUris bundleUris = new BundleUris(repository);
        String runBundles = (String)props.get("-runbundles");
        List<BundleRef> bundles = RunBundles.parse(runBundles);
        
        for (BundleRef bundle : bundles) {
            String uri = bundleUris.getUri(bundle.symbolicName, bundle.version);
            options.add(CoreOptions.bundle(uri));
        }
    }

    private void addSystemPackages(List<Option> options, Properties props) {
        String systemPackageSt = (String)props.get("-runsystempackages");
        if (systemPackageSt != null) {
            String[] systemPackages = systemPackageSt.split(",");
            for (String systemPackage : systemPackages) {
                options.add(systemPackage(systemPackage));
            }
        }
    }

}
