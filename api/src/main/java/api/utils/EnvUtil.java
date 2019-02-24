package api.utils;

import java.util.Optional;

public class EnvUtil {
    public static String getEnv(String name, String defVal) {
        return Optional.ofNullable(System.getenv(name))
                .orElse(defVal);
    }
}
