package kr.order.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.rar.dao.CartDAO;
import kr.rar.dao.ItemDAO;
import kr.rar.vo.CartVO;
import kr.rar.vo.ItemVO;
import kr.controller.Action;

public class OrderFormAction implements Action{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		Integer user_num = (Integer)session.getAttribute("user_num");
		
		if(user_num==null) {//로그인이 되지 않은 경우
			return "redirect:/member/loginForm.do";
		}
		
		//로그인 된 경우
		//POST방식의 접근만 허용
		if(request.getMethod().toUpperCase().equals("GET")) {
			return "/WEB-INF/views/common/notice.jsp";
		}
		CartDAO dao = CartDAO.getInstance();
		
		// 선택된 장바구니 항목 번호들 가져오기
	    String[] selectedCartNums = request.getParameterValues("selectedCartNums");
	    if (selectedCartNums != null) {
	    	dao.resetCartSelected();
	        for (String cart_num : selectedCartNums) {
	            dao.updateCartSelected(Integer.parseInt(cart_num));
	        }
	    }
	    
	    //장바구니 총액
	    int pay_total = dao.getSelectedCartTotal(user_num);
	    if(pay_total <= 0) {//이미 구매가 완료된 경우 재구매 방지 (back버튼 눌러서 돌아간 후 재구매하는 오류 방지)
	    	request.setAttribute("notice_msg", "정상적인 주문이 아니거나 상품이 판매중이 아닙니다.");
	    	request.setAttribute("notice_url", request.getContextPath()+"/item/itemList.do");
	    	return "/WEB-INF/views/common/alert_view.jsp";
	    }
	    
		//장바구니에 담겨있는 상품 정보 호출
		List<CartVO> selectedCartList = dao.getSelectedCartList(user_num);
//		ItemDAO itemDAO = ItemDAO.getInstance();
//		for(CartVO cart : selectedCartList) {
//			ItemVO item = itemDAO.getItem(cart.getItem_num());
//			if(item.getItem_status() == 1) {
//				//상품 판매 완료
//				request.setAttribute("notice_msg", "[" + item.getBookVO().getBk_name() + "]상품 판매 완료");
//				request.setAttribute("notice_url", request.getContextPath() + "/cart/list.do");
//				return "/WEB-INF/views/common/alert_view.jsp";
//			}
//			if(item.getItem_status() == 2) {
//				//상품 판매 중지
//				request.setAttribute("notice_msg", "[" + item.getBookVO().getBk_name() + "]상품 판매 중지");
//				request.setAttribute("notice_url", request.getContextPath() + "/cart/list.do");
//				return "/WEB-INF/views/common/alert_view.jsp";
//			}
//		}
		
		request.setAttribute("list", selectedCartList);
		request.setAttribute("pay_total", pay_total);
		
		return "/WEB-INF/views/order/orderForm.jsp";
	}
}