package crm.spring.rest.service;

import java.util.List;

import crm.spring.rest.exception.DaoException;
import crm.spring.rest.model.Order;

public interface OrderService {
	
	Order getById(Integer id) throws DaoException;

	List<Order> getAllOrders() throws DaoException;

	List<Order> getOrdersByCustomer(Integer id) throws DaoException;

	Order createOrder(Order order) throws DaoException;

	Order updateOrder(Order order) throws DaoException;

	void deleteOrder(Integer id) throws DaoException;
	
	void patchOrderLabel(Integer id, String label) throws DaoException;

}
