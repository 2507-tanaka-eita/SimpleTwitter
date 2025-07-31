<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="./css/style.css" rel="stylesheet" type="text/css">
<title>簡易Twitter</title>
</head>
<body>
	<div class="main-contents">
		<div class="header">
			<c:if test="${ empty loginUser }">
				<a href="login">ログイン</a>
				<a href="signup">登録する</a>
			</c:if>
			<c:if test="${ not empty loginUser }">
				<a href="./">ホーム</a>
				<a href="setting">設定</a>
				<a href="logout">ログアウト</a>
			</c:if>
			<c:if test="${ not empty loginUser }">
				<div class="profile">
					<div class="name">
						<h2>
							<c:out value="${loginUser.name}" />
						</h2>
					</div>
					<div class="account">
						@
						<c:out value="${loginUser.account}" />
					</div>
					<div class="description">
						<c:out value="${loginUser.description}" />
					</div>
				</div>
			</c:if>

			<div class="filterDate">
				<form action="./" method="get">
					日付：
					<%-- type="date"のエラーはeclipseの機能上の問題の為、実装においては問題なし --%>
					<input type="date" name="startDate" value="${param.startDate}">
					<input type="date" name="endDate" value="${param.endDate}">
					<input type="submit" value="絞り込み">
				</form>
			</div>
		</div>

		<c:if test="${ not empty errorMessages }">
			<div class="errorMessages">
				<ul>
					<c:forEach items="${errorMessages}" var="errorMessage">
						<li><c:out value="${errorMessage}" />
					</c:forEach>
				</ul>
			</div>
			<c:remove var="errorMessages" scope="session" />
		</c:if>

		<div class="form-area">
			<c:if test="${ isShowMessageForm }">
				<form action="message" method="post">
					いま、どうしてる？<br />
					<textarea name="text" cols="100" rows="5" class="tweet-box"></textarea>
					<br />
					<input type="submit" value="つぶやく">（140文字まで）
				</form>
			</c:if>
		</div>

		<div class="messages">
			<c:forEach items="${messages}" var="message">
				<div class="message" style="margin-bottom: 10px;">
					<div class="account-name">
						<%-- 実践問題② ----- --%>
						<%-- アカウント名で投稿を絞り込むためにaタグ追加、パラメータにuserId付けてリクエスト --%>
						<span class="account">
							<a href="./?user_id=<c:out value="${message.userId}"/> ">
								<c:out value="${message.account}" />
							</a>
						</span>
						<span class="name">
							<c:out value="${message.name}" />
						</span>
					</div>
					<div class="text" style="white-space: pre-wrap;"><c:out value="${message.text}" /></div>
					<div class="date">
						<fmt:formatDate value="${message.createdDate}"
							pattern="yyyy/MM/dd HH:mm:ss" />
					</div>
					<div class="btnWrap" style="display: flex;">
						<%-- つぶやきの編集 ----- --%>
						<c:if test="${loginUser.id == message.userId}">
							<div class="edit">
								<form action="edit" method="get">
									<input type="hidden" name="messageId" value="${message.id}">
									<input type="submit" value="編集">
								</form>
							</div>
						</c:if>
						<%-- つぶやきの削除 ----- --%>
						<c:if test="${loginUser.id == message.userId}">
							<div class="delete" style="margin-left: 5px;">
								<form action="deleteMessage" method="post">
									<input type="hidden" name="messageId" value="${message.id}">
									<input type="submit" value="削除">
								</form>
							</div>
						</c:if>
					</div>
				</div>

				<%-- つぶやきの返信 ----- --%>
				<div class="comment" style="margin-bottom: 20px;">
					<div class="form-area">
						<c:if test="${ isShowMessageForm }">
							<form action="comment" method="post">
								<textarea name="comment" cols="100" rows="5" class="tweet-box"></textarea>
								<br />
								<input type="hidden" name="messageId" value="${message.id}">
								<input type="submit" value="返信">（140文字まで）
							</form>
						</c:if>
					</div>

					<c:forEach items="${comments}" var="comment">
						<c:if test="${message.id == comment.messageId}">
							<div class="account-name">
								<span class="account">
									<c:out value="${comment.account}" />
								</span>
								<span class="name">
									<c:out value="${comment.name}" />
								</span>
							</div>
							<div class="text" style="white-space: pre-wrap;"><c:out value="${comment.text}" /></div>
							<div class="date" style="margin-bottom: 5px;">
								<fmt:formatDate value="${comment.createdDate}"
									pattern="yyyy/MM/dd HH:mm:ss" />
							</div>
						</c:if>
					</c:forEach>
				</div>
			</c:forEach>
		</div>

		<div class="copyright">Copyright(c)EitaTanaka</div>
	</div>
</body>
</html>