package main;

import Repository.DBConnectionImpl;
import Repository.ISession;
import Repository.SessionImpl;
import com.sun.jndi.cosnaming.IiopUrl;
import dto.AddressEntity;
import dto.PostEntity;
import dto.UserEntity;
import dto.UserModel;
import serviceImpl.InsertManyToOne;
import serviceImpl.InsertOneToOne;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

//import java.sql.Date;
//import java.util.Date;

public class main {
	public static void main(String[] args) {
		ISession session = new SessionImpl();

		java.util.Date utilDate = new java.util.Date();
		java.sql.Date now = new java.sql.Date(utilDate.getTime());
		UserEntity userEntity = new UserEntity( "SonNT", "12345", "emailllll@gmail.com", now, new AddressEntity("HP"));

		session.setInsertor(new InsertOneToOne());

		Integer id = (Integer) session.insert(userEntity);

		List<PostEntity> listPosts = new LinkedList<>();
		PostEntity post1 = new PostEntity("Title 05", userEntity);
		PostEntity post2 = new PostEntity("Title 06", userEntity);

		session.setInsertor(new InsertManyToOne());
		session.insert(post1);
		session.insert(post2);

		System.out.println("post1: " + post1);
		System.out.println("post2: " + post2);
	}
}
