import java.io.*;
import java.util.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    // Private variables
    private static Socket requestSocket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static String message;
    private static Scanner scanner = new Scanner(System.in);
    private static Boolean checkStream = true;

// =============================================================================
// The Main Method call for the Client class. This just starts the Client, which 
//   attempts to connect to the Server
// Parameters:
//   args: Needed Parameter for main method call, not used
// =============================================================================

    public static void main(String args[]) {
        Client.run();
    }

// =============================================================================
// A function to set up the Client and continually check for input from the 
//   Server. This is the core of the Client-Server connection
// =============================================================================

    private static void run() {
        try {
            // Create a socket to connect to the server
            requestSocket = new Socket("localhost", 52312);
            System.out.println("Connected to localhost through port 52312");

            // Get Input and Output streams
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());

            // Welcome the user only once
            welcomeUser();

            // Open all default and saved tables
            resume();

            // Show the help menu up front
            showHelp();

            // Constantly check the input stream
            while (checkStream) {
                readMessage();
            }
        }
        catch(UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        }
        catch(IOException ioException) {
            // ioException.printStackTrace();
            System.err.println("Input stream is empty!");
            checkStream = false;
        }
        finally {
            disconnect();
        }
    }

// =============================================================================
// A function to read in a message from the Server via the InputStream and print
//   it out neatly
// =============================================================================

    private static void readMessage() {
        try {
            message = (String)in.readObject();
            // Check if the server has disconnected already
            if (!message.toUpperCase().equals("EXIT;")) {
                System.out.println("------------------------------------------------------------");
                System.out.println(message);
                System.out.println("------------------------------------------------------------\n");
                promptUser();
            }
            else {
                checkStream = false;
            }
        }
        catch(ClassNotFoundException classNot) {
            System.err.println("Data received in unknown format");
        }
        catch(IOException ioException) {
            // ioException.printStackTrace();
            System.err.println("Input stream is empty!");
            checkStream = false;
        }
    }

// =============================================================================
// A function to send a message to the Server via the OutputStream Socket
// Parameters:
//   message: The String to send to the Client
// =============================================================================

    private static void sendMessage(String message) {
        try {
            out.writeObject(message);
            out.flush();
        }
        catch(IOException ioException) {
            ioException.printStackTrace();
        }
    }

