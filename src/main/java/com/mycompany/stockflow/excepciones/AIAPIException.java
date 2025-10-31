/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.excepciones;


public class AIAPIException extends Exception {
    
    private int statusCode;
    private String errorCode;
    
    public AIAPIException(String message) {
        super(message);
    }
    
    public AIAPIException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public AIAPIException(String message, int statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        if (statusCode > 0) {
            return "AIAPIException: " + getMessage() + 
                   " [Status: " + statusCode + ", Code: " + errorCode + "]";
        }
        return super.toString();
    }
}