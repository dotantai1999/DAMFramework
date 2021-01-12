package main;

import Repository.ISession;
import Repository.SessionImpl;
import dto.AddressEntity;
import dto.PostEntity;
import dto.UserEntity;

public class main {
    public static void main(String[] args) {
        ISession session = new SessionImpl();

        java.util.Date utilDate = new java.util.Date();
        java.sql.Date now = new java.sql.Date(utilDate.getTime());
        UserEntity userEntity = new UserEntity("SonNT3", "12345", "emailllll@gmail.com", now, new AddressEntity("HP"));

        Integer id = (Integer) session.insert(userEntity);

        PostEntity post1 = new PostEntity("Title 07", userEntity);
        PostEntity post2 = new PostEntity("Title 08", userEntity);

        session.insert(post1);
        session.insert(post2);
    }
}
