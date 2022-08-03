package com.model2.mvc.view.cart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.cart.impl.CartServiceImpl;
import com.model2.mvc.service.domain.Cart;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

public class AddCartAction extends Action {
	public AddCartAction() {
		System.out.println("[AddCartAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[AddCartAction execute() start...]");
		Cart cart = new Cart();
		
		ProductService p_service = new ProductServiceImpl();
		Product product = p_service.getProduct( Integer.parseInt(request.getParameter("prod_no")) );

		// getProduct.jsp에서 장바구니 버튼 클릭시 장바구니에 추가된다.
		cart.setProd_no(Integer.parseInt(request.getParameter("prod_no")));
		cart.setUser_id( ( (User)request.getSession(true).getAttribute("user") ).getUserId() );
		cart.setImage(product.getFileName());
		cart.setProd_name(product.getProdName());
		cart.setProd_detail(product.getProdDetail());
		cart.setAmount(Integer.parseInt(request.getParameter("amount")));
		cart.setPrice(product.getPrice());
		
		System.out.println("AddCartAction cart : " + cart.toString());
		
		// 같은 상품이 있는지 비교하는 리스트
		CartService service = new CartServiceImpl();
		List<Cart> cartList = service.getCartList( ( (User)request.getSession(true).getAttribute("user") ).getUserId() );
		
		//장바구니 전부를 가져와서 상품번호가 같다면 수량추가
		boolean isProdNo = false;
		for (int i = 0; i < cartList.size(); i++) {
			System.out.println(cartList.get(i).getProd_no());
			System.out.println(Integer.parseInt(request.getParameter("prod_no")));
			if(cartList.get(i).getProd_no() == Integer.parseInt(request.getParameter("prod_no"))){
				isProdNo = true;
				//수량 업데이트
				cart.setAmount(cartList.get(i).getAmount() + Integer.parseInt(request.getParameter("amount")));
				service.updateAmount(cart);
				System.out.println(cart.toString());
			}
		}
		
		System.out.println(!isProdNo);
		if(!isProdNo) {
			service.insertCart(cart);
		}
		cartList = service.getCartList( ( (User)request.getSession(true).getAttribute("user") ).getUserId() );
		int count = service.totalCountCartList( ( (User)request.getSession(true).getAttribute("user") ).getUserId() );
		
		request.setAttribute("list", cartList);
		request.setAttribute("count", count);

		System.out.println("[AddCartAction execute() end...]");
		return "forward:/cart/listCart.jsp";
	}

}
