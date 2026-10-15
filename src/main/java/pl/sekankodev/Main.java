package pl.sekankodev;

import pl.sekankodev.repository.ComputerRepository;
import pl.sekankodev.service.ComputerService;
import pl.sekankodev.service.ConsoleUi;
import pl.sekankodev.service.NbpClient;
import pl.sekankodev.service.XmlComputerExporter;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.xml.XmlMapper;

import java.net.http.HttpClient;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final String DB_URL = "jdbc:h2:mem:computer_db;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE";
    
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL)){
            ConsoleUi consoleUi = getConsoleUi(connection);
            consoleUi.loop();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static ConsoleUi getConsoleUi(Connection connection) {HttpClient httpClient = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        NbpClient nbpClient = new NbpClient(httpClient, objectMapper);

        XmlMapper xmlMapper = new XmlMapper();
        XmlComputerExporter xmlComputerExporter = new XmlComputerExporter(xmlMapper);

        ComputerRepository computerRepository = new ComputerRepository(connection);
        computerRepository.createTableIfNotExists();

        ComputerService computerService = new ComputerService(computerRepository, nbpClient, xmlComputerExporter);
        Scanner scanner = new Scanner(System.in);

        return new ConsoleUi(scanner, computerService);
    }
}