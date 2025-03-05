package repository;

public class RepositoryException extends Exception{
    public RepositoryException(String msg)
    {
        super(msg);
    }

    public RepositoryException(String msg, Throwable causes)
    {
        super(msg,causes);
    }

}
