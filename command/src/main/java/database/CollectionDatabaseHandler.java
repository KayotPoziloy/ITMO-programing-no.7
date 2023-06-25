package database;

import collection.*;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.TreeMap;

public class CollectionDatabaseHandler {

    private final Connection connection;

    public CollectionDatabaseHandler(Connection connection) {
        this.connection = connection;
    }

    CollectionManager collectionManager = new CollectionManager();

    public void insertRow(HumanBeing humanBeing) throws SQLException {
        String sql = "INSERT INTO HUMANBEINGCOLLECTION (name, coordinate_x, coordinate_y, creation_date, real_hero," +
                " has_toothpick, impact_speed, soundtrack_name, minutes_of_waiting, weapon_type, car, owner)" +
                " Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, humanBeing.getName());
        ps.setFloat(2, humanBeing.getCoordinates().getX());
        ps.setLong(3, humanBeing.getCoordinates().getY());
        ps.setTimestamp(4, new Timestamp(humanBeing.getCreationDate().getTime()));
        ps.setBoolean(5, humanBeing.getRealHero());
        ps.setBoolean(6, humanBeing.getHasToothpick());
        ps.setInt(7, humanBeing.getImpactSpeed());
        ps.setString(8, humanBeing.getSoundtrackName());
        ps.setFloat(9, humanBeing.getMinutesOfWaiting());
        ps.setString(10, humanBeing.getWeaponType().toString());
        ps.setBoolean(11, humanBeing.getCar().isCool());
        ps.setString(12, humanBeing.getOwner());

        ps.executeUpdate();
        ps.close();
    }
//
//    public void replaceRow(HumanBeing humanBeing) throws SQLException {
//        String sql = "UPDATE HUMANBEINGCOLLECTION SET (name, coordinate_x, coordinate_y, age, color, type, " +
//                "character, cave_depth) = (?, ?, ?, ?, ?, ?, ?, ?) WHERE id = ?";
//        PreparedStatement ps = connection.prepareStatement(sql);
//        ps.setString(1, humanBeing.getName());
//        ps.setDouble(2, humanBeing.getCoordinates().getX());
//        ps.setInt(3, humanBeing.getCoordinates().getY());
//        if (humanBeing.getAge() != null) {
//            ps.setLong(4, humanBeing.getAge());
//        } else ps.setNull(4, Types.NULL);
//        ps.setString(5, humanBeing.getColor().toString());
//        ps.setString(6, humanBeing.getType().toString());
//        ps.setString(7, humanBeing.getCharacter().toString());
//        if (humanBeing.getCave().getDepth() != null) {
//            ps.setDouble(8, humanBeing.getCave().getDepth());
//        } else ps.setNull(8, Types.NULL);
//        ps.setInt(9, humanBeing.getId());
//
//        ps.executeUpdate();
//        ps.close();
//    }
//
    public void deleteRowById(Integer id) throws SQLException {
        String sql = "DELETE FROM HUMANBEINGCOLLECTION WHERE ID = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        int delRows = ps.executeUpdate();
        if (delRows == 1) {
            System.out.println("Строка была удалена.");
        } else System.out.println("Не было удалено строк.");

    }

    public boolean isAnyRowById(Integer id) throws SQLException {
        String sql = "SELECT * FROM HUMANBEINGCOLLECTION WHERE ID = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        return rs.isBeforeFirst();
    }

    public boolean isOwner(Integer id, UserData userData) throws SQLException {
        String sql = "SELECT * FROM HUMANBEINGCOLLECTION WHERE ID = ? AND OWNER = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.setString(2, userData.getLogin());
        ResultSet rs = ps.executeQuery();
        return rs.isBeforeFirst();
    }
//
//    public HumanBeing getHumanBeingById(Integer id) throws SQLException {
//        String sql = "SELECT * FROM HUMANBEINGCOLLECTION WHERE ID = ?";
//
//        PreparedStatement ps = connection.prepareStatement(sql);
//        ps.setInt(1, id);
//        ResultSet rs = ps.executeQuery();
//        if (rs.next()) {
//            return this.createDragonFromCurrentRow(rs);
//        }
//        return null;
//    }

    public void deleteAllOwned(UserData userData) throws SQLException { //Возвращает id всех удаленных элементов
        String sql = "DELETE FROM HUMANBEINGCOLLECTION WHERE OWNER = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, userData.getLogin());
        int delRows = ps.executeUpdate();
        System.out.println("Было удалено " + delRows + " строк.");
    }

    public HumanBeing[] loadInMemory() throws SQLException {
        ArrayDeque<HumanBeing> arrayDeque = new ArrayDeque<>();
        String sql = "SELECT * FROM HUMANBEINGCOLLECTION";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                HumanBeing humanBeing = this.createHumanBeingFromCurrentRow(rs);
                arrayDeque.add(humanBeing); //if (dragon != null)
            }
        }
        return arrayDeque.toArray(new HumanBeing[0]);
    }

    private HumanBeing createHumanBeingFromCurrentRow(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String humanBeingName = rs.getString("name");
        Float coordinate_x = rs.getFloat("coordinate_x");
        Long coordinate_y = rs.getLong("coordinate_y");
        Timestamp creationTimestamp = rs.getTimestamp("creation_date");
        Date creationDate = new Date(creationTimestamp.getTime());
        Boolean realHero = rs.getBoolean("real_hero");
        Boolean hasToothpick = rs.getBoolean("has_toothpick");
        Integer impactSpeed = rs.getInt("impact_speed");
        String soundtrackName = rs.getString("soundtrack_name");
        Float minutesOfWaiting = rs.getFloat("minutes_of_waiting");
        String weaponType = rs.getString("weapon_type");
        Boolean car = rs.getBoolean("car");
        String owner = rs.getString("owner");

        HumanBeing humanBeing = HumanBeing.createHumanBeing(
                id,
                humanBeingName,
                coordinate_x,
                coordinate_y,
                creationDate,
                realHero,
                hasToothpick,
                impactSpeed,
                soundtrackName,
                minutesOfWaiting,
                WeaponType.valueOf(weaponType),
                new Car(car),
                owner);

        return humanBeing;
    }

    public Integer[] getAllOwner(UserData userData) throws SQLException {
        String sql = "SELECT * FROM HUMANBEINGCOLLECTION WHERE OWNER = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, userData.getLogin());
        ResultSet rs = ps.executeQuery();
        ArrayList<Integer> ids = new ArrayList<>();
        while (rs.next()) {
            ids.add(rs.getInt(1));
        }
        return ids.toArray(new Integer[0]);
    }
}