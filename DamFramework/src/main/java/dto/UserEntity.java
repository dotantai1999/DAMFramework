package dto;

import annotation.*;

import java.sql.Date;

@Entity
@Table(name = "user")
public class UserEntity {
	@Id
	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "password")
	private String password;

	@Column(name = "email")
	private String email;

	@Column(name = "registered_date")
	private Date registeredDate;

	@OneToOne
	@Column(name = "address")
	private AddressEntity address;


	public UserEntity(String userName, String password, String email, Date registeredDate, AddressEntity address) {
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.registeredDate = registeredDate;
		this.address = address;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}

	public AddressEntity getAddress() {
		return address;
	}

	public void setAddress(AddressEntity address) {
		this.address = address;
	}
}
