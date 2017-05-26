package watchme.http;

import java.util.Arrays;

public final class JSONResponses {

    public static final String ERROR_INCORRECT_REQUEST;
    static {
        ERROR_INCORRECT_REQUEST = "Incorrect request";
    }

    public static final String POST_REQUIRED;
    static {
        POST_REQUIRED = "This request is only accepted using POST!";
    }

    public static final String FEATURE_NOT_AVAILABLE;
    static {
        FEATURE_NOT_AVAILABLE = "Feature not available";
    }

    public static String missing(String... paramNames) {
      String response = "";
      if (paramNames.length == 1) {
          response = "\"" + paramNames[0] + "\"" + " not specified";
      } else {
          response = "At least one of " + Arrays.toString(paramNames) + " must be specified";
      }
      return response;
    }

    public static String incorrect(String paramName) {
        return incorrect(paramName, null);
    }

    public static String incorrect(String paramName, String details) {
        return "Incorrect \"" + paramName + (details != null ? "\" " + details : "\"");
    }

    public static String unknown(String objectName) {
        return "Unknown " + objectName;
    }

    private JSONResponses() {} // never

}
