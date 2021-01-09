package dto;

import annotation.Column;
import annotation.Entity;
import annotation.Id;
import annotation.Table;

@Entity
@Table(name = "address")
public class AddressEntity {
    @Id
    @Column(name = "address_id")
    private int addressId;

    @Column(name = "detail")
    private String detail;

    public AddressEntity(String detail) {
        this.detail = detail;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "AddressEntity{" +
                "addressId=" + addressId +
                ", detail='" + detail + '\'' +
                '}';
    }
}
