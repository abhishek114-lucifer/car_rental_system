package dao;

import model.Booking;
import util.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public void create(Booking booking) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                checkCarAvailable(connection, booking.getCarId());
                insertBooking(connection, booking);
                new CarDAO().setStatus(connection, booking.getCarId(), "Booked");
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public List<Booking> findByUser(int userId) throws SQLException {
        return find("WHERE b.user_id = ?", userId);
    }

    public List<Booking> findAll() throws SQLException {
        return find("", 0);
    }

    public void returnBooking(int bookingId, int userId) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                int carId = getCarId(connection, bookingId, userId);
                updateBookingStatus(connection, bookingId);
                new CarDAO().setStatus(connection, carId, "Available");
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    private void checkCarAvailable(Connection connection, int carId)
            throws SQLException {

        String sql = "SELECT status FROM cars WHERE id = ? FOR UPDATE";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, carId);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next() || !"Available".equals(result.getString("status"))) {
                    throw new SQLException("This car is already booked or does not exist.");
                }
            }
        }
    }

    private void insertBooking(Connection connection, Booking booking)
            throws SQLException {

        String sql = "INSERT INTO bookings "
                + "(user_id, car_id, rent_date, return_date, total_amount, status) "
                + "VALUES (?, ?, ?, ?, ?, 'Booked')";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, booking.getUserId());
            statement.setInt(2, booking.getCarId());
            statement.setDate(3, booking.getRentDate());
            statement.setDate(4, booking.getReturnDate());
            statement.setBigDecimal(5, booking.getTotalAmount());
            statement.executeUpdate();
        }
    }

    private int getCarId(Connection connection, int bookingId, int userId)
            throws SQLException {

        String sql = "SELECT car_id FROM bookings "
                + "WHERE id = ? AND user_id = ? AND status = 'Booked'";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, userId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("car_id");
                }
            }
        }

        throw new SQLException("Active booking not found.");
    }

    private void updateBookingStatus(Connection connection, int bookingId)
            throws SQLException {

        String sql = "UPDATE bookings SET status = 'Returned' WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.executeUpdate();
        }
    }

    private List<Booking> find(String filter, int userId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT b.*, u.name AS user_name, c.car_name "
                + "FROM bookings b "
                + "JOIN users u ON b.user_id = u.id "
                + "JOIN cars c ON b.car_id = c.id "
                + filter + " ORDER BY b.id DESC";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            if (userId > 0) {
                statement.setInt(1, userId);
            }

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Booking booking = new Booking();
                    booking.setId(result.getInt("id"));
                    booking.setUserId(result.getInt("user_id"));
                    booking.setCarId(result.getInt("car_id"));
                    booking.setUserName(result.getString("user_name"));
                    booking.setCarName(result.getString("car_name"));
                    booking.setRentDate(result.getDate("rent_date"));
                    booking.setReturnDate(result.getDate("return_date"));
                    booking.setTotalAmount(result.getBigDecimal("total_amount"));
                    booking.setStatus(result.getString("status"));
                    bookings.add(booking);
                }
            }
        }

        return bookings;
    }
}
