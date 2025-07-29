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
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public EditServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	// つぶやき編集画面の表示
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		String messageIdStr = request.getParameter("messageId");

		HttpSession session = request.getSession();
		List<String> errorMessages = new ArrayList<String>();

		// つぶやき編集画面でのURLパラメータに関するバリデーション
		//   1) IDを削除 or 数字以外に変更
		//   2) 存在しないつぶやきIDに変更
		if ((StringUtils.isNotBlank(messageIdStr) && messageIdStr.matches("^[0-9]+$"))) {
			int messageId =  Integer.parseInt(messageIdStr);
			Message messages = new MessageService().selectEdit(messageId);

			if (messages != null) {
				request.setAttribute("messages", messages);
				request.getRequestDispatcher("edit.jsp").forward(request, response);
			} else {
				errorMessages.add("不正なパラメータが入力されました");
			}
		} else {
			errorMessages.add("不正なパラメータが入力されました");
		}

		if (errorMessages.size() != 0) {
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}
	}

	// つぶやきの編集
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		String messageId = request.getParameter("messageId");
		int messageIntId = Integer.parseInt(messageId);
		String messageText = request.getParameter("text");
		Message messages = new Message();
		messages.setId(messageIntId);
		messages.setText(messageText);

		List<String> errorMessages = new ArrayList<String>();

		if (!isValidMessage(messageText, errorMessages)) {
			request.setAttribute("errorMessages", errorMessages);
			request.setAttribute("messages", messages);
			request.getRequestDispatcher("edit.jsp").forward(request, response);
			return;
		}

		new MessageService().update(messages);

		response.sendRedirect("./");
	}


	// つぶやき編集時のテキスト入力に関するバリデーション
	private boolean isValidMessage(String text, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(text)) {
			errorMessages.add("メッセージを入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}