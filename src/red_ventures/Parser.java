/*
 * Aahad Patel
 * Red Ventures Scoring Project 
 * aahadpat@usc.edu
 */
package red_ventures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Parser {
	
	public int score = 0;
	public String id;
	public String filename = System.getProperty("user.dir") + "/data/";
	public String shortened_file_name = "";
	public Connection con;
	
	//constructor
	public Parser() {
		
	}
	//Method "readFileName()" prompts the user for a valid filename and checks if the filename is valid
	public void readFileName() {
		while(true) {
			System.out.print("Please input a valid HTML filename: ");
			Scanner reader = new Scanner(System.in);
			String file = reader.nextLine();
			//Get rid of excess whitespace and check if it is a valid HTML file
			if(file.trim().contains("html") != true) {
				System.out.println("Invalid! Must be a valid html file!");
			}
			else {
				filename += (file);
				shortened_file_name = file;
				System.out.println();
				System.out.println("Parsing: " + file);
				System.out.println();
				break;
			}
		}	
	}
	
	//Method "parse()" parses the html file, assigning a total score to each file
	public int parse() {
		File file = new File(filename);
		//allows reading line by line
		BufferedReader bf;
		try {
			bf = new BufferedReader(new FileReader(file));
			String each_line;
			try {
				each_line = bf.readLine();
				//Parse each line
				while(each_line != null) {
					each_line.toLowerCase();
					if(each_line.contains("<div") || (each_line.contains("<h1"))) {
						score+=3;
					}
					else if(each_line.contains("<p")) {
						score+=1;
					}
					else if(each_line.contains("<h2")) {
						score+=2;
					}
					else if(each_line.contains("<html")) {
						score+=5;
					}
					else if(each_line.contains("<header") || each_line.contains("<footer")) {
						score+=10;
					}
					else if(each_line.contains("<font") || each_line.contains("<strike")) {
						score-=1;
					}
					else if(each_line.contains("<center") || each_line.contains("<big") || each_line.contains("<tt"))  {
						score-=2;
					}
					else if(each_line.contains("<frameset") || each_line.contains("<frame")) {
						score-=5;
					}
					else {
						score+=0;
					}
					each_line = bf.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Score for " + shortened_file_name + ": " + score);
		System.out.println();
		return score;
	}
	//Method "getConnection()" creates the connection between MySQL and the program
	public Connection getConnection() throws Exception {
		try
	    {
	      //In order to create a connection to MySQL, the following are required: driver, url, username, and password
	      String driver = "com.mysql.jdbc.Driver";
	      String url = "jdbc:mysql://localhost:3306/testDB?user=root&useSSL=false";
	      String username = "root";
	      //No password for now
	      String password = "";
	      Class.forName(driver);
	      //create the connection
	      con = DriverManager.getConnection(url, username, password);
	      System.out.print("Connection made! ");
	      return con;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	System.err.println(e.getMessage());
	    }
		return null;
	}
	
	//Method "insertDatabase()" inserts a filename, score pair into the database
	public void insertDatabase() throws Exception {
		try {
			//Use prepared statements!
			PreparedStatement posted = con.prepareStatement("INSERT INTO Scores (file_name, score) VALUES ('"+shortened_file_name+"', '"+score+"')");
			posted.executeUpdate();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		} finally {
			System.out.println("Total score result for " + shortened_file_name + " has been saved in the database! ");
			System.out.println();
		}
	}
	//Method "getScores()" retrieves information from the MySQL database, including all scores and scores based on a unique id (filename)
	public void getScores() throws Exception{
		while(true) {
			//Ask user if he or she would like to retrieve all scores. If user types "no", ask for a unique id
			System.out.print("Would you like to retrieve all scores? If you answer no, you will be prompted to enter a unique id to retrieve scores for. (yes/no): ");
			Scanner scan = new Scanner(System.in);
			String first_option = scan.nextLine().toLowerCase();
			System.out.println();
			if(!first_option.equals("yes") && !first_option.equals("no")) {
				System.out.println("Your answer must be either yes or no! Try again. ");
			}
			else {
				//Display all scores
				if(first_option.equals("yes")) { 
					try {
						PreparedStatement statement = con.prepareStatement("SELECT * FROM Scores");
						ResultSet result = statement.executeQuery();
						System.out.print("Filename \t\tScore");
						while(result.next()) {
							System.out.println();
							System.out.print(result.getString("file_name"));
							System.out.print("\t\t" + result.getString("score"));
						}
						System.out.println();
						return;
					} catch(Exception e) {
						System.out.println(e.getMessage());
						return;
					}
				}
				//Display the score corresponding to the ID that the user enters
				else {
					System.out.print("Type a unique ID to display all given scores for that ID: ");
					Scanner reader = new Scanner(System.in);
					id = reader.nextLine();
					try {
						PreparedStatement statement = con.prepareStatement("SELECT score FROM Scores WHERE file_name = '"+id+"'");
						System.out.println();
						ResultSet result = statement.executeQuery();
						System.out.print("Filename\t\tScore");
						while(result.next()) {
							System.out.println();
							System.out.print(id + "\t\t" + result.getInt("score"));
						}
						System.out.println();
						break;
					} catch(SQLException e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}
	}
	
	//Method "getScoresBetweenDates()" gets scores between two dates that the user inputs
	public void getScoresBetweenDates() {
		System.out.println();
		System.out.print("Returning scores between two dates: ");
		System.out.println();
		System.out.print("Please enter a start-date in the format YYYY-MM-DD: ");
		Scanner read_start = new Scanner(System.in);
		String start_date = read_start.nextLine();
		System.out.println();
		System.out.print("Please enter an end-date in the format YYYY-MM-DD: ");
		Scanner read_end = new Scanner(System.in);
		String end_date = read_end.nextLine();
		
		try {
			PreparedStatement return_scores = con.prepareStatement("SELECT * from Scores WHERE date BETWEEN '"+start_date+"' AND '"+end_date+"'");
			ResultSet result = return_scores.executeQuery();
			System.out.println();
			System.out.println("Displaying scores within " + start_date + " and " + end_date + ": ");
			System.out.println();
			
			System.out.println("Date\t\t\t\tFilename\t\tScore");
			while(result.next()) {
				String date_displayed = result.getString("date");
				String name_displayed = result.getString("file_name");
				int score_displayed = result.getInt("score");
				System.out.println(date_displayed + "\t\t" + name_displayed + "\t\t"+ score_displayed);
			}
			System.out.println();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		} 
	}
	
	//Method "getHighestScore()" gets filename, score pairs with the highest score
	public void getHighestScore() {
		try {
			PreparedStatement statement = con.prepareStatement("SELECT file_name, score FROM Scores WHERE score = (SELECT MAX(score) FROM Scores)");
			ResultSet result = statement.executeQuery();
			System.out.println("Filename\t\tHighest Score");
			while(result.next()) {
				String filename = result.getString("file_name");
				String highest_score = result.getString("score");
				System.out.println(filename + "\t\t" + highest_score);
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	//Method "getLowestScore()" gets filename, score pairs with the lowest score
	public void getLowestScore() {
		try {
			PreparedStatement statement = con.prepareStatement("SELECT file_name, score FROM Scores WHERE score = (SELECT MIN(score) FROM Scores)");
			ResultSet result = statement.executeQuery();
			System.out.println();
			System.out.println("Filename\t\tLowest Score");
			while(result.next()) {
				String filename = result.getString("file_name");
				String lowest_score = result.getString("score");
				System.out.println(filename + "\t\t" + lowest_score);
			}
			System.out.println();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	//Method "getAverageScore()" gets the average scores across each unique id 
	public void getAverageScore() {
		try {
			PreparedStatement statement = con.prepareStatement("SELECT file_name AS unique_id, AVG(score) AS avg_1 FROM Scores GROUP BY file_name");
			ResultSet result = statement.executeQuery();
			System.out.print("Filename\t\tAverage Score ");
			System.out.println();
			while(result.next()) {
				System.out.println(result.getString("unique_id") + "\t\t" + result.getString("avg_1"));
			}
			System.out.println();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	//Main method
	public static void main(String[] args) throws Exception {
		Parser parse = new Parser();
		parse.readFileName();
		parse.parse();
		parse.getConnection();
		parse.insertDatabase();
		parse.getScores();
		parse.getScoresBetweenDates();
		parse.getHighestScore();
		parse.getLowestScore();
		parse.getAverageScore();
		System.out.println("Thank you for using my program! ");
		System.exit(0);
	}
}

