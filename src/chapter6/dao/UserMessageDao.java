package chapter6.dao;

import static chapter6.utils.CloseableUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.UserMessage;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class UserMessageDao {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public UserMessageDao() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	// 引数に"id"を追加。nullが扱えるようにラッパークラス(Integer)で型指定。
	public List<UserMessage> select(Connection connection, Integer id, int num, String startDateFilter, String endDateFilter) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			/*
			* 実践問題② -----
			* idがnullだったら全件取得する
			* idがnull以外だったら、その値に対応するユーザーIDの投稿を取得する
			*/
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");
			sql.append("    messages.id as id, ");
			sql.append("    messages.text as text, ");
			sql.append("    messages.user_id as user_id, ");
			sql.append("    users.account as account, ");
			sql.append("    users.name as name, ");
			sql.append("    messages.created_date as created_date ");
			sql.append("FROM messages ");
			sql.append("INNER JOIN users ");
			sql.append("ON messages.user_id = users.id ");
			if (id != null) {
				sql.append("WHERE messages.user_id = ? ");
			} else {
				sql.append("WHERE messages.created_date BETWEEN ? AND ? ");
			}
			sql.append("ORDER BY created_date DESC limit " + num);

			ps = connection.prepareStatement(sql.toString());
			if (id != null) {
				ps.setInt(1, id);
			} else {
				ps.setString(1, startDateFilter);
				ps.setString(2, endDateFilter);
			}

			ResultSet rs = ps.executeQuery();

			List<UserMessage> messages = toUserMessages(rs);
			return messages;

		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);

		} finally {
			close(ps);
		}
	}

	private List<UserMessage> toUserMessages(ResultSet rs) throws SQLException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		List<UserMessage> messages = new ArrayList<UserMessage>();
		try {
			while (rs.next()) {
				UserMessage message = new UserMessage();
				message.setId(rs.getInt("id"));
				message.setText(rs.getString("text"));
				message.setUserId(rs.getInt("user_id"));
				message.setAccount(rs.getString("account"));
				message.setName(rs.getString("name"));
				message.setCreatedDate(rs.getTimestamp("created_date"));

				messages.add(message);
			}
			return messages;
		} finally {
			close(rs);
		}
	}
}
