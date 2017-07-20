package net.lr.bndrun;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

class RunBundles {
    /**
     * Parses a string of bundle refs in the style of bndtools -runbundles
     *   
     * @param bundlesSt comma separated list of bundle refs
     * @return list of bundle refs
     */
    static List<BundleRef> parse(String bundlesSt) {
        StringTokenizer tokenizer = new StringTokenizer(bundlesSt, "',;[)");
        List<BundleRef> bundles = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            BundleRef artifact = parseBundleRef(tokenizer);
            bundles.add(artifact);
        }
        return bundles;
    }

    private static BundleRef parseBundleRef(StringTokenizer tokenizer) {
        BundleRef bundle = new BundleRef();
        bundle.symbolicName = tokenizer.nextToken();
        tokenizer.nextToken();
        bundle.version = tokenizer.nextToken();
        tokenizer.nextToken();
        return bundle;
    }

    static class BundleRef {
        String symbolicName;
        String version;
    }
}
