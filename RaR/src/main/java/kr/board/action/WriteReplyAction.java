package kr.board.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;

import kr.rar.dao.BoardDAO;
import kr.rar.vo.BoardReplyVO;
import kr.controller.Action;

public class WriteReplyAction implements Action {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> mapAjax = 
				new HashMap<String,String>();
		
		HttpSession session = request.getSession();
		Integer user_num = (Integer)session.getAttribute("user_num");
		
		if(user_num == null) {//로그인이 안된 경우
			mapAjax.put("result", "logout");
		}else {	//로그인이 된 경우
			//전송된 데이터 인코딩 타입 지정
			request.setCharacterEncoding("utf-8");
			//자바빈(VO)을 생성해서 전송된 데이터 저장
			BoardReplyVO reply = new BoardReplyVO();
			reply.setUser_num(user_num);//댓글 작성자 회원번호
			reply.setContent(request.getParameter("content"));
			reply.setUser_ip(request.getRemoteAddr());
			reply.setBoard_num(Integer.parseInt(
					request.getParameter("board_num")));//댓글의 부모 글 번호
		
			BoardDAO dao = BoardDAO.getInstance();
			dao.insertReplyBoard(reply);
			
			mapAjax.put("result", "success");
			
		}
		
		//JSON 데이터 변환
		ObjectMapper mapper = new ObjectMapper();
		String ajaxData = mapper.writeValueAsString(mapAjax);
		
		request.setAttribute("ajaxData", ajaxData);
		
		return "/WEB-INF/views/common/ajax_view.jsp";
	}

}
