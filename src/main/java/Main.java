import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        JdbcTemplate template = databaseConfiguration.getTemplate();

        Alerta alerta = new Alerta();
        LeituraCsv leituraCsv = new LeituraCsv();

        alerta.salvaTabelaAlerta("captura_srv1.csv");
        leituraCsv.leImportaArquivoCsv("captura_srv1.csv");
    }
}