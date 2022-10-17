package cn.jiefly.dao;

import cn.jiefly.pojo.Person;
import cn.jiefly.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;

public class UserDaoTest {
    private static final Logger logger = Logger.getLogger(UserDaoTest.class);
    @Test
    public void testSelect(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        List<Person> personList = userDao.getUsers();
        logger.error("selectlalala" + personList.toString());
        sqlSession.close();
        Calendar.getInstance()
    }

    @Test
    public void testInsert(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        int i = userDao.insertPerson(new Person(123123, "ins", "M"));
        System.out.println(i);
        sqlSession.close();
    }
}
