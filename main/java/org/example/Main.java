package org.example;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {

        try {
            Connection connection = DatabaseConnection.getConnection();

            if(connection != null){
                System.out.println("SQL Bağlantısı Başarılı ✓");
                connection.close();
            }

        } catch (Exception e){
            System.out.println("SQL Bağlantısı BAŞARISIZ ❌");
            e.printStackTrace();
        }
    }
}




