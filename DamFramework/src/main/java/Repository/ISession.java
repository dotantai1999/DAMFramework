package Repository;

import helper.QueryCreator;
import service.Insertor;

public abstract class ISession<T> {
	protected QueryCreator query = new QueryCreator();
	protected Insertor insertor;

	public ISession() {
	}

	public ISession(Insertor insertor) {
		this.insertor = insertor;
	}

	public QueryCreator getQuery() {
		return query;
	}

	public void setQuery(QueryCreator query) {
		this.query = query;
	}

	public Insertor getInsertor() {
		return insertor;
	}

	public void setInsertor(Insertor insertor) {
		this.insertor = insertor;
	}

	public abstract Object insert(Object object);
	public abstract Object update(Object object);
	public abstract void delete(Object object);
	public abstract Object get(Class zClass, Object id);
//	Object insertOneToOne(Object object);
//	Object insertOneToMany(Object object);
//	Object insertManyToOne(Object object);
}
