package main;

import Repository.EntityManagerFactoryImpl;
import Repository.IJpaRepository;
import Repository.JpaRepositoryImpl;
import dto.AddressEntity;
import dto.CustomerEntity;
import dto.ProductEntity;
import dto.UserEntity;

//import java.sql.Date;
//import java.util.Date;

public class main {
	public static void main(String[] args) {
		EntityManagerFactoryImpl efi = new EntityManagerFactoryImpl();

		java.util.Date utilDate = new java.util.Date();
		java.sql.Date now = new java.sql.Date(utilDate.getTime());
		UserEntity userEntity = new UserEntity("Sơn", "12345", "nguyenthaison@gmail.com", now, new AddressEntity("TP. HCM"));
		System.out.println("dfsfsdsdfsfd");



		IJpaRepository jpaRepository = new JpaRepositoryImpl();
		jpaRepository.insertOneToOne(userEntity);

	}
}
