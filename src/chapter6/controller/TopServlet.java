package chapter6.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chapter6.beans.User;
import chapter6.beans.UserComment;
import chapter6.beans.UserMessage;
import chapter6.logging.InitApplication;
import chapter6.service.CommentService;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/index.jsp" })
public class TopServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public TopServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		boolean isShowMessageForm = false;
		User user = (User) request.getSession().getAttribute("loginUser");
		if (user != null) {
			isShowMessageForm = true;
		}

		/*
		 * 実践問題② -----
		 * String型のuser_idの値をrequest.getParameter("user_id")で
		 * JSPから受け取るように設定＊
		 * MessageServiceのselectに引数としてString型のuser_idを追加
		 */
		// メッセージ一覧の取得
		// 引数：userId＝アカウントでメッセージを絞り込む用
		// 引数：start, end＝日付でメッセージを絞り込む用
		String userId = request.getParameter("user_id");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		List<UserMessage> messages = new MessageService().select(userId, startDate, endDate);

		// つぶやき返信の表示
		List<UserComment> comments= new CommentService().select();

		request.setAttribute("messages", messages);
		request.setAttribute("comments", comments);
		request.setAttribute("isShowMessageForm", isShowMessageForm);
		request.getRequestDispatcher("/top.jsp").forward(request, response);
	}
}