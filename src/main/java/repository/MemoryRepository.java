package repository;

import domain.Entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MemoryRepository<T extends Entity> extends AbstractRepository<T> {

    @Override
    public void add(T elem) throws RepositoryException {
        if(find(elem.getId()) !=null)
        {
            throw new DuplicateIDException("Element is already in repo " + elem.getId());
        }
        data.add(elem);
    }

    @Override
    public void delete(T elem) throws RepositoryException {
        if(find(elem.getId()) != null)
        {
            data.remove(elem);
        }
        else throw new RepositoryException("Element was not found " + elem.getId());
    }

    @Override
    public void update(T elem, T elem1) throws RepositoryException {
        if (elem == null) {
            throw new IllegalAccessError("Elementul cautat nu exista !");
        } else {
            boolean found = false;
            for (int i = 0; i < this.data.size(); i++) {
                T e = this.data.get(i);
                if (e.getId() == elem.getId()) {
                    this.data.set(i, elem1); // Actualizează elementul găsit
                    found = true;
                    break; // Ieși din buclă după ce ai actualizat
                }
            }

            if (!found) {
                throw new NoSuchElementException("Elementul cu id-ul " + elem.getId() + " nu exista !");
            }
        }
    }


    @Override
    public T find(int id) {
        for(T elem : data)
            if(id == elem.getId()) return elem;
        return null;
    }


    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    @Override
    public Collection<T> getAll() {
        return super.getAll();
    }
}
