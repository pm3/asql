package asql;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.postgresql.ds.PGSimpleDataSource;

import com.aston.asql.ASqlBuilder;
import com.aston.asql.base.ThreadDataSourceConnection;
import com.aston.asql.where.ConditionContainer;
import com.aston.asql.where.SqlQuery;

public class Test {

	public static void main(String[] args) {
		try {
			main2(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main2(String[] args) throws Exception {

		// MethodParamNameParser.prepareClass(TestInterface.class, null);

		PGSimpleDataSource ds = new PGSimpleDataSource();
		ds.setServerName("localhost");
		ds.setDatabaseName("superb");
		ds.setUser("aston");
		ds.setPassword("aston");

		ASqlBuilder b = new ASqlBuilder();
		b.setConnectionProvider(new ThreadDataSourceConnection(ds));

		TestInterface i = b.createMapper(TestInterface.class);
		i.delete("pm2");

		long id = i.insert("pm2", "pm@aston.sk", true, false, new Date());
		i.update(id, new Date());
		User u1 = i.select1(id);
		System.out.println(u1);
		User u2 = i.select1(id);
		System.out.println("select 1 " + u2);
		List<User> l3 = i.select3(true);
		for (User u3 : l3)
			System.out.println("select 3 " + u3);
		List<User> l4 = i.select4(true);
		for (User u4 : l4)
			System.out.println("select 4 " + u4);

		List<User> l5 = i.select5(new String[] { "a", "b" });
		System.out.println(l5.size());
		List<User> l6 = i.select6(Arrays.asList("a", "b", "pm2"));
		System.out.println(l6.size());

		ConditionContainer cc = ConditionContainer.and();
		cc.eq("login", "pm2");
		cc.eq("active", true);

		SqlQuery q1 = new SqlQuery("select x.id from sb_user x where {where}");
		q1.where().eq("x.active", true);
		cc.subquery("id", "in", q1);

		List<User> l7 = i.select7(cc);
		System.out.println(l7.size());

	}
}
