package com.blobfunction;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
public class HttpTriggerFileShareFunction {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    public static final String storageConnectionString ="DefaultEndpointsProtocol=http;AccountName=storageaccountappse9f37;AccountKey=V7MpL3NoZpHnz26xRIT9wlcARKuMM1E4W+ZSRuNJfOnjGTS47coDRYFs3FxCcc/abyJzYZJZcP7e30gBu3JO0w==";


    	static Logger LOGGER = LoggerFactory.getLogger(HttpTriggerFileShareFunction.class);

     @FunctionName("HttpFileShareExample")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws Exception {
    	 	context.getLogger().info("Java HTTP trigger processed a request.");
// java copy files 
// java find replace files 
// construct log file in specific path with timestamp filename defined
// call java program with class path defined  to call internally
// repackage the jars as maven dependencies 
// delete files older than 60 days     	 	
// call java from this java using Runtime.getRuntime() 
// control operation like if , else to loop thru variables used and then added to the script parameter for use

    	 	// String str=  runScript3("test-script.sh","");
    	 	String str="";
    	 	try {
               //  new FileSample().FileShareExample();
    	 		//	blobActivities();
				str=this.downloadFilefromShare(context);
				context.getLogger().info("Download Completed");
						
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 	return request.createResponseBuilder(HttpStatus.OK).body("Hello, mani appsrvice"+str).build();
          }


    
    private String downloadFilefromShare(ExecutionContext context) {     
        
    	String str="";
    	String newstr="";
    			
		try {
			CloudFileClient fileClient = FileClientProvider.getFileClientReference();
	    	CloudFileShare share = fileClient.getShareReference("manishare");
	    	CloudFileDirectory rootDir = share.getRootDirectoryReference();
	    	CloudFile file1 = rootDir.getFileReference("scriptinput.log");
	    	CloudFile file2 = rootDir.getFileReference("scriptoutput.log");
	    	file2.create(1024L);
	    	file2.openWriteExisting();
	    	System.out.println(file1.downloadText());
	    	context.getLogger().info("File share,files availability confirmed");
	    	str= file1.downloadText();
	    	newstr=StringUtils.replace(str,"sample","newsampledata");    	
			InputStream in = new ByteArrayInputStream(newstr.getBytes());
			file2.upload(in, newstr.length());   	
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
		return str+newstr;

    }
         
    private void blobActivities() throws IOException {
        String accountName="storageaccountappse9f37";
        /*
         * From the Azure portal, get your Storage account's name and account key.
         */
   

        /*
         * Use your Storage account's name and key to create a credential object; this is used to access your account.
         */
        StorageSharedKeyCredential credential = new StorageSharedKeyCredential("storageaccountappse9f37", "V7MpL3NoZpHnz26xRIT9wlcARKuMM1E4W+ZSRuNJfOnjGTS47coDRYFs3FxCcc/abyJzYZJZcP7e30gBu3JO0w==");

        /*
         * From the Azure portal, get your Storage account blob service URL endpoint.
         * The URL typically looks like this:
         */
        String endpoint = String.format(Locale.ROOT, "https://storageaccountappse9f37.blob.core.windows.net/", accountName);

        /*
         * Create a BlobServiceClient object that wraps the service endpoint, credential and a request pipeline.
         */
        BlobServiceClient storageClient = new BlobServiceClientBuilder().endpoint(endpoint).credential(credential).buildClient();

        /*
         * This example shows several common operations just to get you started.
         */

        /*
         * Create a client that references a to-be-created container in your Azure Storage account. This returns a
         * ContainerClient object that wraps the container's endpoint, credential and a request pipeline (inherited from storageClient).
         * Note that container names require lowercase.
         */
        BlobContainerClient blobContainerClient = storageClient.getBlobContainerClient("input-files-new" + System.currentTimeMillis());

        /*
         * Create a container in Storage blob account.
         */
        blobContainerClient.create();

        /*
         * Create a client that references a to-be-created blob in your Azure Storage account's container.
         * This returns a BlockBlobClient object that wraps the blob's endpoint, credential and a request pipeline
         * (inherited from containerClient). Note that blob names can be mixed case.
         */
        BlockBlobClient blobClient = blobContainerClient.getBlobClient("HelloWorld.txt").getBlockBlobClient();

        String data = "Hello world!";
        InputStream dataStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

        /*
         * Create the blob with string (plain text) content.
         */
        blobClient.upload(dataStream, data.length());

        dataStream.close();

        /*
         * Download the blob's content to output stream.
         */
        int dataSize = (int) blobClient.getProperties().getBlobSize();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(dataSize);
        blobClient.download(outputStream);
        outputStream.close();

        /*
         * Verify that the blob data round-tripped correctly.
         */
        if (!data.equals(new String(outputStream.toByteArray(), StandardCharsets.UTF_8))) {
            throw new RuntimeException("The downloaded data does not match the uploaded data.");
        }

        /*
         * Create more blobs before listing.
         */
        for (int i = 0; i < 3; i++) {
            String sampleData = "Samples";
            InputStream dataInBlobs = new ByteArrayInputStream(sampleData.getBytes(Charset.defaultCharset()));
            blobContainerClient.getBlobClient("myblobsforlisting" + System.currentTimeMillis()).getBlockBlobClient()
                .upload(dataInBlobs, sampleData.length());
            dataInBlobs.close();
        }

        /*
         * List the blob(s) in our container.
         */
        blobContainerClient.listBlobs()
            .forEach(blobItem -> System.out.println("Blob name: " + blobItem.getName() + ", Snapshot: " + blobItem.getSnapshot()));

    }

    public void runFileShareSamples() throws Exception,IOException {

        System.out.println("Azure Storage File sample - Starting.");

        CloudFileClient fileClient = null;
        CloudFileShare fileShare1 = null;
        CloudFileShare fileShare2 = null;
        FileInputStream fileInputStream = null;

        try {

            // Create a file client for interacting with the file service
            fileClient = FileClientProvider.getFileClientReference();

            // Create sample file for upload demonstration
            Random random = new Random();
            System.out.println("\nCreating sample file between 128KB-256KB in size for upload demonstration.");
            File tempFile1 = DataGenerator.createTempLocalFile("file-", ".tmp", (128 * 1024) + random.nextInt(256 * 1024));
            System.out.println(String.format("\tSuccessfully created the file \"%s\".", tempFile1.getAbsolutePath()));

            // Create file share with randomized name
            System.out.println("\nCreate file share for the sample demonstration");
            fileShare1 = createFileShare(fileClient,"manifileshare");
         //   fileShare1 = createFileShare(fileClient, DataGenerator.createRandomName("filebasics-"));
            System.out.println(String.format("\tSuccessfully created the file share \"%s\".", fileShare1.getName()));

            // Get a reference to the root directory of the share.
            CloudFileDirectory rootDir1 = fileShare1.getRootDirectoryReference();

            // Upload a local file to the root directory
            System.out.println("\nUpload the sample file to the root directory.");
            CloudFile file1 = rootDir1.getFileReference(tempFile1.getName());
            file1.uploadFromFile(tempFile1.getAbsolutePath());
            System.out.println("\tSuccessfully uploaded the file.");

            // Create a random directory under the root directory
            System.out.println("\nCreate a random directory under the root directory");
            CloudFileDirectory dir = rootDir1.getDirectoryReference(DataGenerator.createRandomName("dir-"));
            if (dir.createIfNotExists()) {
                System.out.println(String.format("\tSuccessfully created the directory \"%s\".", dir.getName()));
            }
            else {
                throw new IllegalStateException(String.format("Directory with name \"%s\" already exists.", dir.getName()));
            }

            // Upload a local file to the newly created directory sparsely (Only upload certain ranges of the file)
            System.out.println("\nUpload the sample file to the newly created directory partially in distinct ranges.");
            CloudFile file1sparse = dir.getFileReference(tempFile1.getName());
            file1sparse.create(tempFile1.length());
            fileInputStream = new FileInputStream(tempFile1);
            System.out.println("\t\tRange start: 0, length: 1024.");
            file1sparse.uploadRange(fileInputStream, 0, 1024);
            System.out.println("\t\tRange start: 4096, length: 1536.");
            fileInputStream.getChannel().position(4096);
            file1sparse.uploadRange(fileInputStream, 4096, 1536);
            System.out.println("\t\tRange start: 8192, length: EOF.");
            fileInputStream.getChannel().position(8192);
            file1sparse.uploadRange(fileInputStream, 8192, tempFile1.length() - 8192);
            fileInputStream.close();
            System.out.println("\tSuccessfully uploaded the file sparsely.");

            // Query the file ranges
            System.out.println(String.format("\nQuery the file ranges for \"%s\".", file1sparse.getUri().toURL()));
            ArrayList<FileRange> fileRanges = file1sparse.downloadFileRanges();
            for (Iterator<FileRange> itr = fileRanges.iterator(); itr.hasNext(); ) {
                FileRange fileRange = itr.next();
                System.out.println(String.format("\tStart offset: %d, End offset: %d", fileRange.getStartOffset(), fileRange.getEndOffset()));
            }

            // Clear a range and re-query the file ranges
            System.out.println(String.format("\nClearing the second range partially and then re-query the file ranges for \"%s\".", file1sparse.getUri().toURL()));
            file1sparse.clearRange(4608, 512);
            fileRanges = file1sparse.downloadFileRanges();
            for (Iterator<FileRange> itr = fileRanges.iterator(); itr.hasNext(); ) {
                FileRange fileRange = itr.next();
                System.out.println(String.format("\tStart offset: %d, End offset: %d", fileRange.getStartOffset(), fileRange.getEndOffset()));
            }

            // Create another file share with randomized name
            System.out.println("\nCreate another file share for the sample demonstration");
            fileShare2 = createFileShare(fileClient, DataGenerator.createRandomName("filebasics-"));
            System.out.println(String.format("\tSuccessfully created the file share \"%s\".", fileShare2.getName()));

            // Get a reference to the root directory of the share.
            CloudFileDirectory rootDir2 = fileShare2.getRootDirectoryReference();

            // Create sample file for copy demonstration
            System.out.println("\nCreating sample file between 10MB-15MB in size for copy demonstration.");
            File tempFile2 = DataGenerator.createTempLocalFile("file-", ".tmp", (10 * 1024 * 1024) + random.nextInt(5 * 1024 * 1024));
            System.out.println(String.format("\tSuccessfully created the file \"%s\".", tempFile2.getAbsolutePath()));

            // Upload a local file to the root directory
            System.out.println("\nUpload the sample file to the root directory.");
            CloudFile file2 = rootDir1.getFileReference(tempFile2.getName());
            file2.uploadFromFile(tempFile2.getAbsolutePath());
            System.out.println("\tSuccessfully uploaded the file.");

            // Copy the file between shares
            System.out.println(String.format("\nCopying file \"%s\" from share \"%s\" into the share \"%s\".", file2.getName(), fileShare1.getName(), fileShare2.getName()));
            CloudFile file2copy = rootDir2.getFileReference(file2.getName() + "-copy");
            file2copy.startCopy(file2);
            waitForCopyToComplete(file2copy);
            System.out.println("\tSuccessfully copied the file.");

            // Abort copying the file between shares
            System.out.println(String.format("\nAbort when copying file \"%s\" from share \"%s\" into the share \"%s\".", file2.getName(), fileShare1.getName(), fileShare2.getName()));
            System.out.println(String.format("\nAbort when copying file from the root directory \"%s\" into the directory we created \"%s\".", file2.getUri().toURL(), dir.getUri().toURL()));
            CloudFile file2copyaborted = rootDir2.getFileReference(file2.getName() + "-copyaborted");
            boolean copyAborted = true;
            String copyId = file2copyaborted.startCopy(file2);
            try {
                file2copyaborted.abortCopy(copyId);
            }
            catch (StorageException ex) {
                if (ex.getErrorCode().equals("NoPendingCopyOperation")) {
                    copyAborted = false;
                } else {
                    throw ex;
                }
            }
            if (copyAborted == true) {
                System.out.println("\tSuccessfully aborted copying the file.");
            } else {
                System.out.println("\tFailed to abort copying the file because the copy finished before we could abort.");
            }

            // List all file shares and files/directories in each share
            System.out.println("\nList all file shares and files/directories in each share.");
            enumerateFileSharesAndContents(fileClient);

            // Download the uploaded files
            System.out.println("\nDownload the uploaded files.");
            String downloadedFilePath = String.format("%s%s", System.getProperty("java.io.tmpdir"), file1.getName());
            System.out.println(String.format("\tDownload the fully uploaded file from \"%s\" to \"%s\".", file1.getUri().toURL(), downloadedFilePath));
            file1.downloadToFile(downloadedFilePath);
            new File(downloadedFilePath).deleteOnExit();
            downloadedFilePath = String.format("%s%s", System.getProperty("java.io.tmpdir"), file1sparse.getName());
            System.out.println(String.format("\tDownload the sparsely uploaded file from \"%s\" to \"%s\".", file1sparse.getUri().toURL(), downloadedFilePath));
            file1sparse.downloadToFile(downloadedFilePath);
            new File(downloadedFilePath).deleteOnExit();
            downloadedFilePath = String.format("%s%s", System.getProperty("java.io.tmpdir"), file2.getName());
            System.out.println(String.format("\tDownload the copied file from \"%s\" to \"%s\".", file2.getUri().toURL(), downloadedFilePath));
            file2.downloadToFile(downloadedFilePath);
            new File(downloadedFilePath).deleteOnExit();
            downloadedFilePath = String.format("%s%s", System.getProperty("java.io.tmpdir"), file2copy.getName());
            System.out.println(String.format("\tDownload the copied file from \"%s\" to \"%s\".", file2copy.getUri().toURL(), downloadedFilePath));
            file2copy.downloadToFile(downloadedFilePath);
            new File(downloadedFilePath).deleteOnExit();
            downloadedFilePath = String.format("%s%s", System.getProperty("java.io.tmpdir"), file2copyaborted.getName());
            System.out.println(String.format("\tDownload the copied file from \"%s\" to \"%s\".", file2copyaborted.getUri().toURL(), downloadedFilePath));
            file2copyaborted.downloadToFile(downloadedFilePath);
            new File(downloadedFilePath).deleteOnExit();
            System.out.println("\tSuccessfully downloaded the files.");

            // Delete the files and directory
            System.out.print("\nDelete the filess and directory. Press any key to continue...");

            file1.delete();
            file1sparse.delete();
            file2.delete();
            file2copy.delete();
            file2copyaborted.delete();
            System.out.println("\tSuccessfully deleted the files.");
            dir.delete();
            System.out.println("\tSuccessfully deleted the directory.");
        }
        catch (Throwable t) {
            PrintHelper.printException(t);
        }
        finally {
            // Delete any file shares that we created (If you do not want to delete the file share comment the line of code below)
            System.out.print("\nDelete any file shares we created.");

            if (fileShare1 != null && fileShare1.deleteIfExists() == true) {
                System.out.println(String.format("\tSuccessfully deleted the file share: %s", fileShare1.getName()));
            }

            if (fileShare2 != null && fileShare2.deleteIfExists() == true) {
                System.out.println(String.format("\tSuccessfully deleted the file share: %s", fileShare2.getName()));
            }

            // Close the file input stream of the local temporary file
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }

        System.out.println("\nAzure Storage File sample - Completed.\n");
    }

    /**
     * Creates and returns a file share for the sample application to use.
     *
     * @param fileShareName Name of the file share to create
     * @return The newly created CloudFileShare object
     *
     * @throws StorageException
     * @throws RuntimeException
     * @throws IOException
     * @throws URISyntaxException
     * @throws IllegalArgumentException
     * @throws InvalidKeyException
     * @throws IllegalStateException
     */
    private static CloudFileShare createFileShare(CloudFileClient fileClient, String fileShareName) throws StorageException, RuntimeException, IOException, InvalidKeyException, IllegalArgumentException, URISyntaxException, IllegalStateException {

        // Create a new file share
        CloudFileShare fileShare = fileClient.getShareReference(fileShareName);
        try {
            if (fileShare.createIfNotExists() == false) {
                throw new IllegalStateException(String.format("File share with name \"%s\" already exists.", fileShareName));
            }
        }
        catch (StorageException s) {
            if (s.getCause() instanceof java.net.ConnectException) {
                System.out.println("Caught connection exception from the client. If running with the default configuration please make sure you have started the storage emulator.");
            }
            throw s;
        }

        return fileShare;
    }

    /**
     * Enumerates the contents of the file share.
     *
     * @param rootDir Root directory which needs to be enumerated
     *
     * @throws StorageException
     */
    private static void enumerateDirectoryContents(CloudFileDirectory rootDir) throws StorageException {

        Iterable<ListFileItem> results = rootDir.listFilesAndDirectories();
        for (Iterator<ListFileItem> itr = results.iterator(); itr.hasNext(); ) {
            ListFileItem item = itr.next();
            boolean isDirectory = item.getClass() == CloudFileDirectory.class;
            System.out.println(String.format("\t\t%s: %s", isDirectory ? "Directory " : "File      ", item.getUri().toString()));
            if (isDirectory == true) {
            	enumerateDirectoryContents((CloudFileDirectory) item);
            }
        }
    }

    /**
     * Enumerates the shares and contents of the file shares.
     *
     * @param fileClient CloudFileClient object
     *
     * @throws StorageException
     * @throws URISyntaxException
     */
    private static void enumerateFileSharesAndContents(CloudFileClient fileClient) throws StorageException, URISyntaxException {

        for (CloudFileShare share : fileClient.listShares("filebasics")) {
            System.out.println(String.format("\tFile Share: %s", share.getName()));
            enumerateDirectoryContents(share.getRootDirectoryReference());
        }
    }

    /**
     * Wait until the copy complete.
     *
     * @param file Target of the copy operation
     *
     * @throws InterruptedException
     * @throws StorageException
     */
    private static void waitForCopyToComplete(CloudFile file) throws InterruptedException, StorageException {
        CopyStatus copyStatus = CopyStatus.PENDING;
        while (copyStatus == CopyStatus.PENDING) {
            Thread.sleep(1000);
            copyStatus = file.getCopyState().getStatus();
        }
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
