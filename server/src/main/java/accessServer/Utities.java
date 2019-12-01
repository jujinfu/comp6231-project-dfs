package accessServer;

import org.apache.commons.lang3.StringUtils;

public class Utities {
    private static final String SLASH = "/";

    public static String getFileName(String uri) {
        return uri.substring(uri.lastIndexOf(SLASH) + 1, uri.length());
    }

    public static String getUpperParent(String uri) {
        String parentAbs = getFileParent(uri);
        return StringUtils.isEmpty(parentAbs) ? SLASH : uri.substring(parentAbs.lastIndexOf(SLASH) + 1, parentAbs.length());
    }

    public static String getFileParent(String uri) {
        return uri.substring(0, uri.lastIndexOf(SLASH));
    }

    public static boolean isUriValid(String uri) {
        return StringUtils.isEmpty(uri) || uri.startsWith(SLASH) || StringUtils.isEmpty(getFileName(uri)) ;
    }
}
