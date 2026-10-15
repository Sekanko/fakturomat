package pl.sekankodev.repository;

import pl.sekankodev.enums.SortDirection;
import pl.sekankodev.enums.SortOption;
import pl.sekankodev.exception.DataAccessException;
import pl.sekankodev.model.Computer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComputerRepository {
    private final Connection connection;

    public ComputerRepository(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "Connection cannot be null");
    }

    public void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS computers (
                nazwa            VARCHAR(255)   NOT NULL,
                data_ksiegowania DATE           NOT NULL,
                koszt_USD        DECIMAL(10,2)  NOT NULL,
                koszt_PLN        DECIMAL(10,2)  NOT NULL);
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

        try {
            connection.setAutoCommit(false);
            proceedBatch(computers, sql);
            connection.commit();
        } catch (SQLException e){
            rollbackQuietly(e);
            throw new DataAccessException("Transaction failed", e);
        } finally {
            restoreAutoCommit();
        }
    }


    public List<Computer> findAll(SortOption sort, SortDirection direction, String searchQuery) {
        boolean hasSearchQuery = searchQuery != null && !searchQuery.isBlank();

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM computers");
        if (hasSearchQuery)             sqlBuilder.append(" WHERE LOWER(nazwa) LIKE LOWER(?)");
        if (sort != SortOption.NOTHING) sqlBuilder.append(" ORDER BY ").append(sort.getColumnName());
        if (direction != SortDirection.ASC) sqlBuilder.append(" DESC");

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

    private void proceedBatch(Iterable<Computer> computers, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            for (Computer computer : computers) {
                    if (computer == null) throw new IllegalArgumentException("Computer cannot be null");

                    statement.setString(1, computer.name());
                    statement.setDate(2, java.sql.Date.valueOf(computer.postDate()));
                    statement.setBigDecimal(3, computer.priceUsd());
                    statement.setBigDecimal(4, computer.pricePln());
                    statement.addBatch();
                }
                statement.executeBatch();
        }
    }

    private void rollbackQuietly(Exception e) {
        try {
            connection.rollback();
        } catch (SQLException ex) {
            e.addSuppressed(ex);
        }
    }

    private void restoreAutoCommit(){
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DataAccessException("Could not restore auto-commit", e);
        }
    }
}
