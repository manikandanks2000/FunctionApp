package com.dbfunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.QueueOutput;

/**
 * Azure Functions with HTTP Trigger.
 */
public class HttpTriggerSQLFunction {
	/**
	 * This function listens at endpoint "/api/HttpExample". Two ways to invoke it
	 * using "curl" command in bash: 1. curl -d "HTTP Body" {your
	 * host}/api/HttpExample 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
	 */
	
	@FunctionName("HttpSQLFunction")
	public HttpResponseMessage run(@HttpTrigger(name = "req", methods = { HttpMethod.GET,
			HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<Customer>> request,
			final ExecutionContext context,
			@QueueOutput(name = "httpQueue", queueName = "httpRequestQueue", connection = "AzureWebJobsStorage") final OutputBinding<Customer> result) {
		context.getLogger().info("Java HTTP trigger DB operations processed in this request.");
		String selectresult="";
		String connectionUrl = "jdbc:sqlserver://manidbserver.database.windows.net:1433;" + "database=manidb;"
				+ "user=azureuser;" + "password=mani4YOU;" + "encrypt=true;" + "trustServerCertificate=true;"
				+ "loginTimeout=30;";
		Connection connection;
		try {
			connection = DriverManager.getConnection(connectionUrl);
	
		insertQuery(connection,1,"fname1","lname1","fname1 lname1","fname1.lname1@email.com");
		selectresult=selectQuery(connection);
		context.getLogger().info("select query"+selectQuery(connection));
		updateQuery(connection, 1, "fname2", "lname2","fname2 lname2","fname2.lname2@email.com");
		context.getLogger().info("select query"+selectQuery(connection));
		deleteQuery(connection,1);
		context.getLogger().info("select query"+selectQuery(connection));	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return request.createResponseBuilder(HttpStatus.OK).body("Database updated"+selectresult).build();
		
	}
	
	 public String  selectQuery(Connection connection) {

			ResultSet resultSet = null;
			String selectsql = "select * from Customer";
			
			String str="";
			try  {

				Statement statement = connection.createStatement();
				resultSet = statement.executeQuery(selectsql);
			while (resultSet.next()) {
					str=str+resultSet.getInt(1) + " " + resultSet.getString(2) +resultSet.getString(3) + " " + resultSet.getString(4)+"\n";
					System.out.println(str);
				}
					return str;
			}
			catch (Exception e) {
				e.printStackTrace();

			}
			return str;

	 }
	 
	 
	 
	 public void insertQuery(Connection conn,int userid,String fname,String lname,String fullname,String emailaddress) {
	 
	 	try {	
		String sql = "insert into Customer(userid,fname, lname, fullname, email) VALUES (?,?, ?, ?, ?)";
		
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setInt(1, userid);
		statement.setString(2, fname);
		statement.setString(3, lname);
		statement.setString(4, fullname);
		statement.setString(5, emailaddress);
		
		int rowsInserted = statement.executeUpdate();
		if (rowsInserted > 0) {
			System.out.println("A new user was inserted successfully!");
		}

		
		} catch (SQLException ex) {
			ex.printStackTrace();
		}		

	}
	 
	 
	 public void deleteQuery(Connection conn,int userid) {
		 
		 	try {	
			String sql = "DELETE FROM Customer WHERE userid=?";
			
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1,userid);
				int rowsDeleted = statement.executeUpdate();
			if (rowsDeleted > 0) {
				System.out.println("A user was deleted successfully!");
			}
			
			} catch (SQLException ex) {
				ex.printStackTrace();
			}		

		}

	 public void updateQuery(Connection conn,int userid,String fname,String lname,String fullname,String email) {
		 
		 	try {	
				String sql = "UPDATE Customer SET fname=?,lname=?, fullname=?, email=? WHERE userid=?";
			
			PreparedStatement statement = conn.prepareStatement(sql);
		
			statement.setString(1, fname);
			statement.setString(2, lname);
			statement.setString(3, fullname);
			statement.setString(4, email);
			statement.setInt(5, userid);
			int rowsUpdated = statement.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.println("An existing user was updated successfully!");
			}

			
			} catch (SQLException ex) {
				ex.printStackTrace();
			}		

		}
		


	 
	
}