// =============================================================================
// A function to disconnect from the Server cleanly
// =============================================================================

    private static void disconnect() {
        // Close the connection
        try {
            System.out.print("Disconnecting from server... ");
            in.close();
            out.close();
            requestSocket.close();
            System.out.println("Connection closed.");
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

// =============================================================================
// A function to print out a welcoming statement on Client startup
// =============================================================================

    private static void welcomeUser() {
        System.out.println("\n------------------------------------------------------------");
        System.out.println("Welcome to the official Aggie Sports Management Client!");
        System.out.println("Type 'help' for a detailed list of commands!");
        System.out.println("------------------------------------------------------------");
    }

// =============================================================================
// A function to create/open all tables from the previous session
// =============================================================================

    private static void resume() {
        // Create global tables that store all sports, teams, and players
        // Note: These only CREATE if they don't already exist as .ser files
        String global_sports = "CREATE TABLE sports (name VARCHAR(15), playing_surface VARCHAR(20), " + 
        "country_created VARCHAR(12)) PRIMARY KEY (name, playing_surface);";
        sendMessage(global_sports);

        String global_teams = "CREATE TABLE teams (name VARCHAR(15), sport VARCHAR(15), wins INTEGER, " + 
        "losses INTEGER, ties INTEGER) PRIMARY KEY (name, wins);";
        sendMessage(global_teams);

        String global_players = "CREATE TABLE players (name VARCHAR(15), age INTEGER, team VARCHAR(15), " + 
        "jersey INTEGER, position VARCHAR(15)) PRIMARY KEY (name, jersey);";
        sendMessage(global_players);

        // Open all saved tables
        String open_all_relations = "OPEN OPEN_ALL_RELATIONS;";
        sendMessage(open_all_relations);
    }

// =============================================================================
// A function to print out the help menu, which lists all available commands
// =============================================================================

    private static void showHelp() {
        System.out.println("\n------------------------------------------------------------");
        System.out.println("PROGRAM COMMANDS:");
        System.out.println("- Help....Show this helpful text menu");
        System.out.println("- Quit.....Exits the program");
        System.out.println();
        System.out.println("ADD COMMANDS:");
        System.out.println("- Add Sport......Add Sport to the database");
        System.out.println("- Add Team.......Add Team to a Sport table");
        System.out.println("- Add Player.....Add a player to a Team table");
        System.out.println();
        System.out.println("UPDATE COMMANDS:");
        System.out.println("- Update Team.......Update a Team's information");
        System.out.println("- Update Player.....Update a Player's information");
        System.out.println();
        System.out.println("REMOVE COMMANDS:");
        System.out.println("- Remove Player.....Remove a player from a team");
        System.out.println();
        System.out.println("VIEW COMMANDS:");
        System.out.println("- View All Sports......Shows all the sports in the database");
        System.out.println("- View All Teams.......Shows all the teams in the database");
        System.out.println("- View All Players.....Shows all the players in the database");
        System.out.println("- View Team............Shows player information for a team");
        System.out.println("- View Team Roster.....Shows the roster (list of player names) for a team");
        System.out.println("- View Player..........Shows information for a player");
        System.out.println();
        System.out.println("MANAGER COMMANDS:");
        System.out.println("- Merge Teams.......Shows a merge between two teams");
        System.out.println("- Split Teams.......Shows a difference between two teams");
        System.out.println("- Join Teams........Shows a join between two teams");
        System.out.println("- Trade Players.....Swap two players from two teams");
        System.out.println("------------------------------------------------------------\n");
    }

// =============================================================================
// A function that will prompt the user for a command, and then enter that 
//   command's function when necessary. If an invalid input is given, it will
//   continue to prompt until 'Quit' is given
// =============================================================================

    private static void promptUser() {
        Boolean checkInputStream = false;

        while (!checkInputStream) {
            System.out.print("User> ");
            String input = scanner.nextLine();

            switch (input.toUpperCase()) {
                // Program commands
                case "HELP":
                    showHelp();
                    break;
                case "QUIT":
                    sendMessage("EXIT;");
                    checkInputStream = true;
                    checkStream = false;
                    break;

                // Add commands
                case "ADD SPORT":
                    addSport();
                    break;
                case "ADD TEAM":
                    addTeam();
                    break;
                case "ADD PLAYER":
                    addPlayer();
                    break;

                // Update commands
                case "UPDATE TEAM":
                    updateTeam();
                    break;
                case "UPDATE PLAYER":
                    updatePlayer();
                    break;

                // Remove commands
                case "REMOVE PLAYER":
                    removePlayer();
                    break;

                // View commands
                case "VIEW ALL SPORTS":
                    sendMessage("SHOW sports;");
                    checkInputStream = true;
                    break;
                case "VIEW ALL TEAMS":
                    sendMessage("SHOW teams;");
                    checkInputStream = true;
                    break;
                case "VIEW ALL PLAYERS":
                    sendMessage("SHOW players;");
                    checkInputStream = true;
                    break;
                case "VIEW TEAM":
                    viewTeam();
                    checkInputStream = true;
                    break;
                case "VIEW TEAM ROSTER":
                    viewTeamRoster();
                    checkInputStream = true;
                    break;
                case "VIEW PLAYER":
                    viewPlayer();
                    checkInputStream = true;
                    break;

                // Manager commands
                case "MERGE TEAMS":
                    mergeTeams();
                    checkInputStream = true;
                    break;
                case "SPLIT TEAMS":
                    splitTeams();
                    checkInputStream = true;
                    break;
                case "JOIN TEAMS":
                    joinTeams();
                    checkInputStream = true;
                    break;
                case "TRADE PLAYERS":
                    tradePlayer();
                    break;

                // Invalid input
                default:
                    System.out.println("Client> Invalid input (type 'Help' to see a list of valid input)");
                    break;
            }
        }
    }

// =============================================================================
// A function to ask the user for a specific value. It will perpetually ask
//   the user for the value until it is given a valid input, or until the user
//   quits the program.
// Parameters:
//   prompt: The String to prompt the user with
// =============================================================================

    private static String getUserInput(String prompt) {
        Boolean finished = false;
        String input = "";

        while (!finished) {
            System.out.print(prompt);
            input = scanner.nextLine();

            switch (input.toUpperCase().trim()) {
                case "HELP":
                    showHelp();
                    break;
                case "QUIT":
                    sendMessage("EXIT;");
                    disconnect();
                    System.exit(0);
                case "":
                    break;
                default:
                    finished = true;
                    break;
            }
        }

        input = input.trim();
        input = input.replaceAll(" ", "_");
        input = input.replaceAll(";", "");
        return input;
    }

// =============================================================================
// A method to generate an SQL command for adding a sport
// =============================================================================

    private static void addSport() {
        // Get the required information from the user
        String name = getUserInput("What is the name of the sport? ");
        String playing_surface = getUserInput("What is " + name + "'s playing surface? (Gym, Field, etc.) ");
        String country = getUserInput("Where did " + name + " originate? ");

       // Generate the SQL command for inserting the team information into the teams table
        String insert_sport = "INSERT INTO sports VALUES FROM (\"" + name + "\", \"" +
        playing_surface + "\", \"" + country + "\");";

        // Send the SQL commands to the Server through sockets
        sendMessage(insert_sport);
    }

// =============================================================================
// A method to generate an SQL command for adding a team
// =============================================================================

    private static void addTeam() {
        // Get the required information from the user
        String name = getUserInput("What is the name of the team? ");
        String sport = getUserInput("Which Sport does " + name + " play? ");
        String wins = getUserInput("How many wins do the " + name + " have? ");
        String losses = getUserInput("How many losses do the " + name + " have? ");
        String ties = getUserInput("How many ties do the " + name + " have? ");

        // Generate the SQL for creating a table for the new team's players
        String create_team = "CREATE TABLE " + name + "(name VARCHAR(15), age INTEGER, " + 
        "jersey INTEGER, position VARCHAR(20)) PRIMARY KEY (name, jersey);"; 
       
       // Generate the SQL command for inserting the team information into the teams table
        String insert_teams = "INSERT INTO teams VALUES FROM (\"" + name + "\", \"" + sport + "\"," + wins + ", " 
        + losses + ", " + ties + ");";

        // Send the SQL commands to the Server through sockets
        sendMessage(create_team);
        sendMessage(insert_teams);
    }


// =============================================================================
// A method to generate an SQL command for adding a player
// =============================================================================

    private static void addPlayer() {
        // Get the required information from the user
        String name = getUserInput("What is the new player's name? ");
        String team = getUserInput("What team does " + name + " play for? ");
        String age = getUserInput("How old is " + name + "? ");
        String jersey = getUserInput("What is " + name + "\'s Jersey Number? ");
        String position = getUserInput("What is " + name + "\'s position? ");

        // Generate the SQL for inserting the player information into the players table
        String player_insert = "INSERT INTO players VALUES FROM (\"" + name + "\", " + 
        age + ", \"" + team + "\", " + jersey + ", \"" + position + "\");"; 

        // Generate the SQL for inserting the players information into the player's team table
        String player_insert_team = "INSERT INTO " + team + " VALUES FROM (\"" + name + "\", " + 
        age + ", " + jersey + ", \"" + position + "\");"; 

        // Send the SQL commands to the Server through sockets
        sendMessage(player_insert);
        sendMessage(player_insert_team);
    }

// =============================================================================
// A method to generate an SQL command for removing for adding a player
// =============================================================================

    private static void removePlayer() {
        // Get the required information from the user
        String name = getUserInput("What is the name of the player to delete? ");
        String team = getUserInput("What team does " + name + " play for? ");
        String jersey = getUserInput("What is " + name + "\'s Jersey Number? ");

        // Generate the SQL for deleting a player from the players table
        String player_remove = "DELETE FROM players " + 
        " WHERE name == \"" + name + "\" && jersey == " + jersey + ";";

        // Generate the SQL for deleting a player from the teams table
        String team_remove = "DELETE FROM " + team +
        " WHERE name == \"" + name + "\" && jersey == " + jersey + ";";

        // Send the SQL commands to the Server through sockets
        sendMessage(player_remove);
        sendMessage(team_remove);
    }

// =============================================================================
// A method to generate an SQL command for updating for adding a team
// =============================================================================

    private static void updateTeam() {
        // Get the required information from the user
        String name = getUserInput("What is the name of the team to update? ");
        String sport = getUserInput("Which Sport does " + name + " play? ");
        String wins = getUserInput("How many wins do the " + name + " have? ");

        // Determine which attributes to update
        String attribute_values = getUserInput("Which values do you want to update? " + 
            "(Pick as many as you like: Name, Sport, Wins, Losses, or Ties) ");
        String new_attribute_values = "";
        // Check if the user wants to update this attribute
        if (attribute_values.toUpperCase().contains("NAME")) {
            // Previous value exists, comma needed
            if (!new_attribute_values.trim().isEmpty()) {
                new_attribute_values += ", ";
            }
            // Append the user inputted value
            new_attribute_values += "name = \"" + getUserInput("What is " + name + "\'s new team name? ") + "\"";
        }
        if (attribute_values.toUpperCase().contains("SPORT")) {
            if (!new_attribute_values.trim().isEmpty()) {
                new_attribute_values += ", ";
            }
            new_attribute_values += "sport = \"" + getUserInput("What sport has " + name + " transferred to? ") + "\"";
        }
        if (attribute_values.toUpperCase().contains("WINS")) {
            if (!new_attribute_values.trim().isEmpty()) {
                new_attribute_values += ", ";
            }
            new_attribute_values += "wins = " + getUserInput("How many wins do the " + name + " have now? ");
        }
        if (attribute_values.toUpperCase().contains("LOSSES")) {
            if (!new_attribute_values.trim().isEmpty()) {
                new_attribute_values += ", ";
            }
            new_attribute_values += "losses = " + getUserInput("How many losses do the " + name + " have now? ");
        }
        if (attribute_values.toUpperCase().contains("TIES")) {
            if (!new_attribute_values.trim().isEmpty()) {
                new_attribute_values += ", ";
            }
            new_attribute_values += "ties = " + getUserInput("How many ties do the " + name + " have now? ");
        }

        // Generate the SQL for updating the team in the teams table
        String update_team = "UPDATE teams SET " + new_attribute_values +
        " WHERE name == \"" + name + "\" && wins == " + wins + ";";

        // Generate the SQL for updating the team in his sports table
        String update_sport = "UPDATE " + sport + " SET " + new_attribute_values +
        " WHERE name == \"" + name + "\" && wins == " + wins + ";";

        // Send the SQL commands to the Server through sockets
        sendMessage(update_team);
        sendMessage(update_sport);
    }

// =============================================================================
// A method to generate an SQL command for updating for adding a player
// =============================================================================

    private static void updatePlayer() {
        // Get the required information from the user
        String name = getUserInput("What is the name of the player to update? ");
        String team = getUserInput("What team does " + name + " play for? ");
        String jersey = getUserInput("What is " + name + "\'s Jersey Number? ");

        // Determine which attributes to update
        String attribute_values = getUserInput("Which values do you want to update? " + 
            "(Pick as many as you like: Name, Age, Team, Jersey, or Position) ");
        String new_attribute_values = "";
        // Check if the user wants to update this attribute
        if (attribute_values.toUpperCase().contains("NAME")) {
            // Previous value exists, comma needed
            if (!new_attribute_values.trim().isEmpty()) {
                new_attribute_values += ", ";
            }
            // Append the user inputted value
            new_attribute_values += "name = \"" + getUserInput("What is " + name + "\'s new name? ") + "\"";
        }
        if (attribute_values.toUpperCase().contains("AGE")) {
            if (!new_attribute_values.trim().isEmpty()) {
                new_attribute_values += ", ";
            }
            new_attribute_values += "age = " + getUserInput("How old is " + name + "\'s now? ");
        }
        if (attribute_values.toUpperCase().contains("TEAM")) {
            if (!new_attribute_values.trim().isEmpty()) {
                new_attribute_values += ", ";
            }
            new_attribute_values += "team = \"" + getUserInput("What is " + name + "\'s new team? ") + "\"";
        }
        if (attribute_values.toUpperCase().contains("JERSEY")) {
            if (!new_attribute_values.trim().isEmpty()) {
                new_attribute_values += ", ";
            }
            new_attribute_values += "jersey = " + getUserInput("What is " + name + "\'s new jersey number? ");
        }
        if (attribute_values.toUpperCase().contains("POSITION")) {
            if (!new_attribute_values.trim().isEmpty()) {
                new_attribute_values += ", ";
            }
            new_attribute_values += "position = \"" + getUserInput("What is " + name + "\'s new position? ") + "\"";
        }

        // Generate the SQL for updating the player in the players table
        String update_players = "UPDATE players SET " + new_attribute_values +
        " WHERE name == \"" + name + "\" && jersey == " + jersey + ";";

        // Generate the SQL for updating the player in his team's table
        String update_team = "UPDATE " + team + " SET " + new_attribute_values +
        " WHERE name == \"" + name + "\" && jersey == " + jersey + ";";

        // Send the SQL commands to the Server through sockets
        sendMessage(update_players);
        sendMessage(update_team);
    }


// =============================================================================
// A method to generate an SQL command for merging for adding two teams
// This is the set union method
// =============================================================================

    private static void mergeTeams() {
        // Get the required information from the user
        String name1 = getUserInput("What is the name of the first team you want to merge? ");
        String name2 = getUserInput("What is the name of the second team you want to merge? ");

        // Generate the SQL for uniting the two teams
        String join_teams = "SHOW " + name1 + " + " + name2 + ";";

        // Send the SQL commands to the Server through sockets
        sendMessage(join_teams);
    }

// =============================================================================
// A method to generate an SQL command for splitting two teams 
// This is the set difference method
// =============================================================================

    private static void splitTeams() {
        // Get the required information from the user
        String name1 = getUserInput("What is the name of the primary team? ");
        String name2 = getUserInput("What is the name of the secondary team -- the one you want to subtract from the primary? ");

        // Generate the SQL for subtracting the two teams
        String join_teams = "SHOW " + name1 + " - " + name2 + ";";

        // Send the SQL commands to the Server through sockets
        sendMessage(join_teams);
    }

// =============================================================================
// A method to generate an SQL command for joining two teams
// This is the natural join method
// =============================================================================

    private static void joinTeams() {
        // Get the required information from the user
        String name1 = getUserInput("What is the name of the first team you want to join? ");
        String name2 = getUserInput("What is the name of the second team you want to join? ");

        // Generate the SQL for joining the two teams
        String join_teams = "SHOW " + name1 + " JOIN " + name2 + ";";

        // Send the SQL commands to the Server through sockets
        sendMessage(join_teams);
    }


// =============================================================================
// A method to generate an SQL command for trading for adding a player
// =============================================================================

    private static void tradePlayer() {
        // Get the required information from the user
        String name1 = getUserInput("What is the name of the first player to trade? ");
        String team1 = getUserInput("What team does " + name1 + " play for? ");
        String jersey1 = getUserInput("What is " + name1 + "\'s Jersey Number? ");
        String name2 = getUserInput("What is the name of the second player to trade? ");
        String team2 = getUserInput("What team does " + name2 + " play for? ");
        String jersey2 = getUserInput("What is " + name2 + "\'s Jersey Number? ");

        // Generate the SQL for inserting player 1 into the team 2's table
        String trade1 = "INSERT INTO " + team2 + " VALUES FROM RELATION select (name==\"" +
        name1 + "\"&&jersey==" + jersey1 + ") " + team1 + ";";

        // Generate the SQL for inserting player 2 into the team 1's table
        String trade2 = "INSERT INTO " + team1 + " VALUES FROM RELATION select (name==\"" + 
        name2 + "\"&&jersey==" + jersey2 + ") " + team2 + ";";

        // Generate the SQL for deleting player 1 from his own table
        String delete1 = "DELETE FROM " + team1 + " WHERE name==\"" + name1 + "\"&& jersey==" +
        jersey1 + ";";

        // Generate the SQL for deleting player 2 from his own table
        String delete2 = "DELETE FROM " + team2 + " WHERE name==\"" + name2 + "\"&& jersey==" +
        jersey2 + ";";

        // Send the SQL commands to the Server through sockets
        sendMessage(trade1);
        sendMessage(trade2);
        sendMessage(delete1);
        sendMessage(delete2);
    }

// =============================================================================
// A method to generate an SQL command for viewing a team
// =============================================================================

    private static void viewTeam() {
        // Get the required information from the user
        String team = getUserInput("What is the name of the team you want to view? ");
        
        // Generate the SQL for showing a team table
        String view_team = "SHOW " + team + ";";

        // Send the SQL commands to the Server through sockets
        sendMessage(view_team);
    }

// =============================================================================
// A method to generate an SQL command for viewing a team's roster (list of 
//   player names)
// This is the projection method
// =============================================================================

    private static void viewTeamRoster() {
        // Get the required information from the user
        String name = getUserInput("What is the name of the team whose roster you want to see? ");

        // Generate the SQL for projecting all player names from a team name
        String view_roster = "SHOW project (name) " + name + ";";

        // Send the SQL commands to the Server through sockets
        sendMessage(view_roster);
    }


// =============================================================================
// A method to generate an SQL command for viewing a player
// This is the selection method
// =============================================================================

    private static void viewPlayer() {
        // Get the required information from the user
        String name = getUserInput("What is the name of the player you want to query? ");
        String jersey_number = getUserInput("What is " + name + "'s jersey number? ");
        
        // Generate the SQL for selecting a player name from the players table
        String view_player = "SHOW select (name == \"" + name + "\" && jersey == " + jersey_number + ") players;";

        // Send the SQL commands to the Server through sockets
        sendMessage(view_player);
    }

}