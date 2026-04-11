package Service;

import Manager.ProductMgr;
import entity.Product;
import exceptions.ProductNotFoundException;
import exceptions.ProductStillReservedException;

public class ProductService {

    private final ProductMgr productMgr;

    public ProductService(ProductMgr productMgr) {
        this.productMgr = productMgr;
    }

    public Product addProduct (Product product) {
        return productMgr.save(product);
    }

    public void removeProduct(Product product) {

        if(product.getReserveQuantity() > 0) {
            throw new ProductStillReservedException("Cannot remove product. It has " + product.getReserveQuantity() + "reservations");
        }

        productMgr.delete(product.getId());

    }

    public void addInventory(String productId, Integer quantity) {

        Product product = productMgr.findById(productId);

        if(product == null) {
            throw new ProductNotFoundException("Product not found with product Id " + productId);
        }

        int totalQ = product.getTotalQuantity() + quantity;
        product.setTotalQuantity(totalQ);
        productMgr.save(product);
    }


}

