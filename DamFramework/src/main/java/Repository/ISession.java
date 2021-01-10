package Repository;

public interface ISession<T> {
	Object insert(Object object);
	Object update(Object object);
	void delete(Object object);
	Object insertOneToOne(Object object);
	Object insertOneToMany(Object object);
	Object get(Class zClass, Object id);
}
