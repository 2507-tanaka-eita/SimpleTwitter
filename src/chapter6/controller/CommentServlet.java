package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.CommentService;

@WebServlet(urlPatterns = { "/comment" })
public class CommentServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public CommentServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	// つぶやきの返信
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		HttpSession session = request.getSession();
		List<String> errorMessages = new ArrayList<String>();
		String comment = request.getParameter("comment");
		int messageId = Integer.parseInt(request.getParameter("messageId"));

		if (!isValidComment(comment, errorMessages)) {
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		Message messages = new Message();
		messages.setId(messageId);
		messages.setText(comment);

		User user = (User) session.getAttribute("loginUser");
		messages.setUserId(user.getId());

		new CommentService().insert(messages);
		response.sendRedirect("./");

	}

	// つぶやき返信時のテキスト入力に関するバリデーション
	private boolean isValidComment(String comment, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(comment)) {
			errorMessages.add("メッセージを入力してください");
		} else if (140 < comment.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}