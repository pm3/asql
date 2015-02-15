package asql;

import java.util.Date;
import java.util.List;

import com.aston.asql.bean.annotation.Sql;
import com.aston.asql.where.ConditionContainer;

public interface TestInterface {

	@Sql("delete from sb_user where login={1}")
	public int delete(String login);

	@Sql("insert into sb_user (login,email,active,admin,last_login) values ({1},{2},{3},{4},{5})")
	public long insert(String login, String email, boolean active, boolean admin, Date lastLogin);

	@Sql("update sb_user set last_login={2} where id={1}")
	public int update(long id, Date lastLogin);

	@Sql("where id={1}")
	public User select1(long id);

	@Sql("bean:load")
	public User select2(long id);

	@Sql("where active={1}")
	public List<User> select3(boolean active);

	@Sql("select id,login from sb_user where active={1}")
	public List<User> select4(boolean active);

	@Sql("select id,login from sb_user where login in({1,in})")
	public List<User> select5(String[] in);

	@Sql("where login in({1,in})")
	public List<User> select6(List<String> in);

	@Sql("where {1}")
	public List<User> select7(ConditionContainer cc);

}
