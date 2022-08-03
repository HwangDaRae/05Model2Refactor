package com.model2.mvc.view.purchase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.cart.impl.CartServiceImpl;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;
import com.model2.mvc.service.user.UserService;
import com.model2.mvc.service.user.impl.UserServiceImpl;

public class AddPurchaseAction extends Action {

	public AddPurchaseAction() {
		System.out.println("[AddPurchaseAction default Constructor()]");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("[AddPurchaseAction execute() start...]");
		Map<String, Object> map = new HashMap<String, Object>();
		ProductService pService = new ProductServiceImpl();
		UserService uService = new UserServiceImpl();
		PurchaseService service = new PurchaseServiceImpl();
		Product productVO = new Product();
		CartService cService = new CartServiceImpl();
		ArrayList<Purchase> list = new ArrayList<Purchase>();

		Purchase purchaseVO = new Purchase();
		purchaseVO.setPaymentOption(request.getParameter("paymentOption")); // 구매방법
		purchaseVO.setReceiverName(request.getParameter("receiverName")); // 구매자이름
		purchaseVO.setReceiverPhone(request.getParameter("receiverPhone")); // 구매자연락처
		purchaseVO.setDivyAddr(request.getParameter("receiverAddr")); // 구매자주소
		purchaseVO.setDivyRequest(request.getParameter("receiverRequest")); // 구매자요청사항
		purchaseVO.setDivyDate(request.getParameter("receiverDate")); // 배송희망일자

		System.out.println("구매 : " + purchaseVO.toString());

		if (request.getParameter("prodNo") != null) {
			// 상세정보에서 구매
			String prodNo = request.getParameter("prodNo");

			// 상세정보에서 구매정보
			// purchaseVO.setAmount(Integer.parseInt(request.getParameter("amount")));

			// 상세정보에서 상품정보
			productVO = pService.getProduct(Integer.parseInt(prodNo));
			purchaseVO.setPurchaseProd(productVO);

			// 상세정보에서 유저정보
			User userVO = uService.getUser(((User) request.getSession(true).getAttribute("user")).getUserId());
			purchaseVO.setBuyer(userVO);

			// 구매한 상품의 수량정보
			purchaseVO.setAmount(Integer.parseInt(request.getParameter("amount"))); // 수량

			// 상품 수량 -= 구매한 수량
			productVO.setAmount(productVO.getAmount() - Integer.parseInt(request.getParameter("amount")));
			pService.updateProduct(productVO);

			// 재고상품수량과 구매정보를 PurchaseVO에 넣는다
			purchaseVO = service.addPurchase(purchaseVO, productVO);
			System.out.println("purchaseVO.toString() : " + purchaseVO.toString());

			list.add(purchaseVO);

			request.setAttribute("list", list);
		} else {
			// 장바구니에서 구매

			// 배열로 온 상품번호와 상품수량 출력
			String[] prodductNo = request.getParameterValues("productNo");
			String[] productAmount = request.getParameterValues("amount");

			for (int i = 0; i < prodductNo.length; i++) {
				System.out.println("상품번호 : " + prodductNo[i] + ", 상품수량 : " + productAmount[i]);
				// 상품정보 가져오기
				productVO = pService.getProduct(Integer.parseInt(prodductNo[i]));
				purchaseVO.setPurchaseProd(productVO);

				// 유저정보 가져오기
				User userVO = uService.getUser(((User) request.getSession(true).getAttribute("user")).getUserId());
				purchaseVO.setBuyer(userVO);

				// 구매한 상품의 수량정보
				purchaseVO.setAmount(Integer.parseInt(productAmount[i])); // 수량

				// 상품수량 = 상품수량 - 구매수량
				productVO.setAmount(productVO.getAmount() - Integer.parseInt(productAmount[i]));
				pService.updateProduct(productVO);

				// 재고상품수량과 구매정보를 PurchaseVO에 넣는다
				purchaseVO = service.addPurchase(purchaseVO, productVO);
				System.out.println("purchaseVO.toString() : " + purchaseVO.toString());

				list.add(purchaseVO);

				// 장바구니에서 삭제
				map.put("user_id", ((User)request.getSession(true).getAttribute("user")).getUserId());
				map.put("deleteArray", prodductNo);
				//cService.deleteCart(map);

				purchaseVO = new Purchase();
			}
		}
		request.setAttribute("list", list);
		for (Purchase purchase : list) {
			System.out.println(purchase.toString());
		}

		System.out.println("[AddPurchaseAction execute() end...]");
		return "forward:/purchase/addPurchase.jsp";
	}

