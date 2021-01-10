package main;

import Repository.DBConnectionImpl;
import Repository.ISession;
import Repository.SessionImpl;
import dto.AddressEntity;
import dto.UserEntity;
import dto.UserModel;

//import java.sql.Date;
//import java.util.Date;

public class main {
	public static void main(String[] args) {

		java.util.Date utilDate = new java.util.Date();
		java.sql.Date now = new java.sql.Date(utilDate.getTime());
		UserEntity userEntity = new UserEntity( null, "12345", "fafaffaafa@gmail.com", now, new AddressEntity("SG"));
		UserEntity userEntity2 = new UserEntity("LA3", "12345", "123456@gmail.com", now, new AddressEntity("DN"));
		System.out.println("running . . .");


		ISession session = new SessionImpl();
//
//		UserEntity obj = (UserEntity) session.get(UserEntity.class, 32);
//
//		System.out.println("returnnnn: " + obj.toString());
		Long id = (Long) session.insertOneToOne(userEntity2);

		System.out.println("returnnnnn: " + id);



//		userEntity2.setUserId(32);
//		session.update(userEntity2);
	}
}
