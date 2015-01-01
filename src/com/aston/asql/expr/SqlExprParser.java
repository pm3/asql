package com.aston.asql.expr;

public class SqlExprParser {

	public String parse(String sql, IExprParamCreator paramCreator) {

		StringBuilder sb = new StringBuilder(sql.length());
		char[] chars = sql.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char ch = chars[i];
			if (ch == '\'') {
				int end = parseQuote(chars, i);
				if (end == -1)
					throw new IllegalStateException("sql ends inside quote " + sql.substring(i));
				sb.append(sql.subSequence(i, end + 1));
				i = end;
			} else if (ch == '{') {
				int end = parseExpr(sql, chars, i, paramCreator);
				if (end == -1)
					throw new IllegalStateException("sql ends inside expression" + sql.substring(i));
				sb.append('?');
				i = end;
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	protected int parseQuote(char[] chars, int start) {
		for (int i = start + 1; i < chars.length; i++) {
			char ch = chars[i];
			if (ch == '\'') {
				return i;
			}
		}
		return -1;
	}

	protected int parseExpr(String sql, char[] chars, int start, IExprParamCreator paramCreator) {
		int brackets = 0;
		int square = 0;
		int posConverterComma = 0;

		for (int i = start + 1; i < chars.length; i++) {
			char ch = chars[i];

			if (ch == '\'') {
				i = parseQuote(chars, i);
				if (i < 0)
					throw new IllegalStateException("expression ends inside expr quote");
			} else if (ch == '(') {
				brackets++;
			} else if (ch == ')' && brackets > 0) {
				brackets--;
			} else if (ch == '[') {
				square++;
			} else if (ch == ']' && square > 0) {
				square--;
			} else if (ch == ',' && brackets == 0 && square == 0) {
				posConverterComma = i;
			} else if (ch == '}' && brackets == 0 && square == 0) {
				if (start < posConverterComma)
					paramCreator.addParam(sql.substring(start + 1, posConverterComma).trim(), sql.substring(posConverterComma + 1, i).trim());
				else
					paramCreator.addParam(sql.substring(start + 1, i).trim(), null);
				return i;
			}
		}
		return -1;
	}

	protected int parseBrackets(char[] chars, int start) {
		for (int i = start + 1; i < chars.length; i++) {
			char ch = chars[i];
			if (ch == '\'')
				i = parseQuote(chars, i);
			else if (ch == ')')
				return i;
		}
		return -1;
	}

	public static void main(String[] args) {
		print("select * from 1");
		print("select * from t1 where c=''");
		print("select * from t1 where c={2,int}");
		print("select * from t1 where c={2} and ={2} and d={3.asd, dffd} and e={3[1,2].toString(1,2)} and ");
		print("select * from t1 where c in({1},{22}) limit 0");
		print("select * from t1 where c={1} and c='{2}' limit 0");
		print("select * from t1 where c={5.fr()} limit 0");
		print("select * from t1 where c=({6({[{}]})}) limit 0");
		print("select * from t1 where c={8('a')} limit 0");
		print("select * from t1 where c={9('()')} limit 0");
		print("select * from t1 where c=({9('(,'','',123)')}) limit 0");
		print("select * from t1 where and c={2.c['");
		print("select * from t1 where and c={2.c(");
		print("select * from t1 where and c='");
	}

	public static void print(String sql) {
		try {
			System.out.println(sql);
			SqlExprParser p = new SqlExprParser();
			String sql0 = p.parse(sql, new IExprParamCreator() {

				@Override
				public void addParam(String expr, String converter) {
					System.out.println("'" + expr + "' - '" + converter + "'");
				}
			});
			System.out.println(sql0);
		} catch (Exception e) {
			System.out.println(sql + e.getMessage());
		}
		System.out.println();
	}
}
