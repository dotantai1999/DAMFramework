package main;

import Repository.IJpaRepository;
import Repository.JpaRepositoryImpl;
import dto.CustomerEntity;
import dto.ProductEntity;

public class main {
	public static void main(String[] args) {
		CustomerEntity customerEntity = new CustomerEntity("Tài Tài Tài");
		System.out.println("dfsfsdsdfsfd");
		ProductEntity productEntity = new ProductEntity("Tài", 3000);
		productEntity.setProduct_id(10);
		
		
		IJpaRepository jpaRepository = new JpaRepositoryImpl();
		jpaRepository.delete(productEntity);


	}
}
