package database;

import collection.HumanBeing;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.TreeMap;

public class CollectionDatabaseHandler {

    private final Connection connection;

    public CollectionDatabaseHandler(Connection connection) {
        this.connection = connection;
    }

    public void insertRow(HumanBeing humanBeing) throws SQLException {
        String sql = "INSERT INTO DRAGONCOLLECTION (name, coordinate_x, coordinate_y, creation_date, real_hero," +
                " has_toothpick, impact_speed, soundtrack_name, minutes_of_waiting, weapon_type, car, id)" +
                " Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, humanBeing.getName());
        ps.setFloat(2, humanBeing.getCoordinates().getX());
        ps.setLong(3, humanBeing.getCoordinates().getY());
        ps.setDate(4, (Date) humanBeing.getCreationDate());
        ps.setBoolean(5, humanBeing.getRealHero());
        ps.setBoolean(6, humanBeing.getHasToothpick());
        ps.setInt(7, humanBeing.getImpactSpeed());
        ps.setString(8, humanBeing.getSoundtrackName());
        ps.setFloat(9, humanBeing.getMinutesOfWaiting());
        ps.setString(10, humanBeing.getWeaponType().toString());
        ps.setString(11, humanBeing.getCar().toString());
        ps.setInt(12, humanBeing.getId());
        ps.executeUpdate();
        ps.close();
    }
//
//    public void replaceRow(HumanBeing humanBeing) throws SQLException {
//        String sql = "UPDATE DRAGONCOLLECTION SET (name, coordinate_x, coordinate_y, age, color, type, " +
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
        String sql = "DELETE FROM DRAGONCOLLECTION WHERE ID = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        int delRows = ps.executeUpdate();
        if (delRows == 1) {
            System.out.println("Строка была удалена.");
        } else System.out.println("Не было удалено строк.");

    }
//
//    public Integer getOwnedRowByColor(Color color, UserData userData) throws SQLException { //if find - return id, else return null
//        String sql = "SELECT ID FROM DRAGONCOLLECTION WHERE color = ? AND OWNER = ?";
//        PreparedStatement ps = connection.prepareStatement(sql);
//        ps.setString(1, color.name());
//        ps.setString(2, userData.getLogin());
//        ResultSet rs = ps.executeQuery();
//        if (rs.next()) {
//            return rs.getInt(1);
//        }
//        return null;
//    }
//
//    public boolean isAnyRowById(Integer id) throws SQLException {
//        String sql = "SELECT * FROM DRAGONCOLLECTION WHERE ID = ?";
//        PreparedStatement ps = connection.prepareStatement(sql);
//        ps.setInt(1, id);
//        ResultSet rs = ps.executeQuery();
//        return rs.isBeforeFirst();
//    }
//
    public boolean isOwner(Integer id, UserData userData) throws SQLException {
        String sql = "SELECT * FROM DRAGONCOLLECTION WHERE ID = ? AND OWNER = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.setString(2, userData.getLogin());
        ResultSet rs = ps.executeQuery();
        return rs.isBeforeFirst();
    }
//
//    public HumanBeing getHumanBeingById(Integer id) throws SQLException {
//        String sql = "SELECT * FROM DRAGONCOLLECTION WHERE ID = ?";
//
//        PreparedStatement ps = connection.prepareStatement(sql);
//        ps.setInt(1, id);
//        ResultSet rs = ps.executeQuery();
//        if (rs.next()) {
//            return this.createDragonFromCurrentRow(rs);
//        }
//        return null;
//    }
//
//    public void deleteAllOwned(UserData userData) throws SQLException { //Возвращает id всех удаленных элементов
//        String sql = "DELETE FROM DRAGONCOLLECTION WHERE OWNER = ?";
//        PreparedStatement ps = connection.prepareStatement(sql);
//        ps.setString(1, userData.getLogin());
//        int delRows = ps.executeUpdate();
//        System.out.println("Было удалено " + delRows + " строк.");
//    }
//
//    public HumanBeing[] loadInMemory() throws SQLException {
//        TreeMap<Integer, HumanBeing> treeMap = new TreeMap<>();
//        String sql = "SELECT * FROM DRAGONCOLLECTION";
//        Statement statement = connection.createStatement();
//        ResultSet rs = statement.executeQuery(sql);
//        if (rs.isBeforeFirst()) {
//            while (rs.next()) {
//                HumanBeing humanBeing = this.createDragonFromCurrentRow(rs);
//                treeMap.put(humanBeing.getId(), humanBeing); //if (dragon != null)
//            }
//        }
//        return treeMap.values().toArray(new Dragon[0]);
//    }
//
//    private HumanBeing createDragonFromCurrentRow(ResultSet rs) throws SQLException {
//        Integer id = rs.getInt(1);
//        String dragon_name = rs.getString(2);
//        Double coordinate_x = rs.getDouble(3);
//        Integer coordinate_y = rs.getInt(4);
//        ZonedDateTime creationDate = rs.getTimestamp(5).toLocalDateTime().atZone(ZoneId.of("UTC+03:00"));
//        Long age = rs.getLong(6);
//        Color color = Color.valueOf(rs.getString(7));
//        DragonType type = DragonType.valueOf(rs.getString(8));
//        DragonCharacter dragonCharacter = DragonCharacter.valueOf(rs.getString(9));
//        Double depth = rs.getDouble(10);
//        String owner = rs.getString(11);
//
//        return Dragon.createDragon(id, dragon_name, coordinate_x, coordinate_y, age, color, type, dragonCharacter, depth, creationDate, owner);
//    }
//
//    public Integer[] getAllOwner(UserData userData) throws SQLException {
//        String sql = "SELECT * FROM DRAGONCOLLECTION WHERE OWNER = ?";
//        PreparedStatement ps = connection.prepareStatement(sql);
//        ps.setString(1, userData.getLogin());
//        ResultSet rs = ps.executeQuery();
//        ArrayList<Integer> ids = new ArrayList<>();
//        while (rs.next()) {
//            ids.add(rs.getInt(1));
//        }
//        return ids.toArray(new Integer[0]);
//    }
}