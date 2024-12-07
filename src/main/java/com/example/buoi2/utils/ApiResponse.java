package com.example.buoi2.utils;

public class ApiResponse<T> {
    private String message;
    private int statusCode;
    private T data;

    public ApiResponse(String message, int statusCode, T data) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
    }

    public ApiResponse(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = null;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"message\": \"").append(message).append("\", ");
        json.append("\"statusCode\": ").append(statusCode);
        if (data != null) {
            json.append(", \"data\": ").append("\"").append(data).append("\"");
        }
        json.append("}");
        return json.toString();
    }
}
