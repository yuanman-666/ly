package com.leyou.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.dto.CartDTO;
import com.leyou.dto.OrderDTO;
import com.leyou.item.pojo.Sku;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.interceptor.LoginInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.utils.IdWorker;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private GoodsClient goodsClient;

    @Transactional
    public Long createOrder(OrderDTO orderDTO){

        Order order = new Order();

        //1-获取订单id
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());
        //2-获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        order.setUserId(userInfo.getId());
        order.setBuyerNick(userInfo.getUsername());
        order.setBuyerRate(0);
        //3-收货人信息,将来是根据addressId,查询用户微服务中的地址管理服务，获取地址信息
        //在这里我们就直接写固定值
        order.setReceiverState("上海市");
        order.setReceiverCity("上海市");
        order.setReceiverDistrict("奉贤区");
        order.setReceiverMobile("13900001111");
        order.setReceiverZip("200010");
        order.setReceiver("林志玲");
        order.setReceiverAddress("五四公路八维培训学校");



        Map<Long, Integer> numMap = orderDTO.getCarts().stream().
                collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));

        //4-金额
        Set<Long> idSet = numMap.keySet();
        List<Sku> skus = goodsClient.queryByIds(new ArrayList<>(idSet));
        long totalPay = 0;
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (Sku sku : skus) {
            //计算总价
            totalPay+= sku.getPrice()*numMap.get(sku.getId());
            //封装orderdetail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(sku.getId());
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(),","));
            orderDetail.setNum(numMap.get(sku.getId()));
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setTitle(sku.getTitle());
            orderDetail.setPrice(sku.getPrice());
            orderDetail.setOrderId(orderId);
            orderDetails.add(orderDetail);
        }
        order.setTotalPay(totalPay);
        //实付金额等于总金额减去优惠金额，加上邮费
        order.setActualPay(totalPay+order.getPostFee()-0);

        //5-order 写入数据库
        orderMapper.insertSelective(order);

        //6-新增orderDetail
        for (OrderDetail orderDetail : orderDetails) {
            orderDetailMapper.insertSelective(orderDetail);
        }


        //新增订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(new Date());
        orderStatus.setStatus(OrderStatusEnum.UNPAID.value());
        orderStatus.setOrderId(orderId);

        orderStatusMapper.insertSelective(orderStatus);

        //减少库存
        goodsClient.decreaseStock(orderDTO);


        return orderId;
    }

    /*
    查询order
     */
    public Order queryByOrderId(Long id) {
        //查询order
        Order order = orderMapper.selectByPrimaryKey(id);
        //查询orderDetail
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.select(orderDetail);
        //查询orderStatus
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(id);

        order.setOrderStatus(orderStatus);
        order.setDetailList(orderDetailList);

        return order;

    }
}
