package com.blobfunction;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.function.util.DataGenerator;
import com.function.util.PrintHelper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.microsoft.azure.storage.file.CopyStatus;
import com.microsoft.azure.storage.file.FileRange;
import com.microsoft.azure.storage.file.ListFileItem;
/**
 * Azure Functions with HTTP Trigger.
 */
public class HttpTriggerJavaBatch {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
   static Logger LOGGER = LoggerFactory.getLogger(HttpTriggerJavaBatch.class);

    @FunctionName("HttpJavaBatchFileShareExample")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws Exception {
    	 	context.getLogger().info("Java HTTP trigger processed a request.");
    	 	String name="";
    	 	String currentfilestr="";
    	 	String newfilecontent="";
    	 	String currentfile="scriptinput.log";
    	 	String newfile="scriptoutput.log";
    	 	String fileshare="manishare";
    	 	
    
    	 	
    	 	String dbConnection=System.getenv("DBConnection");
    	 	context.getLogger().info("DBConnection conection string"+dbConnection);
     	   	
    	 	String connectionUrl1=System.getenv("DatabaseConnectionString");
    	 	context.getLogger().info("db conection string"+connectionUrl1);
    	 	
 			String connectionUrl = "jdbc:sqlserver://manisqlserver.database.windows.net:1433;" + "database=manisqldatabase;"
 					+ "user=dbuser;" + "password=mani4YOU!@;" + "encrypt=true;" + "trustServerCertificate=true;"
 				+ "loginTimeout=30;";
 			
 			
    	 	try {
    	 		
    	 	    context.getLogger().info("GET parameters are: " + request.getQueryParameters());
    	 	    name = request.getQueryParameters().getOrDefault("name", "");
     	 	   if (name.isEmpty()) {
   	 	        return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
   	 	                        .body("input parameters name and value not found.")
   	 	                        .build();
     	 	   } 
     	 	   
     	 	   
     	 	   	// Data from database uploade to file share in CSV format 
     	 	   
     			Connection connection;
     			String firstname="";
     			String queryresultset="";
     			try {
     				connection = DriverManager.getConnection(connectionUrl);
      				firstname=selectQuery(connection,1);
      				queryresultset=this.selectQueryResults(connection);
     			context.getLogger().info("select query result"+firstname);
     			} catch (SQLException e) {
     				// TODO Auto-generated catch block
     				context.getLogger().info("Java batch failed");	
     				e.printStackTrace();
     			}
     			uploadFileShare(context,fileshare,currentfile,queryresultset);
     	 
     			
     			
     			
     	 	   	currentfilestr=this.downloadFileShare(context,fileshare,currentfile,newfile);
     	 	   	context.getLogger().info("current file content"+currentfilestr);
     	    	newfilecontent=StringUtils.replace(currentfilestr,"zcxxz",firstname);    
     	    	  context.getLogger().info("new file content"+newfilecontent);
    	    	uploadFileShare(context,fileshare,newfile,newfilecontent);
    			context.getLogger().info("File content replaced and uploaded in the new file");

				context.getLogger().info("Download Completed");
				//this.runScript3(".","echo hello");
				context.getLogger().info("Java batch completed successfully");		
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				context.getLogger().info("Java batch failed");		
				
			}
    	 	return request.createResponseBuilder(HttpStatus.OK).body("Azure FileShare service\n"+"CurrentFile Content:"+"\n"+currentfilestr+"\n"+"NewFile Content based on DB query and Java processing the data :"+"\n"+newfilecontent).build();
          }


    
    private String downloadFileShare(ExecutionContext context,String fileshare,String currentfile,String newfile) {     
    	String currentstr="";
  		try {
			CloudFileClient fileClient = FileClientProvider.getFileClientReference();
	 	//	context.getLogger().info("properties info"+LoadConfigProperties.getConfigProperties("fileshare"));
	 		
	 	    			
	    	CloudFileShare share = fileClient.getShareReference(fileshare);
	    	CloudFileDirectory rootDir = share.getRootDirectoryReference();
	    	CloudFile file1 = rootDir.getFileReference(currentfile);
	    	CloudFile file2 = rootDir.getFileReference(newfile);
	    	file2.create(1024L);
	    	file2.openWriteExisting();
	    	System.out.println(file1.downloadText());
	    	context.getLogger().info("File share,files availability confirmed");
	    	currentstr= file1.downloadText();
		    	
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currentstr;
    }
    

    private void uploadFileShare(ExecutionContext context,String fileshare,String newfile,String newfilecontent) {     
  		try {
			CloudFileClient fileClient = FileClientProvider.getFileClientReference();
	    	CloudFileShare share = fileClient.getShareReference(fileshare);
	    	CloudFileDirectory rootDir = share.getRootDirectoryReference();
	    	CloudFile file2 = rootDir.getFileReference(newfile);
			InputStream in = new ByteArrayInputStream(newfilecontent.getBytes());
			file2.upload(in, newfilecontent.length());   	
			context.getLogger().info("File content replaced and uploaded in the new file");
	    	
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

    }

	 public String  selectQuery(Connection connection,int userid) {

			ResultSet resultSet = null;
			String selectsql = "select fname from Customer where user_id="+""+userid+"";
			
			String str="";
			try  {

				Statement statement = connection.createStatement();
				resultSet = statement.executeQuery(selectsql);
			while (resultSet.next()) {
					str= resultSet.getString(1);
					System.out.println(str);
				}
					return str;
			}
			catch (Exception e) {
				e.printStackTrace();

			}
			return str;

	 }
	 
	 public String  selectQueryResults(Connection connection) {

			ResultSet resultSet = null;
			String selectsql = "select * from Customer";
			
			String str="";
			try  {

				Statement statement = connection.createStatement();
				resultSet = statement.executeQuery(selectsql);
			while (resultSet.next()) {
					str=str+resultSet.getInt(1) + "," + resultSet.getString(2) + ","+resultSet.getString(3) +  "," + resultSet.getString(4)+"\n";
					System.out.println(str);
				}
					return str;
			}
			catch (Exception e) {
				e.printStackTrace();

			}
			return str;

	 }
	 
    
          public  String runScript3(String path, String... args) {
            String str="";
            try {
            
                String[] cmd = new String[args.length + 1];
                cmd[0] = path;
                int count = 0;
                for (String s : args) {
                    cmd[++count] = args[count - 1];
                }
                Process process = Runtime.getRuntime().exec(cmd);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                try {
                    process.waitFor();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                while (bufferedReader.ready()) {
                    str=str+bufferedReader.readLine();
               
                    System.out.println("Received from script: " + str);
                         
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.exit(1);
            }
            return str;
        }
     
        
}
