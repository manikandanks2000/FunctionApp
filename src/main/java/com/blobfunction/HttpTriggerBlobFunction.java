package com.blobfunction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;
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
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.microsoft.azure.storage.file.CopyStatus;
import com.microsoft.azure.storage.file.FileRange;
import com.microsoft.azure.storage.file.ListFileItem;

public class HttpTriggerBlobFunction {

    public static final String storageConnectionString ="DefaultEndpointsProtocol=http;AccountName=storageaccountappse9f37;AccountKey=V7MpL3NoZpHnz26xRIT9wlcARKuMM1E4W+ZSRuNJfOnjGTS47coDRYFs3FxCcc/abyJzYZJZcP7e30gBu3JO0w==";

      static Logger LOGGER = LoggerFactory.getLogger(HttpTriggerBlobFunction.class);
	    File sourceFile = null,uploadFile=null;
	    String currentfilecontent="",newfilecontent="";
	 	String str="";
     @FunctionName("HttpBlobStorageFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws Exception {
    	 	context.getLogger().info("Java HTTP trigger processed a request.");

    	 	try {
    	 		CloudBlobContainer container=blobActivities(context);		
				context.getLogger().info("blob operation completed");
			  	 sourceFile=createSampleFile(context, "sourceinput.log");
			  	 this.uploadBobFile(container, sourceFile, context);
			  	 uploadFile = new File(sourceFile.getParentFile(),"sourceoutput.log");
			  	 CloudBlockBlob bloboutput = container.getBlockBlobReference(uploadFile.getName());
				currentfilecontent=this.downloadBobFile(container, sourceFile, context);
	 			context.getLogger().info("download the sample file content "+currentfilecontent);
				newfilecontent=StringUtils.replace(currentfilecontent,"Sample","mani");
				context.getLogger().info("Uploading the sample file ");
				Writer output = new BufferedWriter(new FileWriter(uploadFile));
			//	output.write("writing to new file");
	     		output.write(newfilecontent);
	    		output.close();
	    		bloboutput.uploadFromFile(uploadFile.getAbsolutePath());	
	    	
	    		
				context.getLogger().info("Uploading the sample file content"+newfilecontent);

	   			 
    	 	} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 	return request.createResponseBuilder(HttpStatus.OK).body("Azure Blob Service\n"+"CurrentFile Content:"+"\n"+currentfilecontent+"\n"+"NewFile Content:"+"\n"+newfilecontent).build();
       	 
          }
   
           
    private CloudBlobContainer blobActivities(ExecutionContext context) throws IOException {
        context.getLogger().info("blob operation started");
    
		context.getLogger().info("Azure Blob storage quick start sample");
        String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=storageaccountappse9f37;AccountKey=V7MpL3NoZpHnz26xRIT9wlcARKuMM1E4W+ZSRuNJfOnjGTS47coDRYFs3FxCcc/abyJzYZJZcP7e30gBu3JO0w==";
 		CloudStorageAccount storageAccount;
 		CloudBlobClient blobClient = null;
 		CloudBlobContainer container=null;

       
     	try {
  	    	storageAccount = CloudStorageAccount.parse(storageConnectionString);

  	    	blobClient = storageAccount.createCloudBlobClient();
  	    	container = blobClient.getContainerReference("manishare");
  		// Create the container if it does not exist with public access.
		context.getLogger().info("Creating container: " + container.getName());
		container.createIfNotExists(new BlobRequestOptions(), new OperationContext());		 
	    context.getLogger().info("blob container created"+container.getName());
		} catch (InvalidKeyException | URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     	
        /*
         * Create a client that references a to-be-created blob in your Azure Storage account's container.
         * This returns a BlockBlobClient object that wraps the blob's endpoint, credential and a request pipeline
         * (inherited from containerClient). Note that blob names can be mixed case.
         */

     	return container;
		

    }
    
    
	 public String downloadBobFile(CloudBlobContainer container, File sourceFile,ExecutionContext context) {
		 
			String currentfilestr="";			
			
			try {
				//Getting a blob reference
				CloudBlockBlob blobinput = container.getBlockBlobReference(sourceFile.getName());
				File uploadFile = new File(sourceFile.getParentFile(),"sourceoutput.log");
				CloudBlockBlob bloboutput = container.getBlockBlobReference(uploadFile.getName());
				
				//Creating blob and uploading file to it
				

				blobinput.uploadFromFile(sourceFile.getAbsolutePath());
					//String blob.downloadText();

					File downloadedFile = new File(sourceFile.getParentFile(), "sourceinput.log");
					blobinput.downloadToFile(downloadedFile.getAbsolutePath());
					currentfilestr=blobinput.downloadText();
						} catch (StorageException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//Listing contents of container
				for (ListBlobItem blobItem : container.listBlobs()) {
				context.getLogger().info("URI of blob is: " + blobItem.getUri());
				}
 		 return currentfilestr;
 	 }
        
	 
	 public void uploadBobFile(CloudBlobContainer container, File file,ExecutionContext context) {
			try {
				//Getting a blob reference
				
				CloudBlockBlob bloboutput = container.getBlockBlobReference(file.getName());
			
				if(bloboutput.deleteIfExists()) {
					context.getLogger().info("delete existing file if any");
	    		bloboutput.uploadFromFile(file.getAbsolutePath());	
	    		
				}
					context.getLogger().info("Uploading the blob file"+bloboutput.getName());
					
				} catch (StorageException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//Listing contents of container
				for (ListBlobItem blobItem : container.listBlobs()) {
				context.getLogger().info("URI of blob is: " + blobItem.getUri());
				}
		 
	 }

	 
	 
	 
    	public File createSampleFile(ExecutionContext context,String FileName) {
    	    File sourceFile = null;
    		//Creating a sample file
    		try {
    	        sourceFile = new File(FileName);
	
    		context.getLogger().info("Creating a sample file at: " + sourceFile.toString());
    		Writer output = new BufferedWriter(new FileWriter(sourceFile));
    		output.write("Hello Azure! Sample Content\n");
       		output.write("Hello Azure! Sample Content\n");
       		output.write("Hello Azure! Sample Content\n");
    		output.close();
    		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return sourceFile;
    	}
    	
    	
}
