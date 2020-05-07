package com.dbfunction;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
			HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<Employee>> request,
			final ExecutionContext context,
			@QueueOutput(name = "httpQueue", queueName = "httpRequestQueue", connection = "AzureWebJobsStorage") final OutputBinding<Employee> result) {
		context.getLogger().info("Java HTTP trigger processed a request.");

		String connectionUrl = "jdbc:sqlserver://manisqlserver.database.windows.net:1433;" + "database=manisqldatabase;"
				+ "user=dbuser;" + "password=mani4YOU!@;" + "encrypt=true;" + "trustServerCertificate=true;"
				+ "loginTimeout=30;";
		ResultSet resultSet = null;
		try (Connection connection = DriverManager.getConnection(connectionUrl);) {
			// Code here.
			String selectsql = "select * from employee";
			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery(selectsql);

			// Print results from select statement
			String str="";
			while (resultSet.next()) {
				str=str+resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3)+"\n";
				System.out.println(str);
			}

			return request.createResponseBuilder(HttpStatus.OK).body("Database data , "+str).build();
		}
		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();

			return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Exception " + e.getMessage()).build();
		}

	}

}
