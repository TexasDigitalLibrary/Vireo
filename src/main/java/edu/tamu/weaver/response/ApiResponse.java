package edu.tamu.weaver.response;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonView;

// Overriding Weaver-Webservice-Core ApiResponse for additional meta stacktrace property

/**
 * Abstract class for an API response.
 *
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public class ApiResponse {

    @JsonView(ApiView.Partial.class)
    private final Meta meta;

    @JsonView(ApiView.Partial.class)
    private HashMap<String, Object> payload;

    private ApiResponse() {
        meta = new Meta();
        payload = new HashMap<String, Object>();
    }

    public ApiResponse(ApiStatus status) {
        this();
        this.meta.setStatus(status);
        this.meta.setMessage(status.getMessage());
    }

    public ApiResponse(ApiStatus status, Object... payload) {
        this();
        this.meta.setStatus(status);
        this.meta.setMessage(status.getMessage());
        for (Object obj : payload) {
            String objectType = obj.getClass().getSimpleName();
            if (objectType.equals("ArrayList")) {
                ArrayList<?> a = ((ArrayList<?>) obj);
                if (a.size() > 0)
                    objectType += "<" + a.get(0).getClass().getSimpleName() + ">";
            }
            this.payload.put(objectType, obj);
        }
    }

    // Vireo customization
    public ApiResponse(ApiStatus status, String message, String stacktrace) {
        this(status);
        this.meta.setMessage(message);
        this.meta.setStacktrace(stacktrace);
    }

    public ApiResponse(ApiStatus status, String message, Object... payload) {
        this(status, payload);
        this.meta.setMessage(message);
    }

    public ApiResponse(String id, ApiStatus status, String message, Object... payload) {
        this(status, payload);
        this.meta.setId(id);
        this.meta.setMessage(message);
    }

    public ApiResponse(String id, ApiStatus status, Object... payload) {
        this(status, payload);
        this.meta.setId(id);
    }

    public ApiResponse(ApiStatus status, ApiAction action) {
        this(status);
        this.meta.setAction(action);
    }

    public ApiResponse(ApiStatus status, ApiAction action, Object... payload) {
        this(status, payload);
        this.meta.setAction(action);
    }

    public ApiResponse(ApiStatus status, ApiAction action, String message, Object... payload) {
        this(status, action, payload);
        this.meta.setMessage(message);
    }

    public ApiResponse(String id, ApiStatus status, ApiAction action, String message, Object... payload) {
        this(status, action, payload);
        this.meta.setId(id);
        this.meta.setMessage(message);
    }

    public ApiResponse(String id, ApiStatus status, ApiAction action, Object... payload) {
        this(status, action, payload);
        this.meta.setId(id);
    }

    /**
     * Gets meta.
     *
     * @return Meta
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Gets payload.
     *
     * @return HashMap<String, Object>
     */
    public HashMap<String, Object> getPayload() {
        return payload;
    }

    /**
     * Sets payload.
     *
     * @param payload
     *            HashMap<String, Object>
     */
    public void setPayload(HashMap<String, Object> payload) {
        this.payload = payload;
    }

    /**
     * Inner class Meta
     */
    public class Meta {

        @JsonView(ApiView.Partial.class)
        private ApiStatus status;

        @JsonView(ApiView.Partial.class)
        private ApiAction action;

        @JsonView(ApiView.Partial.class)
        private String message;

        // Vireo customization
        @JsonView(ApiView.Partial.class)
        private String stacktrace;

        @JsonView(ApiView.Partial.class)
        private String id;

        public Meta() {

        }

        public ApiStatus getStatus() {
            return status;
        }

        public void setStatus(ApiStatus status) {
            this.status = status;
        }

        public ApiAction getAction() {
            return action;
        }

        public void setAction(ApiAction action) {
            this.action = action;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        // Vireo customization
        public String getStacktrace() {
            return stacktrace;
        }

        // Vireo customization
        public void setStacktrace(String stacktrace) {
            this.stacktrace = stacktrace;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

}