	/*
	 * @Override public String execute(HttpServletRequest request,
	 * HttpServletResponse response) throws Exception {
	 * System.out.println("[AddPurchaseAction execute() start...]"); Map<String,
	 * Object> map = new HashMap<String, Object>(); ProductService pService = new
	 * ProductServiceImpl(); UserService uService = new UserServiceImpl();
	 * PurchaseService service = new PurchaseServiceImpl(); Purchase purchaseVO =
	 * new Purchase(); Product productVO = new Product(); CartService cService = new
	 * CartServiceImpl(); ArrayList<Purchase> list = new ArrayList<Purchase>();
	 * 
	 * if(request.getParameter("prodNo") != null) { //상세정보에서 구매 String prodNo =
	 * request.getParameter("prodNo");
	 * 
	 * // 상세정보에서 구매정보
	 * purchaseVO.setPaymentOption(request.getParameter("paymentOption")); //구매방법
	 * purchaseVO.setReceiverName(request.getParameter("receiverName")); //구매자이름
	 * purchaseVO.setReceiverPhone(request.getParameter("receiverPhone")); //구매자연락처
	 * purchaseVO.setDivyAddr(request.getParameter("receiverAddr")); //구매자주소
	 * purchaseVO.setDivyRequest(request.getParameter("receiverRequest")); //구매자요청사항
	 * purchaseVO.setDivyDate(request.getParameter("receiverDate")); //배송희망일자
	 * purchaseVO.setAmount(Integer.parseInt(request.getParameter("amount"))); //수량
	 * 
	 * // 상세정보에서 상품정보 productVO = pService.getProduct(Integer.parseInt(prodNo));
	 * purchaseVO.setPurchaseProd(productVO);
	 * 
	 * // 상세정보에서 유저정보 User userVO = uService.getUser(
	 * ((User)request.getSession(true).getAttribute("user")).getUserId() );
	 * purchaseVO.setBuyer(userVO);
	 * 
	 * //구매한 상품의 수량정보
	 * purchaseVO.setAmount(Integer.parseInt(request.getParameter("amount")));
	 * 
	 * // 상품 수량 -= 구매한 수량 productVO.setAmount( productVO.getAmount() -
	 * Integer.parseInt(request.getParameter("amount")) );
	 * pService.updateProduct(productVO);
	 * 
	 * //가져온상품과 구매정보를 PurchaseVO에 넣는다 purchaseVO = service.addPurchase(purchaseVO,
	 * productVO); System.out.println("purchaseVO.toString() : " +
	 * purchaseVO.toString());
	 * 
	 * list.add(purchaseVO);
	 * 
	 * request.setAttribute("list", list); }else{ //구매하기까지는 성공 장바구니에서 삭제실패 화면에 뿌리기
	 * 안됨 System.out.println("여기는 장바구니에서 구매"); //장바구니에서 구매 String[]
	 * allCheckBoxProdNo = request.getParameterValues("addPurchaseCheckBox"); int[]
	 * allCheckBoxProdNoNum = new int[allCheckBoxProdNo.length]; for (int i = 0; i <
	 * allCheckBoxProdNo.length; i++) { allCheckBoxProdNoNum[i] =
	 * Integer.parseInt(allCheckBoxProdNo[i]); }
	 * 
	 * String[] checkedProdNo = request.getParameterValues("deleteCheckBox");
	 * String[] allamountProdNo = request.getParameterValues("amount"); for (int i =
	 * 0; i < allCheckBoxProdNo.length; i++) { System.out.println("여기는 for문안"); for
	 * (int j = 0; j < checkedProdNo.length; j++) {
	 * System.out.println("여기는 for문안2");
	 * if(checkedProdNo[j].equals(allCheckBoxProdNo[i])) { Purchase purchase = new
	 * Purchase(); System.out.println("상품번호 : " + allCheckBoxProdNo[i]);
	 * System.out.println("수량 : " + allamountProdNo[i]);
	 * 
	 * //구매할 상품정보 productVO =
	 * pService.getProduct(Integer.parseInt(allCheckBoxProdNo[i]));
	 * purchase.setPurchaseProd(productVO);
	 * 
	 * //구매한 유저정보 User userVO = uService.getUser(
	 * ((User)request.getSession(true).getAttribute("user")).getUserId() );
	 * purchase.setBuyer(userVO);
	 * 
	 * //구매한 상품의 수량정보 purchase.setAmount(Integer.parseInt(allamountProdNo[i]));
	 * 
	 * // 상품 수량 -= 구매한 수량 productVO.setAmount( productVO.getAmount() -
	 * Integer.parseInt(allamountProdNo[i]) ); pService.updateProduct(productVO);
	 * 
	 * //가져온상품과 구매정보를 PurchaseVO에 넣는다 purchase = service.addPurchase(purchase,
	 * productVO); System.out.println("여기는 장바구니 구매 액션 : " + purchase.toString());
	 * System.out.println("여기는 장바구니 구매 액션 : " + productVO.toString());
	 * 
	 * //구매한 상품 장바구니에서 삭제 map.put("user_id",
	 * ((User)request.getSession(true).getAttribute("user")).getUserId()); int[]
	 * arrayProdNo = new int[1]; arrayProdNo[0] = productVO.getProdNo();
	 * map.put("deleteArray", arrayProdNo); cService.deleteCart(map);
	 * 
	 * list.add(purchase); System.out.println(list.toString());
	 * 
	 * } } }
	 * 
	 * } request.setAttribute("list", list); for (Purchase purchase : list) {
	 * System.out.println(purchase.toString()); }
	 * 
	 * System.out.println("[AddPurchaseAction execute() end...]"); return
	 * "forward:/purchase/addPurchase.jsp"; }
	 */

}
