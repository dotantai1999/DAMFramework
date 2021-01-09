package Repository;

public interface ISession<T> {
	Object insert(Object object);
	void update(Object object);
	void delete(Object object);
	Object insertOneToOne(Object object);
}
