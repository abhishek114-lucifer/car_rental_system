package dao;

import model.Car;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {

    public List<Car> findAll() throws SQLException {
        String sql = "SELECT id, car_name, model, price_per_day, status, image_url "
                + "FROM cars ORDER BY id DESC";
        return find(sql);
    }

    public List<Car> findAvailable() throws SQLException {
        String sql = "SELECT id, car_name, model, price_per_day, status, image_url "
                + "FROM cars WHERE status = 'Available' ORDER BY car_name";
        return find(sql);
    }

    public Car findById(int id) throws SQLException {
        String sql = "SELECT id, car_name, model, price_per_day, status, image_url "
                + "FROM cars WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapCar(result);
                }
            }
        }

        return null;
    }

    public void add(Car car) throws SQLException {
        String sql = "INSERT INTO cars "
                + "(car_name, model, price_per_day, status, image_url) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, car.getCarName());
            statement.setString(2, car.getModel());
            statement.setBigDecimal(3, car.getPricePerDay());
            statement.setString(4, car.getStatus());
            statement.setString(5, car.getImageUrl());
            statement.executeUpdate();
        }
    }

    public void update(Car car) throws SQLException {
        String sql = "UPDATE cars SET car_name = ?, model = ?, price_per_day = ?, "
                + "status = ?, image_url = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, car.getCarName());
            statement.setString(2, car.getModel());
            statement.setBigDecimal(3, car.getPricePerDay());
            statement.setString(4, car.getStatus());
            statement.setString(5, car.getImageUrl());
            statement.setInt(6, car.getId());
            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM cars WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public void setStatus(Connection connection, int carId, String status)
            throws SQLException {

        String sql = "UPDATE cars SET status = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, carId);
            statement.executeUpdate();
        }
    }

    private List<Car> find(String sql) throws SQLException {
        List<Car> cars = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                cars.add(mapCar(result));
            }
        }

        return cars;
    }

    private Car mapCar(ResultSet result) throws SQLException {
        Car car = new Car();
        car.setId(result.getInt("id"));
        car.setCarName(result.getString("car_name"));
        car.setModel(result.getString("model"));
        car.setPricePerDay(result.getBigDecimal("price_per_day"));
        car.setStatus(result.getString("status"));
        car.setImageUrl(result.getString("image_url"));
        return car;
    }
}
