package tukano.api.clients.rest;

import java.util.function.Supplier;

import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;
import jakarta.ws.rs.core.Response.Status;


public class RestClient {
    protected static final int MAX_RETRIES = 7;
    protected static final int RETRY_SLEEP = 1000;
    
    //protected static final int READ_TIMEOUT = 10000;
	protected static final int CONNECT_TIMEOUT = 4000;
	
	protected final ClientConfig config;
	
	
	public RestClient() {
        this.config = new ClientConfig();
        
        config.property("jersey.config.client.connectTimeout", CONNECT_TIMEOUT);
    }

    protected <T> Result<T> reTry(Supplier<Result<T>> func) {
    	for (int i = 0; i < MAX_RETRIES; i++)
    		try {
    			return func.get();
    		} catch (ProcessingException x) {
    			utils.Sleep.ms( RETRY_SLEEP );
    		} catch (Exception x) {
    			x.printStackTrace();
    			return Result.error(ErrorCode.INTERNAL_ERROR);
    		}
    	return Result.error(ErrorCode.TIMEOUT);
    }

    protected <T> Result<T> toJavaResult(Response r, Class<T> entityType) {
    	return toJavaResultAdapter(r, entityType);
    }
    
    protected <T> Result<T> toJavaResult(Response r, GenericType<T> entityType) {
    	return toJavaResultAdapter(r, entityType);
    }
    
    @SuppressWarnings("unchecked")
	private <T> Result<T> toJavaResultAdapter(Response r, Object entityType) {
        try {
            var status = r.getStatusInfo().toEnum();
            if (status == Status.OK && r.hasEntity()) {
            	if (entityType instanceof Class) 
            		return Result.ok(r.readEntity((Class<T>) entityType));
            	else 
                    return Result.ok(r.readEntity( (GenericType<T>) entityType));
            }
            else if (status == Status.NO_CONTENT) 
                return Result.ok(); // Assuming no content is also considered successful
            return Result.error(getErrorCodeFrom(status.getStatusCode()));
        } finally {
            r.close();
        }
    }
    
    public static ErrorCode getErrorCodeFrom(int status) {
		return switch (status) {
		case 200, 209 -> ErrorCode.OK;
		case 409 -> ErrorCode.CONFLICT;
		case 403 -> ErrorCode.FORBIDDEN;
		case 404 -> ErrorCode.NOT_FOUND;
		case 400 -> ErrorCode.BAD_REQUEST;
		case 500 -> ErrorCode.INTERNAL_ERROR;
		case 501 -> ErrorCode.NOT_IMPLEMENTED;
		default -> ErrorCode.INTERNAL_ERROR;
		};
	}
    
}