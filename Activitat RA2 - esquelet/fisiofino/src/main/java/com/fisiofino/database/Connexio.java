package com.fisiofino.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


/****AQUESTA CLASSE DEFINEIX LA CONNEXIÓ I EL DATASOURCE */

public class Connexio {

    /************** S'HA D'IMPLEMENTAR TOT INCLOSES LES FUNCIONS
                  DEFINIDES A SOTA        ********************/
    private static final Path FITXER_CONFIG = Path.of("src", "main", "resources", "conn.properties");
    private static HikariDataSource pool = null;

    private static Properties llegirPropietats() throws IOException {
        Properties p = new Properties();
        try (BufferedReader lector = Files.newBufferedReader(FITXER_CONFIG)) {
            p.load(lector);
        }
        return p;
    }

    public static void iniciaConnexio () throws IOException {
       /************* HAS DE COMPLETAR EL CODI ******************/
       if (pool == null) {
            Properties p = llegirPropietats();
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(p.getProperty("DB_URL"));
            config.setUsername(p.getProperty("DB_USER"));
            config.setPassword(p.getProperty("DB_PASSWORD"));
            config.setMaximumPoolSize(Integer.parseInt(p.getProperty("DB_MAX_POOL", "10")));

            pool = new HikariDataSource(config);
        }
    }
    //==================================================================0
    public static Connection getConnection() throws SQLException {
        if (pool == null) {
            throw new IllegalStateException("Cal cridar iniciaConnexio() abans d'obtenir connexions");
        }
        return pool.getConnection();
    }


    public static void tancaConnexio () {
    /************* HAS DE COMPLETAR EL CODI ******************/
    if (pool != null) {
            pool.close();
            pool = null;
        }
    }  

}





