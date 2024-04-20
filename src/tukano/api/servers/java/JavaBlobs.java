/**
 * 
 */
package tukano.api.servers.java;

import java.io.FileOutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import tukano.api.clients.factories.ShortsClientFactory;
import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;

import java.util.logging.Logger;


/**
 * 
 */
public class JavaBlobs implements Blobs {

	private static Logger Log = Logger.getLogger(JavaBlobs.class.getName());
	
	@Override
	public Result<Void> upload(String blobId, byte[] bytes) {
		Log.info("Received upload request for blobId: " + blobId);
		
		var client = ShortsClientFactory.getClient();

		var result = client.getShort(blobId);

		
		if (!result.isOK()) {
			Log.warning("Blob Id not valid(does not exist).");
			return Result.error(ErrorCode.FORBIDDEN);
		}

		var existingBlob = download(blobId);
		if(existingBlob.isOK()) {
			byte[] existingBytes = existingBlob.value();
			if(!bytesMatch(bytes, existingBytes)) {
				Log.warning("Bytes do not match");
				return Result.error(ErrorCode.CONFLICT);
			}				
		}
		
		Log.info("DELETE: " + blobId + " || " + bytes.toString());
			
        try {

        	FileOutputStream fos = new FileOutputStream(blobId);
            fos.write(bytes);
            fos.close();
                
            Log.info("Blob uploaded successfully: " + blobId);
            return Result.ok();
        } catch (Exception e) {
            Log.severe("Error uploading blob: " + e.getMessage());
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }
	}

	@Override
	public Result<byte[]> download(String blobId) {
		
		try {
			
			var client = ShortsClientFactory.getClient();

			var result = client.getShort(blobId);
			if (!result.isOK()) {
				Log.warning("No blob exists to download. ");
				return Result.error(ErrorCode.NOT_FOUND);
			}
			
			Path blobFilePath = Paths.get(blobId); 
			if (!Files.exists(blobFilePath)) {	//not needed
				Log.warning("No blob exists to download. ");
	            return Result.error(ErrorCode.NOT_FOUND);
	        }
			
			
	        byte[] bytes = Files.readAllBytes(blobFilePath);
	        
	        return Result.ok(bytes);
		} catch (Exception e) {
			Log.severe("Error download blob: " + e.getMessage());
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}
	}
	
	private boolean bytesMatch(byte[] bytes1, byte[] bytes2) {
	    if (bytes1.length != bytes2.length) {
	        return false;
	    }
	    for (int i = 0; i < bytes1.length; i++) {
	        if (bytes1[i] != bytes2[i]) {
	            return false;
	        }
	    }
	    return true;
	}

}
