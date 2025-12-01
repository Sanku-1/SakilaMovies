package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SakilaMovies {
    public static void main(String[] args) {
    // Check for command line arguments for username and password
        if (args.length != 2) {
        System.out.println("Application needs two arguments to run: " +
                "java com.pluralsight.Main <username> <password>");
        System.exit(1);
    }

    String username = args[0];
    String password = args[1];

    // Create the datasource
    BasicDataSource dataSource = new BasicDataSource();
    // Configure the datasource
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
        dataSource.setUsername(username);
        dataSource.setPassword(password);

    Scanner scanner = new Scanner(System.in);

        while (true) {
        System.out.println("What do you want to do?");
        System.out.println("1) Search for Actors by last name");
        System.out.println("2) Display all customers");
        System.out.println("3) Display all categories");
        System.out.println("0) Exit");
        System.out.print("Select an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                displayActors(dataSource, scanner);
                break;
            case 2:
                displayAllCustomers(dataSource);
                break;
            case 3:
                displayAllCategories(dataSource);
                break;
            case 0:
                System.out.println("Exiting...");
                return;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    }
}

private static void displayAllCategories(BasicDataSource dataSource) {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT CategoryID, CategoryName FROM Categories ORDER BY CategoryID");
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Categories:");
            while (resultSet.next()) {
                int categoryId = resultSet.getInt("CategoryID");
                String categoryName = resultSet.getString("CategoryName");
                System.out.println(categoryId + ": " + categoryName);
            }

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the category ID: ");
            int categoryId = scanner.nextInt();

            displayProductsInCategory(connection, categoryId);

        }
    } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
    }
}

private static void displayProductsInCategory(Connection connection, int categoryId) {
    String query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products WHERE CategoryID = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        preparedStatement.setInt(1, categoryId);
        try (ResultSet productResultSet = preparedStatement.executeQuery()) {
            System.out.println("\nProducts in the selected category:");
            while (productResultSet.next()) {
                int productId = productResultSet.getInt("ProductID");
                String productName = productResultSet.getString("ProductName");
                double unitPrice = productResultSet.getDouble("UnitPrice");
                int unitsInStock = productResultSet.getInt("UnitsInStock");

                System.out.println("Product ID: " + productId);
                System.out.println("Product Name: " + productName);
                System.out.println("Unit Price: " + unitPrice);
                System.out.println("Units In Stock: " + unitsInStock);
                System.out.println("-----------------------------------------");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private static void displayActors(BasicDataSource dataSource, Scanner scanner) {
    System.out.println("Please enter the last name of the actor you wish to search for:");
    String userSearch = scanner.nextLine();

    String query = "SELECT first_name, last_name FROM actor WHERE (actor.last_name LIKE ?)";

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Use try-with-resources for automatic resource management
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, userSearch);
                ResultSet results = statement.executeQuery();
                while (results.next()) {

                    String first_name = results.getString("first_name");
                    String last_name = results.getString("last_name");


                    System.out.println("First Name: " + first_name);
                    System.out.println("Last Name: " + last_name);
                    System.out.println("-----------------------------------------");
                }
            }
        }
    } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
    }
}


private static void displayAllCustomers(BasicDataSource dataSource) {
    String query = "SELECT ContactName, CompanyName, City, Country, Phone FROM Customers ORDER BY Country";

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Use try-with-resources for automatic resource management
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                String contactName = results.getString("ContactName");
                String companyName = results.getString("CompanyName");
                String city = results.getString("City");
                String country = results.getString("Country");
                String phone = results.getString("Phone");

                System.out.println("Contact Name: " + contactName);
                System.out.println("Company Name: " + companyName);
                System.out.println("City: " + city);
                System.out.println("Country: " + country);
                System.out.println("Phone: " + phone);
                System.out.println("-----------------------------------------");
            }
        }
    } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
    }
}
}
