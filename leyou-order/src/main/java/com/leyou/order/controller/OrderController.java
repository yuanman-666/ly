package com.leyou.order.controller;

import com.leyou.dto.OrderDTO;
import com.leyou.order.pojo.Order;
import com.leyou.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("order")
@Api("订单服务接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     *
     * @param orderDTO 订单对象
     * @return 订单编号
     */
    @PostMapping
    @ApiOperation(value = "创建订单接口，返回订单编号", notes = "创建订单")
    @ApiImplicitParam(name = "orderDTO", required = true, value = "订单的json对象,包含订单条目和物流信息")
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO){

        return ResponseEntity.status(HttpStatus.OK).body(orderService.createOrder(orderDTO));
    }

    /**
     * 根据订单号查询用户订单
     *
     * @param id 订单id
     * @return 订单对象
     */
    @RequestMapping(value = "{id}",method = RequestMethod.GET)
    @ApiOperation(value = "查询订单接口，返回订单对象", notes = "创建订单")
    @ApiImplicitParam(name = "id", required = true, value = "订单的id",type = "Long")
    public ResponseEntity<Order> queryByOrderId(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderService.queryByOrderId(id));
    }
}
