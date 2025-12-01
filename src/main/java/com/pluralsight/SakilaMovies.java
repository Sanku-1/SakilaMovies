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
        System.out.println("2) Search for Movies by Actor");
        System.out.println("0) Exit");
        System.out.print("Select an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                displayActors(dataSource, scanner);
                break;
            case 2:
                actorMovieMatch(dataSource, scanner);
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

                if (results.next()) {
                    System.out.println("Your matches are: \n");
                    // if there are, you are already sitting on the first one so
                    // switch your loop to using a do/while
                    do {
                    // process results
                        String first_name = results.getString("first_name");
                        String last_name = results.getString("last_name");


                        System.out.println("First Name: " + first_name);
                        System.out.println("Last Name: " + last_name);
                        System.out.println("-----------------------------------------");
                    } while (results.next());
                }
                else {
                    System.out.println("No matches!");
                }
            }
        }
    } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
    }
}


    private static void actorMovieMatch(BasicDataSource dataSource, Scanner scanner) {
        System.out.println("Please enter the first name of the actor you wish to search for:");
        String userSearchFirst = scanner.nextLine();
        System.out.println("Please enter the last name of the actor you wish to search for:");
        String userSearchLast = scanner.nextLine();

        String query =  "SELECT film.title, actor.first_name, actor.last_name " +
                        "FROM film_actor " +
                        "LEFT JOIN film " +
                            "ON (film_actor.film_id = film.film_id) " +
                        "LEFT JOIN actor " +
                            "ON (film_actor.actor_id = actor.actor_id) " +
                        "WHERE ((actor.first_name LIKE ?) and (actor.last_name LIKE ?))";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Use try-with-resources for automatic resource management
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, userSearchFirst);
                    statement.setString(2, userSearchLast);
                    ResultSet results = statement.executeQuery();
                    if (results.next()) {
                        System.out.println("Your matches are: \n");
                        // if there are, you are already sitting on the first one so
                        // switch your loop to using a do/while
                        do {
                        // process results
                            String movie_title = results.getString("film.title");
                            System.out.println(movie_title);
                            System.out.println("-----------------------------------------");
                        } while (results.next());
                    }
                    else {
                        System.out.println("No matches!");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
