package Manager;

import entity.Product;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProductMgr {

    private final Map<String, Product> products;

    public ProductMgr() {
        this.products = new ConcurrentHashMap<>();
    }

    public Product save(Product product) {
       return products.put(product.getId(), product);
    }

    public void delete(String id) {
        products.remove(id);
    }

    public Product findById(String id) {
        return products.get(id);
    }

    public List<Product> finaAll() {
        return products.values().stream().toList();
    }
}
