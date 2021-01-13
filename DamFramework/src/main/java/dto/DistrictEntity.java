package dto;

import annotation.Column;
import annotation.Entity;
import annotation.Id;
import annotation.Table;

@Entity
@Table(name = "district")
public class DistrictEntity {
    @Id
    @Column(name = "district_id")
    private int districtId;

    @Column(name = "name")
    private String name;

    public DistrictEntity() {
    }

    public DistrictEntity(String name) {
        this.name = name;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DistrictEntity{" +
                "districtId=" + districtId +
                ", name='" + name + '\'' +
                '}';
    }
}
