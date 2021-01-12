package main;

import Repository.DBConnectionImpl;
import Repository.ISession;
import Repository.SessionImpl;
import com.sun.jndi.cosnaming.IiopUrl;
import dto.AddressEntity;
import dto.PostEntity;
import dto.UserEntity;
import dto.UserModel;

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
		UserEntity userEntity = new UserEntity( "Sonnnn", "12345", "emailllll@gmail.com", now, new AddressEntity("SG"));

		Integer id = (Integer) session.insertOneToOne(userEntity);

		List<PostEntity> listPosts = new LinkedList<>();
		PostEntity post1 = new PostEntity("Title 01", userEntity);
		PostEntity post2 = new PostEntity("Title 02", userEntity);

		session.insertManyToOne(post1);
		session.insertManyToOne(post2);

		System.out.println("post1: " + post1);
		System.out.println("post2: " + post2);
	}
}
