package org.example.seata;

import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.saga.impl.BusinessSagaServiceImpl;
import org.example.seata.service.BusinessService;
import org.example.seata.service.impl.BusinessServiceImpl;
import org.example.seata.tcc.impl.BusinessTCCServiceImpl;

import java.util.Scanner;

/**
 * Seata 分布式事务示例主类
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("=== Seata 分布式事务示例 ===");
        log.info("请确保已经启动 Seata Server 并初始化了数据库");
        log.info("数据库初始化脚本位于 resources/db_init.sql");
        
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            printMenu();
            int choice = getChoice(scanner);
            
            if (choice == 0) {
                log.info("退出程序");
                break;
            }
            
            processChoice(choice, scanner);
        }
    }
    
    private static void printMenu() {
        log.info("\n请选择要测试的分布式事务模式：");
        log.info("1. AT 模式（默认模式）");
        log.info("2. TCC 模式");
        log.info("3. SAGA 模式");
        log.info("0. 退出");
        log.info("请输入选择（0-3）：");
    }
    
    private static int getChoice(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void processChoice(int choice, Scanner scanner) {
        if (choice < 1 || choice > 3) {
            log.error("无效的选择，请重新输入");
            return;
        }
        
        log.info("请输入用户ID（默认：user1）：");
        String userId = scanner.nextLine().trim();
        if (userId.isEmpty()) {
            userId = "user1";
        }
        
        log.info("请输入商品ID（默认：product1）：");
        String productId = scanner.nextLine().trim();
        if (productId.isEmpty()) {
            productId = "product1";
        }
        
        log.info("请输入购买数量（默认：10）：");
        int count;
        try {
            String countStr = scanner.nextLine().trim();
            count = countStr.isEmpty() ? 10 : Integer.parseInt(countStr);
        } catch (NumberFormatException e) {
            count = 10;
        }
        
        log.info("用户ID: {}, 商品ID: {}, 数量: {}", userId, productId, count);
        
        BusinessService businessService = null;
        String mode = "";
        
        try {
            switch (choice) {
                case 1:
                    mode = "AT";
                    businessService = new BusinessServiceImpl();
                    Long atOrderId = businessService.createOrder(userId, productId, count);
                    log.info("AT 模式下订单创建成功，订单ID: {}", atOrderId);
                    break;
                case 2:
                    mode = "TCC";
                    businessService = new BusinessTCCServiceImpl();
                    Long tccOrderId = businessService.createOrderTCC(userId, productId, count);
                    log.info("TCC 模式下订单创建成功，订单ID: {}", tccOrderId);
                    break;
                case 3:
                    mode = "SAGA";
                    businessService = new BusinessSagaServiceImpl();
                    Long sagaOrderId = businessService.createOrderSAGA(userId, productId, count);
                    log.info("SAGA 模式下订单创建成功，订单ID: {}", sagaOrderId);
                    break;
            }
        } catch (BusinessException e) {
            log.error("{} 模式下订单创建失败: {}", mode, e.getMessage());
        } catch (Exception e) {
            log.error("{} 模式下发生未知异常: {}", mode, e.getMessage(), e);
        }
    }
}