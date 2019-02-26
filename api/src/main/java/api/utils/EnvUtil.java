package api.utils;

import java.util.Optional;

public class EnvUtil {
    public static String getEnv(String name) {
        return Optional.ofNullable(System.getenv(name))
                .orElseThrow(() -> new RuntimeException("missing env " + name));
    }
}
