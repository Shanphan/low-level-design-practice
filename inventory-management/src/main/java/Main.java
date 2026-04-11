import manager.ProductMgr;
import manager.ReservationMgr;
import manager.UserMgr;
import service.ProductService;
import service.ReservationService;
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
        System.out.println(p1.toString());

        Reservation reservation = reservationService.reserve(p1.getId(), u.getId(), 1);
        System.out.println(reservation.toString());
        System.out.println(p1.toString());

        reservationService.confirm(reservation.getId());
        System.out.println(reservation.toString());
        System.out.println(p1.toString());







    }
}
