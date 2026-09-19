package com.retailbilling.controller;

import com.retailbilling.service.BillScannerService;
import com.retailbilling.entity.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class BillScannerController {

    private final BillScannerService billScannerService;

    public BillScannerController(BillScannerService billScannerService) {
        this.billScannerService = billScannerService;
    }

    @PostMapping("/scan-bill")
    public ResponseEntity<List<Product>> scanBill(@RequestParam("file") MultipartFile file) {
        List<Product> extractedProducts = billScannerService.processInvoice(file);
        return ResponseEntity.ok(extractedProducts);
    }
}