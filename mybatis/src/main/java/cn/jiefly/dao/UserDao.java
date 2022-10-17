package cn.jiefly.dao;

import cn.jiefly.pojo.Person;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserDao {
    List<Person> getPersonList();

    int insertPerson(Person person);

    @Select("select * from t_person")
    List<Person> getUsers();
}
