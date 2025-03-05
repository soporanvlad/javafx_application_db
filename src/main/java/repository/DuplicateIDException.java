package repository;

public class DuplicateIDException extends RepositoryException {
    public DuplicateIDException(String s) {
        super(s);
    }
}
