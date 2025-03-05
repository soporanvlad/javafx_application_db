package repository;

import domain.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractRepository<T extends Entity> implements Iterable<T>  {

    protected List<T> data = new ArrayList<>();

    public abstract void add(T elem) throws RepositoryException;

    public abstract void delete(T elem) throws RepositoryException;

    public abstract void update(T elem, T elem1) throws RepositoryException;

    public abstract T find(int id);

    public int size()
    {
        return this.data.size();
    }

    public Collection<T> getAll()
    {
        return new ArrayList<>(data);
    }

    public Iterator<T> interator()
    {
        return data.iterator();
    }



}
