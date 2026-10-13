package sekankodev.pl.repository;

import sekankodev.pl.enums.SortOption;
import sekankodev.pl.exception.DataAccessException;
import sekankodev.pl.model.Computer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComputerRepository {
    private final Connection connection;

    public ComputerRepository(Connection connection) {
        this.connection = connection;
    }

    public void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS computers (
                nazwa VARCHAR(255),
                data_ksiegowania DATE,
                koszt_USD DECIMAL(10,2),
                koszt_PLN DECIMAL(10,2));
                """;

        try (Statement statement = connection.createStatement()){
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("Create table query went wrong!",e);
        }
    }

    public void insert(Iterable<Computer> computers) {
        if (computers == null || !computers.iterator().hasNext()) {
            return;
        }

        String sql = """
                INSERT INTO computers ("nazwa", "data_ksiegowania", "koszt_USD", "koszt_PLN") VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Computer computer : computers) {
                statement.setString(1, computer.getName());
                statement.setDate(2, java.sql.Date.valueOf(computer.getPostDate()));
                statement.setBigDecimal(3, computer.getPricePln());
                statement.setBigDecimal(4, computer.getPriceUsd());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new DataAccessException("Insert query went wrong for computers: " + computers,e);
        }
    }

    public List<Computer> findAll(SortOption sort, String searchQuery) {
        boolean hasSearchQuery = searchQuery != null && !searchQuery.isBlank();

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM computers");

        if (hasSearchQuery) {
            sqlBuilder.append(" WHERE LOWER(nazwa) LIKE LOWER(?)");
        }

        if (sort != SortOption.NOTHING) {
            sqlBuilder.append(" ORDER BY ").append(sort.getColumnName());
        }

        try (PreparedStatement statement = connection.prepareStatement(sqlBuilder.toString())){
            if (hasSearchQuery) {
                statement.setString(1, "%" + searchQuery.trim().toLowerCase() + "%");
            }

            try (ResultSet resultSet = statement.executeQuery()){
                List<Computer> computers = new ArrayList<>();

                while (resultSet.next()){
                    computers.add(
                            new Computer(
                                resultSet.getString("nazwa"),
                                resultSet.getDate("data_ksiegowania").toLocalDate(),
                                resultSet.getBigDecimal("koszt_PLN"),
                                resultSet.getBigDecimal("koszt_USD"))
                    );
                }
                return computers;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Find all query went wrong for: sort -> " + sort.getColumnName() + " and search -> " + searchQuery, e);
        }
    }
}
