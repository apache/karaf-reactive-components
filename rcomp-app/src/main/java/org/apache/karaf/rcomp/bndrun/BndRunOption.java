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
package org.apache.karaf.rcomp.bndrun;
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
import org.apache.karaf.rcomp.bndrun.RunBundles.BundleRef;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;

/**
 * Allow to use bndrun files for pax exam tests
 */
public class BndRunOption {
    private List<Option> options = new ArrayList<>();
    private Properties props = new Properties();
    
    /**
     * Get pax exam composite Option from the contents of a bndrun file 
     * 
     * @param bndRunPath
     * @param repositoryPath
     * @return
     * @throws Exception
     */
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
