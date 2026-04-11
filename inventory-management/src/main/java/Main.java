import Manager.ProductMgr;
import Manager.ReservationMgr;
import Manager.UserMgr;
import Service.ProductService;
import Service.ReservationService;
import entity.Product;
import entity.Reservation;
import entity.User;

public class Main {

    public static void main(String[] args) {

        ProductMgr productMgr = new ProductMgr();
        ProductService productService = new ProductService(productMgr);

        UserMgr userMgr = new UserMgr();

        ReservationMgr reservationMgr = new ReservationMgr();
        ReservationService reservationService = new ReservationService(reservationMgr, productMgr);

        User u = new User("SP");
        userMgr.save(u);
        Product p1 = new Product("ALOO", 5);
        Product p2 = new Product("ONION", 2);

        productService.addProduct(p1);
        productService.addProduct(p2);

        Reservation reservation = reservationService.reserve(p1.getId(), u.getId(), 1);


        reservationService.confirm(reservation.getId());

        reservationService.cancel(reservation.getId());






    }
}
