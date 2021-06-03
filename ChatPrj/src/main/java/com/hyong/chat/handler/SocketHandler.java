package com.hyong.chat.handler;

import java.util.HashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SocketHandler extends TextWebSocketHandler{
		// TextWebSocketHandler는 handleTextMessage를 실행시킴. 메시지 타입따라
		// handleTextMessage 또는 handleBinaryMessage가 실행됨.
		
		//웹 소켓 세션을 담아둘 맵
		//각 유저들을 담아내는 그릇
		HashMap<String, WebSocketSession> sessionMap = new HashMap<>(); 

		// 메세지 발송. 메세지 수신하면 실행
		// 메세지를 발송하면 웹소켓에 String이 보내진다. 
		@Override
		protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
			
			//받은 메세지를 String에 넣는다. getPayLoad를 통해 message에 포함된 text를 가져올 수 있음.
			String msg = message.getPayload();
			//key는 sessionMap의 String부분. 즉 각 유저별 반복문
			for(String key : sessionMap.keySet()) {
				// WebSocketSession객체 생성인데...참여한 유저 중 한명씩
				WebSocketSession wss = sessionMap.get(key);
				try {
					// 각 유저한테 sendMessage를 하는데...그것이 msg로 만든 TextMessage객체
					wss.sendMessage(new TextMessage(msg));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 즉, 한 유저가 메세지를 발송하면 이 SocketHandler가 세션과 메세지를 받고, sessionMap에 있는(현재참여하고 있는) 유저들한테
		// 반복문을 통해 sendMessage를 하게 된다는 것. sendMessage를 하게 되면 클라이언트들은 onmessage 메소드를 불러옴.
		// 클라이언트의 send가 handleTextMessage를 호출하고, sendMessage가 클라이언트의 onmessage를 호출
		
		
		
		//웹소켓이 열리면 맨 처음 실행되는 메소드
		//WebSocket 객체가 생성되고 그것을 서버에 보내면 이놈이 받아내는 듯
		//WebSocket 객체가 곧 session이 되고 WebSocketSession인터페이스의 getId()메소드로 Id고유값 얻어서 sessionMap에 저장
		//이게 곧 한 유저의 세션이 되는 것
		@Override
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			// 소켓 연결. 웹소켓 연결이 되면 동작
			super.afterConnectionEstablished(session);
			sessionMap.put(session.getId(), session);
		}
		
		@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
			// 소켓 종료. 웹소켓이 종료되면 동작
			sessionMap.remove(session.getId());
			super.afterConnectionClosed(session, status);
		}
		
}
