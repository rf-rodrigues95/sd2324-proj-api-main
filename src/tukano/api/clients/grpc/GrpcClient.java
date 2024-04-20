package tukano.api.clients.grpc;

import static tukano.api.java.Result.error;
import static tukano.api.java.Result.ok;

import java.util.function.Supplier;

import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;

import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;

public class GrpcClient {
	protected static final int MAX_RETRIES = 6;
	protected static final int RETRY_SLEEP = 1000;

	// protected static final int READ_TIMEOUT = 10000;
	protected static final int CONNECT_TIMEOUT = 4000;

	public GrpcClient() {

	}

	static <T> Result<T> toJavaResult(Supplier<T> func) {
		try {
			return ok(func.get());
		} catch (StatusRuntimeException sre) {
			var code = sre.getStatus().getCode();
			if (code == Code.UNAVAILABLE || code == Code.DEADLINE_EXCEEDED)
				throw sre;
			return error(statusToErrorCode(sre.getStatus()));
		}
	}
	
	static ErrorCode statusToErrorCode(Status status) {
		return switch (status.getCode()) {
		case OK -> ErrorCode.OK;
		case NOT_FOUND -> ErrorCode.NOT_FOUND;
		case ALREADY_EXISTS -> ErrorCode.CONFLICT;
		case PERMISSION_DENIED -> ErrorCode.FORBIDDEN;
		case INVALID_ARGUMENT -> ErrorCode.BAD_REQUEST;
		case UNIMPLEMENTED -> ErrorCode.NOT_IMPLEMENTED;
		default -> ErrorCode.INTERNAL_ERROR;
		};
	}

}