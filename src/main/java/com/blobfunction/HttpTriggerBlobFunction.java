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

     @FunctionName("HttpBlobStorageFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws Exception {
    	 	context.getLogger().info("Java HTTP trigger processed a request.");

    	 	String str="";
    	 	try {
    	 		blobActivities(context);		
				context.getLogger().info("blob operation completed");
    	 	} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 	return request.createResponseBuilder(HttpStatus.OK).body("Hello, mani appsrvice"+str).build();
          }
   
           
    private void blobActivities(ExecutionContext context) throws IOException {
        context.getLogger().info("blob operation started");
        File sourceFile = null;
		context.getLogger().info("Azure Blob storage quick start sample");
        String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=storageaccountappse9f37;AccountKey=V7MpL3NoZpHnz26xRIT9wlcARKuMM1E4W+ZSRuNJfOnjGTS47coDRYFs3FxCcc/abyJzYZJZcP7e30gBu3JO0w==";
 		CloudStorageAccount storageAccount;
 		CloudBlobClient blobClient = null;
 		CloudBlobContainer container=null;

       
     	try {
     		CloudFileClient fileClient = FileClientProvider.getFileClientReference();
  	    	CloudFileShare share = fileClient.getShareReference("manishare");	
			storageAccount = CloudStorageAccount.parse(storageConnectionString);

		blobClient = storageAccount.createCloudBlobClient();
		container = blobClient.getContainerReference("quickstartcontainer");

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

		//Creating a sample file
		sourceFile = File.createTempFile("sampleFile", ".txt");
		context.getLogger().info("Creating a sample file at: " + sourceFile.toString());
		Writer output = new BufferedWriter(new FileWriter(sourceFile));
		output.write("Hello Azure!");
		output.close();
		
		try {
		//Getting a blob reference
		CloudBlockBlob blob = container.getBlockBlobReference(sourceFile.getName());

		//Creating blob and uploading file to it
		context.getLogger().info("Uploading the sample file ");

			blob.uploadFromFile(sourceFile.getAbsolutePath());
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
        
}
