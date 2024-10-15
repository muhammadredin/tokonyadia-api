package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.dto.request.ProductRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.StoreRequest;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
import io.github.muhammadredin.tokonyadiaapi.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seeder")
@RequiredArgsConstructor
public class SeederController {
    private final StoreService storeService;
    private final ProductService productService;

    final String TOKO_1 = "101cfccd-fdd0-478e-9728-39a1d27ecd48"; // Toko Maju Jaya
    final String TOKO_2 = "37d27f25-c5b1-4f96-ba5d-65c4f115cdba"; // Toko Sejahtera
    final String TOKO_3 = "f2e449f2-e539-48e0-9715-76a34d9e9e21"; // Toko Nusantara
    final String TOKO_4 = "921dadcd-bc52-452b-8ee3-3546e568aa1b"; // Toko Lestari
    final String TOKO_5 = "c757c1db-703b-49d7-93f5-9adaa520cd9a"; // Toko Harmoni

    @PostMapping("/store")
    public void store() {
        List<StoreRequest> stores = List.of(
                StoreRequest.builder()
                        .name("Toko Maju Jaya")
                        .address("Jl. Pahlawan No. 12")
                        .phoneNumber("081234567890")
                        .noSiup("45388221")
                        .build(),
                StoreRequest.builder()
                        .name("Toko Sejahtera")
                        .address("Jl. Merdeka No. 25")
                        .phoneNumber("082345678901")
                        .noSiup("67291033")
                        .build(),
                StoreRequest.builder()
                        .name("Toko Nusantara")
                        .address("Jl. Sudirman No. 45")
                        .phoneNumber("083456789012")
                        .noSiup("99834567")
                        .build(),
                StoreRequest.builder()
                        .name("Toko Lestari")
                        .address("Jl. Diponegoro No. 89")
                        .phoneNumber("084567890123")
                        .noSiup("12345678")
                        .build(),
                StoreRequest.builder()
                        .name("Toko Harmoni")
                        .address("Jl. Gajah Mada No. 30")
                        .phoneNumber("085678901234")
                        .noSiup("78561234")
                        .build()
        );

        for (StoreRequest storeRequest : stores) {
            storeService.createStore(storeRequest);
        }
    }

