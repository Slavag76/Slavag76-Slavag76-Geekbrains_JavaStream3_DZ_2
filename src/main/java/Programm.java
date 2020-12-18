
import java.sql.*;
import java.util.Scanner;

public class Programm {

    Connection connection;
    Statement stmt;
    Integer randomPrice;
    ResultSet rs;

    public static void main(String... args) throws SQLException {

        Programm programm = new Programm();

        programm.open(); /*создаем подключение к БД*/
        programm.createDatabase(); /*заполняем таблицу*/
        programm.changeGoods(); /*добавляем  новое значение в нижнюю строчку*/
        programm.whatIsPrice(); /*узнаем цену конкретного товара*/
        programm.selectPrice(); /*делаем выборку в задданых значениях (см границы в методе)*/
        programm.deleteData(); /*очищаем таблицу перед новым запуском*/
        programm.close(); /*отключаемся*/
    }

    private void selectPrice() throws SQLException {
        System.out.println("Товары с ценой между 500 и 501 рублей: ");
        String SQLSelectPrice = "SELECT good_id, good_name, good_price FROM goods WHERE good_price BETWEEN 500 AND 501";
        rs = stmt.executeQuery(SQLSelectPrice);
      while (rs.next()) {
          System.out.printf("\n Good_id: %d good_name: %8s good_price: %d", rs.getInt("good_id"), rs.getString("good_name"), rs.getInt("good_price"));
      }
    }

    private void deleteData() throws SQLException {
        String SQLDelete = "DELETE FROM goods";
        stmt.executeUpdate(SQLDelete);
        System.out.println("\nВсе, таблица очищена");
    }

    private void changeGoods() throws SQLException {
        Scanner newgood = new Scanner(System.in);
        System.out.println("Введите название нового товара");
        String newGoodNamme = newgood.nextLine();
        System.out.println("Введите цену этого товара");
        int newGoodPrice = newgood.nextInt();
        String query = "INSERT INTO goods (good_name, good_price) " +
                "VALUES ('" + newGoodNamme + "', '" + newGoodPrice + "')";
        stmt.executeUpdate(query);
        rs = stmt.executeQuery(String.format("SELECT good_id FROM goods WHERE good_name = '%s'", newGoodNamme));
        int newGoodID = rs.getInt("good_id");

        System.out.println("Новый товар добавлен с id " + newGoodID);
    }

    private void whatIsPrice() throws SQLException {
        System.out.println("Введите название товара в формате goodХ, где Х - целое число");
        Scanner scanner = new Scanner(System.in);
        String numberOfGoods = scanner.nextLine();
        rs = stmt.executeQuery(String.format("SELECT good_name, good_price FROM goods WHERE good_name = '%s'", numberOfGoods));

        String goodNameDB = rs.getString("good_name");
        int price = rs.getInt("good_price");
        System.out.println(goodNameDB);

        if (numberOfGoods.equals(goodNameDB)) {
            System.out.println("Цена данного товара: " + price + " рублей");
        } else {
            System.out.println("Такого товара не существует");
        }
    }

    private void createDatabase() throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement("INSERT INTO goods (good_name, good_price) VALUES " + "(?, ?)");
        connection.setAutoCommit(false);
        for (int i = 0; i < 10000; i++) {
            randomPrice = (int) (Math.random() * 1000);
            pstmt.setString(1, "good" + i);
            pstmt.setInt(2, randomPrice);
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        connection.setAutoCommit(true);
        System.out.println("DataBase of goods created");
    }

    void open() {

        try {

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/goods.db");
            stmt = connection.createStatement();
            System.out.println("Connected to database");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    void close() {

        try {
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}