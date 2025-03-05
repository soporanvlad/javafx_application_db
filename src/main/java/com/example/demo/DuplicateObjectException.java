package com.example.demo;

import repository.RepositoryException;

public class DuplicateObjectException extends RepositoryException {
    public DuplicateObjectException(String message){
        super(message);
    }
}
