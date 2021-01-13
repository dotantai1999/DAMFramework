package dto;

import annotation.*;

@Entity
@Table(name = "address")
public class AddressEntity {
    @Id
    @Column(name = "address_id")
    private int addressId;

    @Column(name = "detail")
    private String detail;

    @OneToOne
    @JoinColumn(name = "district", referenceColumnName = "district_id")
    private DistrictEntity district;

    public AddressEntity() {
    }

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

    public AddressEntity(String detail, DistrictEntity district) {
        this.detail = detail;
        this.district = district;
    }

    public DistrictEntity getDistrict() {
        return district;
    }

    public void setDistrict(DistrictEntity district) {
        this.district = district;
    }

    @Override
    public String toString() {
        return "AddressEntity{" +
                "addressId=" + addressId +
                ", detail='" + detail + '\'' +
                ", district=" + district +
                '}';
    }
}
