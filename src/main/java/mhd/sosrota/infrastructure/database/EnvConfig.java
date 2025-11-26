package mhd.sosrota.infrastructure.database;

import io.github.cdimascio.dotenv.Dotenv;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 25/11/2025
 * @brief Class EnvConfig
 */
public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String key) {
        return dotenv.get(key);
    }
}
