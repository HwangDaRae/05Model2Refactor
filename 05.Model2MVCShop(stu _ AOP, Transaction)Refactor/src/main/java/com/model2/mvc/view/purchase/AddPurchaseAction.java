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
		purchaseVO.setPaymentOption(request.getParameter("paymentOption")); // ���Ź��
		purchaseVO.setReceiverName(request.getParameter("receiverName")); // �������̸�
		purchaseVO.setReceiverPhone(request.getParameter("receiverPhone")); // �����ڿ���ó
		purchaseVO.setDivyAddr(request.getParameter("receiverAddr")); // �������ּ�
		purchaseVO.setDivyRequest(request.getParameter("receiverRequest")); // �����ڿ�û����
		purchaseVO.setDivyDate(request.getParameter("receiverDate")); // ����������

		System.out.println("���� : " + purchaseVO.toString());

		if (request.getParameter("prodNo") != null) {
			// ���������� ����
			String prodNo = request.getParameter("prodNo");

			// ���������� ��������
			// purchaseVO.setAmount(Integer.parseInt(request.getParameter("amount")));

			// ���������� ��ǰ����
			productVO = pService.getProduct(Integer.parseInt(prodNo));
			purchaseVO.setPurchaseProd(productVO);

			// ���������� ��������
			User userVO = uService.getUser(((User) request.getSession(true).getAttribute("user")).getUserId());
			purchaseVO.setBuyer(userVO);

			// ������ ��ǰ�� ��������
			purchaseVO.setAmount(Integer.parseInt(request.getParameter("amount"))); // ����

			// ��ǰ ���� -= ������ ����
			productVO.setAmount(productVO.getAmount() - Integer.parseInt(request.getParameter("amount")));
			pService.updateProduct(productVO);

			// ����ǰ������ ���������� PurchaseVO�� �ִ´�
			purchaseVO = service.addPurchase(purchaseVO, productVO);
			System.out.println("purchaseVO.toString() : " + purchaseVO.toString());

			list.add(purchaseVO);

			request.setAttribute("list", list);
		} else {
			// ��ٱ��Ͽ��� ����

			// �迭�� �� ��ǰ��ȣ�� ��ǰ���� ���
			String[] prodductNo = request.getParameterValues("productNo");
			String[] productAmount = request.getParameterValues("amount");

			for (int i = 0; i < prodductNo.length; i++) {
				System.out.println("��ǰ��ȣ : " + prodductNo[i] + ", ��ǰ���� : " + productAmount[i]);
				// ��ǰ���� ��������
				productVO = pService.getProduct(Integer.parseInt(prodductNo[i]));
				purchaseVO.setPurchaseProd(productVO);

				// �������� ��������
				User userVO = uService.getUser(((User) request.getSession(true).getAttribute("user")).getUserId());
				purchaseVO.setBuyer(userVO);

				// ������ ��ǰ�� ��������
				purchaseVO.setAmount(Integer.parseInt(productAmount[i])); // ����

				// ��ǰ���� = ��ǰ���� - ���ż���
				productVO.setAmount(productVO.getAmount() - Integer.parseInt(productAmount[i]));
				pService.updateProduct(productVO);

				// ����ǰ������ ���������� PurchaseVO�� �ִ´�
				purchaseVO = service.addPurchase(purchaseVO, productVO);
				System.out.println("purchaseVO.toString() : " + purchaseVO.toString());

				list.add(purchaseVO);

				// ��ٱ��Ͽ��� ����
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
	 * if(request.getParameter("prodNo") != null) { //���������� ���� String prodNo =
	 * request.getParameter("prodNo");
	 * 
	 * // ���������� ��������
	 * purchaseVO.setPaymentOption(request.getParameter("paymentOption")); //���Ź��
	 * purchaseVO.setReceiverName(request.getParameter("receiverName")); //�������̸�
	 * purchaseVO.setReceiverPhone(request.getParameter("receiverPhone")); //�����ڿ���ó
	 * purchaseVO.setDivyAddr(request.getParameter("receiverAddr")); //�������ּ�
	 * purchaseVO.setDivyRequest(request.getParameter("receiverRequest")); //�����ڿ�û����
	 * purchaseVO.setDivyDate(request.getParameter("receiverDate")); //����������
	 * purchaseVO.setAmount(Integer.parseInt(request.getParameter("amount"))); //����
	 * 
	 * // ���������� ��ǰ���� productVO = pService.getProduct(Integer.parseInt(prodNo));
	 * purchaseVO.setPurchaseProd(productVO);
	 * 
	 * // ���������� �������� User userVO = uService.getUser(
	 * ((User)request.getSession(true).getAttribute("user")).getUserId() );
	 * purchaseVO.setBuyer(userVO);
	 * 
	 * //������ ��ǰ�� ��������
	 * purchaseVO.setAmount(Integer.parseInt(request.getParameter("amount")));
	 * 
	 * // ��ǰ ���� -= ������ ���� productVO.setAmount( productVO.getAmount() -
	 * Integer.parseInt(request.getParameter("amount")) );
	 * pService.updateProduct(productVO);
	 * 
	 * //�����»�ǰ�� ���������� PurchaseVO�� �ִ´� purchaseVO = service.addPurchase(purchaseVO,
	 * productVO); System.out.println("purchaseVO.toString() : " +
	 * purchaseVO.toString());
	 * 
	 * list.add(purchaseVO);
	 * 
	 * request.setAttribute("list", list); }else{ //�����ϱ������ ���� ��ٱ��Ͽ��� �������� ȭ�鿡 �Ѹ���
	 * �ȵ� System.out.println("����� ��ٱ��Ͽ��� ����"); //��ٱ��Ͽ��� ���� String[]
	 * allCheckBoxProdNo = request.getParameterValues("addPurchaseCheckBox"); int[]
	 * allCheckBoxProdNoNum = new int[allCheckBoxProdNo.length]; for (int i = 0; i <
	 * allCheckBoxProdNo.length; i++) { allCheckBoxProdNoNum[i] =
	 * Integer.parseInt(allCheckBoxProdNo[i]); }
	 * 
	 * String[] checkedProdNo = request.getParameterValues("deleteCheckBox");
	 * String[] allamountProdNo = request.getParameterValues("amount"); for (int i =
	 * 0; i < allCheckBoxProdNo.length; i++) { System.out.println("����� for����"); for
	 * (int j = 0; j < checkedProdNo.length; j++) {
	 * System.out.println("����� for����2");
	 * if(checkedProdNo[j].equals(allCheckBoxProdNo[i])) { Purchase purchase = new
	 * Purchase(); System.out.println("��ǰ��ȣ : " + allCheckBoxProdNo[i]);
	 * System.out.println("���� : " + allamountProdNo[i]);
	 * 
	 * //������ ��ǰ���� productVO =
	 * pService.getProduct(Integer.parseInt(allCheckBoxProdNo[i]));
	 * purchase.setPurchaseProd(productVO);
	 * 
	 * //������ �������� User userVO = uService.getUser(
	 * ((User)request.getSession(true).getAttribute("user")).getUserId() );
	 * purchase.setBuyer(userVO);
	 * 
	 * //������ ��ǰ�� �������� purchase.setAmount(Integer.parseInt(allamountProdNo[i]));
	 * 
	 * // ��ǰ ���� -= ������ ���� productVO.setAmount( productVO.getAmount() -
	 * Integer.parseInt(allamountProdNo[i]) ); pService.updateProduct(productVO);
	 * 
	 * //�����»�ǰ�� ���������� PurchaseVO�� �ִ´� purchase = service.addPurchase(purchase,
	 * productVO); System.out.println("����� ��ٱ��� ���� �׼� : " + purchase.toString());
	 * System.out.println("����� ��ٱ��� ���� �׼� : " + productVO.toString());
	 * 
	 * //������ ��ǰ ��ٱ��Ͽ��� ���� map.put("user_id",
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
