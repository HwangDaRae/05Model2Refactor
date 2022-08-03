package com.model2.mvc.view.cart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.cart.impl.CartServiceImpl;
import com.model2.mvc.service.domain.Cart;
import com.model2.mvc.service.domain.User;

public class DeleteCartAction extends Action {
	public DeleteCartAction() {
		System.out.println("[DeleteCartAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[DeleteCartAction execute() start...]");
		Map<String, Object> map = new HashMap<String, Object>();
		
		//1개 or 여러개 삭제시
		String[] delete = request.getParameterValues("deleteCheckBox");
		int[] deleteArray = new int[delete.length];
		for (int i=0; i<deleteArray.length; i++) {
			deleteArray[i] = Integer.parseInt(delete[i]);
			System.out.println("삭제할 상품번호 : " + deleteArray[i]);
		}
		
		//삭제할 상품번호와 user_id를 map에 넣는다
		map.put("deleteArray", deleteArray);
		map.put("user_id", ( (User)request.getSession(true).getAttribute("user") ).getUserId() );

		//장바구니에서 상품을 삭제하고 삭제한 list를 가져온다
		CartService service = new CartServiceImpl();
		service.deleteCart(map);
		List<Cart> cartList = service.getCartList( ( (User)request.getSession(true).getAttribute("user") ).getUserId() );
		int count = service.totalCountCartList( ( (User)request.getSession(true).getAttribute("user") ).getUserId() );
		
		request.setAttribute("list", cartList);
		//count : 게시물 수, listCart.jsp에서 count>0일때 for문으로 list출력
		request.setAttribute("count", count);
		
		System.out.println("[DeleteCartAction execute() end...]");
		return "forward:/cart/listCart.jsp";
	}

}
