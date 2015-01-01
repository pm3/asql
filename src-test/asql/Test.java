package asql;

import java.util.Date;
import java.util.List;

import org.postgresql.ds.PGSimpleDataSource;

import com.aston.asql.ASqlBuilder;
import com.aston.asql.base.ThreadDataSourceConnection;

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
		List<User> l = i.select3(true);
		for (User u3 : l)
			System.out.println("select 3 " + u3);
		List<User> l2 = i.select4(true);
		for (User u3 : l2)
			System.out.println("select 4 " + u3);
	}
}