    @PostMapping("/product")
    public void product() {
        List<ProductRequest> products = List.of(
                // Products for TOKO_1
                ProductRequest.builder().name("Choco Delight").description("Chocolate-filled biscuit").price(6000).stock(10).storeId(TOKO_1).build(),
                ProductRequest.builder().name("Vanilla Wafer").description("Crispy vanilla wafer").price(4500).stock(15).storeId(TOKO_1).build(),
                ProductRequest.builder().name("Strawberry Cream").description("Biscuit with strawberry filling").price(5000).stock(8).storeId(TOKO_1).build(),
                ProductRequest.builder().name("Oatmeal Cookie").description("Healthy oatmeal cookie").price(7000).stock(7).storeId(TOKO_1).build(),
                ProductRequest.builder().name("Coconut Crunch").description("Crunchy coconut biscuit").price(5300).stock(9).storeId(TOKO_1).build(),
                ProductRequest.builder().name("Almond Biscuit").description("Biscuit with almond slices").price(7500).stock(6).storeId(TOKO_1).build(),
                ProductRequest.builder().name("Butter Biscuit").description("Classic buttery biscuit").price(5000).stock(5).storeId(TOKO_1).build(),
                ProductRequest.builder().name("Honey Graham").description("Honey-flavored crackers").price(4800).stock(10).storeId(TOKO_1).build(),
                ProductRequest.builder().name("Cheese Biscuit").description("Savoury cheesy biscuit").price(5200).stock(12).storeId(TOKO_1).build(),
                ProductRequest.builder().name("Ginger Snap").description("Spicy ginger snap biscuit").price(6200).stock(15).storeId(TOKO_1).build(),

                // Products for TOKO_2
                ProductRequest.builder().name("Choco Delight").description("Chocolate-filled biscuit").price(6000).stock(8).storeId(TOKO_2).build(),
                ProductRequest.builder().name("Caramel Cookie").description("Soft cookie with caramel filling").price(5400).stock(9).storeId(TOKO_2).build(),
                ProductRequest.builder().name("Lemon Biscuit").description("Refreshing lemon-flavored biscuit").price(4700).stock(7).storeId(TOKO_2).build(),
                ProductRequest.builder().name("Vanilla Wafer").description("Crispy vanilla wafer").price(4500).stock(15).storeId(TOKO_2).build(),
                ProductRequest.builder().name("Butter Biscuit").description("Classic buttery biscuit").price(5000).stock(10).storeId(TOKO_2).build(),
                ProductRequest.builder().name("Peanut Butter Biscuit").description("Biscuit with peanut butter filling").price(5100).stock(8).storeId(TOKO_2).build(),
                ProductRequest.builder().name("Raisin Cookie").description("Cookie with raisins").price(6800).stock(5).storeId(TOKO_2).build(),
                ProductRequest.builder().name("Cinnamon Biscuit").description("Crispy biscuit with cinnamon flavor").price(5900).stock(7).storeId(TOKO_2).build(),
                ProductRequest.builder().name("Cheese Biscuit").description("Savoury cheesy biscuit").price(5200).stock(9).storeId(TOKO_2).build(),
                ProductRequest.builder().name("Hazelnut Biscuit").description("Crunchy hazelnut-flavored biscuit").price(6200).stock(6).storeId(TOKO_2).build(),

                // Products for TOKO_3
                ProductRequest.builder().name("Peanut Butter Biscuit").description("Biscuit with peanut butter filling").price(5100).stock(12).storeId(TOKO_3).build(),
                ProductRequest.builder().name("Lemon Biscuit").description("Refreshing lemon-flavored biscuit").price(4700).stock(6).storeId(TOKO_3).build(),
                ProductRequest.builder().name("Caramel Cookie").description("Soft cookie with caramel filling").price(5400).stock(10).storeId(TOKO_3).build(),
                ProductRequest.builder().name("Coconut Crunch").description("Crunchy coconut biscuit").price(5300).stock(8).storeId(TOKO_3).build(),
                ProductRequest.builder().name("Oatmeal Cookie").description("Healthy oatmeal cookie").price(7000).stock(7).storeId(TOKO_3).build(),
                ProductRequest.builder().name("Hazelnut Biscuit").description("Crunchy hazelnut-flavored biscuit").price(6200).stock(4).storeId(TOKO_3).build(),
                ProductRequest.builder().name("Butter Biscuit").description("Classic buttery biscuit").price(5000).stock(6).storeId(TOKO_3).build(),
                ProductRequest.builder().name("Vanilla Wafer").description("Crispy vanilla wafer").price(4500).stock(12).storeId(TOKO_3).build(),
                ProductRequest.builder().name("Raisin Cookie").description("Cookie with raisins").price(6800).stock(9).storeId(TOKO_3).build(),
                ProductRequest.builder().name("Ginger Snap").description("Spicy ginger snap biscuit").price(6200).stock(5).storeId(TOKO_3).build(),

                // Products for TOKO_4
                ProductRequest.builder().name("Choco Delight").description("Chocolate-filled biscuit").price(6000).stock(9).storeId(TOKO_4).build(),
                ProductRequest.builder().name("Cinnamon Biscuit").description("Crispy biscuit with cinnamon flavor").price(5900).stock(5).storeId(TOKO_4).build(),
                ProductRequest.builder().name("Cheese Biscuit").description("Savoury cheesy biscuit").price(5200).stock(8).storeId(TOKO_4).build(),
                ProductRequest.builder().name("Honey Graham").description("Honey-flavored crackers").price(4800).stock(7).storeId(TOKO_4).build(),
                ProductRequest.builder().name("Raisin Cookie").description("Cookie with raisins").price(6800).stock(5).storeId(TOKO_4).build(),
                ProductRequest.builder().name("Coconut Crunch").description("Crunchy coconut biscuit").price(5300).stock(7).storeId(TOKO_4).build(),
                ProductRequest.builder().name("Hazelnut Biscuit").description("Crunchy hazelnut-flavored biscuit").price(6200).stock(6).storeId(TOKO_4).build(),
                ProductRequest.builder().name("Peanut Butter Biscuit").description("Biscuit with peanut butter filling").price(5100).stock(9).storeId(TOKO_4).build(),
                ProductRequest.builder().name("Oatmeal Cookie").description("Healthy oatmeal cookie").price(7000).stock(5).storeId(TOKO_4).build(),
                ProductRequest.builder().name("Almond Biscuit").description("Biscuit with almond slices").price(7500).stock(7).storeId(TOKO_4).build(),

                // Products for TOKO_5
                ProductRequest.builder().name("Choco Delight").description("Chocolate-filled biscuit").price(6000).stock(10).storeId(TOKO_5).build(),
                ProductRequest.builder().name("Vanilla Wafer").description("Crispy vanilla wafer").price(4500).stock(14).storeId(TOKO_5).build(),
                ProductRequest.builder().name("Cinnamon Biscuit").description("Crispy biscuit with cinnamon flavor").price(5900).stock(8).storeId(TOKO_5).build(),
                ProductRequest.builder().name("Honey Graham").description("Honey-flavored crackers").price(4800).stock(6).storeId(TOKO_5).build(),
                ProductRequest.builder().name("Butter Biscuit").description("Classic buttery biscuit").price(5000).stock(5).storeId(TOKO_5).build(),
                ProductRequest.builder().name("Strawberry Cream").description("Biscuit with strawberry filling").price(5000).stock(7).storeId(TOKO_5).build(),
                ProductRequest.builder().name("Oatmeal Cookie").description("Healthy oatmeal cookie").price(7000).stock(9).storeId(TOKO_5).build(),
                ProductRequest.builder().name("Caramel Cookie").description("Soft cookie with caramel filling").price(5400).stock(7).storeId(TOKO_5).build(),
                ProductRequest.builder().name("Hazelnut Biscuit").description("Crunchy hazelnut-flavored biscuit").price(6200).stock(6).storeId(TOKO_5).build(),
                ProductRequest.builder().name("Peanut Butter Biscuit").description("Biscuit with peanut butter filling").price(5100).stock(11).storeId(TOKO_5).build()
        );

        for (ProductRequest product: products) {
            productService.createProduct(product);
        }

    }
}
