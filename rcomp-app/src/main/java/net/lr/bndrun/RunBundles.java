package net.lr.bndrun;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

class RunBundles {
    static List<BundleRef> parse(String bundles) {
        StringTokenizer tokenizer = new StringTokenizer(bundles, "',;[)");
        List<BundleRef> artifacts = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            BundleRef artifact = parseBundleRef(tokenizer);
            artifacts.add(artifact);
        }
        return artifacts;
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
