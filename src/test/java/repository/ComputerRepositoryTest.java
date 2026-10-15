package repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.sekankodev.enums.SortDirection;
import pl.sekankodev.enums.SortOption;
import pl.sekankodev.exception.DataAccessException;
import pl.sekankodev.model.Computer;
import pl.sekankodev.repository.ComputerRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

 public class ComputerRepositoryTest {
    Connection connection;
    ComputerRepository repo;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:" +
                UUID.randomUUID() +
                 ";DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1"
        );
        repo = new ComputerRepository(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Nested
    class createTableIfNotExistsTest {
        @Test
         void tableDoesNotExist_createTable() throws Exception {
            repo.createTableIfNotExists();

            ResultSet rs = connection.getMetaData().getTables(null, null, "computers", null);
            assertTrue(rs.next());
            rs.close();
        }

        @Test
         void callTwice_doesNotThrowException(){
            repo.createTableIfNotExists();
            assertDoesNotThrow(repo::createTableIfNotExists);
        }

        @Test
         void closedConnection_throwsDataAccessException() throws SQLException {
            connection.close();
            assertThrows(DataAccessException.class, repo::createTableIfNotExists);
        }
    }

    @Nested
    class InsertTest{
        List<Computer> computers;

        @BeforeEach
        void setUp() throws SQLException {
            repo.createTableIfNotExists();
            computers = new ArrayList<>();
            computers.add(new Computer("test", LocalDate.now(), BigDecimal.valueOf(100), BigDecimal.valueOf(120)));
            computers.add(new Computer("test2", LocalDate.now(), BigDecimal.valueOf(200), BigDecimal.valueOf(220)));

        }

        @Test
         void computerIterableIsNull_nothingHappens() throws SQLException {
            repo.insert(null);
            assertEquals(0, countRowsInComputerTable());

        }

        @Test
         void computerIterableIsEmpty_nothingHappens() throws SQLException {
            repo.insert(null);
            assertEquals(0, countRowsInComputerTable());
        }

        @Test
         void computerIterableContainsNull_throwsIllegalArgumentException(){
            computers.add(null);
            assertThrows(IllegalArgumentException.class, () -> repo.insert(computers));
        }

        @Test
         void validInput_computersAreInserted() throws SQLException {
            repo.insert(computers);
            assertEquals(2, countRowsInComputerTable());
        }

        @Test
         void closedConnection_throwsDataAccessException() throws SQLException {
            connection.close();
            assertThrows(DataAccessException.class, () -> repo.insert(computers));
        }

        @Test
         void invalidComputerData_rollsBackAndThrowsDataAccessException() throws SQLException {
            computers.add(new Computer(null, LocalDate.now(), BigDecimal.valueOf(100), BigDecimal.valueOf(120)));
            assertThrows(DataAccessException.class, () -> repo.insert(computers));
            assertEquals(0, countRowsInComputerTable());
        }

        private int countRowsInComputerTable() throws SQLException {
            try (Statement st = connection.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM computers")) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @Nested
    class FindAllTest{
        List<Computer> computers;

        @BeforeEach
        void setUp() throws SQLException {
            repo.createTableIfNotExists();
            computers = new ArrayList<>();
            computers.add(new Computer("btest1", LocalDate.of(2004,1,1), BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP), BigDecimal.valueOf(120).setScale(2, RoundingMode.HALF_UP)));
            computers.add(new Computer("atest2", LocalDate.of(2002,1,1), BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), BigDecimal.valueOf(220).setScale(2, RoundingMode.HALF_UP)));
            computers.add(new Computer("atest3", LocalDate.of(2000,1,1), BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), BigDecimal.valueOf(220).setScale(2, RoundingMode.HALF_UP)));


            repo.insert(computers);
        }

        @Test
         void sortOptionIsNothingSearchIsNull_returnsFullList(){
            List<Computer> result = repo.findAll(SortOption.NOTHING, SortDirection.ASC, null);
            assertEquals(computers, result);
        }

        @Test
         void searchIsBlank_returnsFilteredList(){
            List<Computer> result = repo.findAll(SortOption.NOTHING, SortDirection.ASC," ");
            assertEquals(computers, result);
        }

        @Test
         void searchTwoRecords_returnsFilteredList(){
            List<Computer> result = repo.findAll(SortOption.NOTHING, SortDirection.ASC,"atest");
            assertEquals(List.of(computers.get(1), computers.get(2)), result);
        }

        @Test
         void searchOneRecord_returnsFilteredList(){
            List<Computer> result = repo.findAll(SortOption.POST_DATE, SortDirection.ASC, "test1");
            assertEquals(List.of(computers.getFirst()), result);
        }

        @Test
         void sortByNameAsc_returnsSortedList(){
            List<Computer> result = repo.findAll(SortOption.NAME, SortDirection.ASC, "");
            assertEquals(List.of(computers.get(1), computers.get(2), computers.get(0)), result);
        }

        @Test
         void sortByNameDesc_returnsSortedList(){
            List<Computer> result = repo.findAll(SortOption.NAME, SortDirection.DESC, "");
            assertEquals(List.of(computers.get(0), computers.get(2), computers.get(1)), result);
        }

        @Test
         void sortByPostDateAsc_returnsSortedList(){
            List<Computer> result = repo.findAll(SortOption.POST_DATE, SortDirection.ASC, "");
            assertEquals(List.of(computers.get(2), computers.get(1), computers.get(0)), result);
        }

        @Test
         void sortByPostDateDesc_returnsSortedList(){
            List<Computer> result = repo.findAll(SortOption.POST_DATE, SortDirection.DESC, "");
            assertEquals(List.of(computers.get(0), computers.get(1), computers.get(2)), result);
        }

        @Test
         void sortAndSearch_returnsSortedAndFilteredList(){
            List<Computer> result = repo.findAll(SortOption.NAME, SortDirection.ASC, "atest");
            assertEquals(List.of(computers.get(1), computers.get(2)), result);
        }
    }

    @Test
     void closedConnection_throwsDataAccessException() throws SQLException {
        connection.close();
        assertThrows(DataAccessException.class, () -> repo.findAll(SortOption.NOTHING, SortDirection.ASC, ""));
    }
}
