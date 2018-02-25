package com.decora.persistence.model.user;

import com.decora.http.resource.Permissions;
import com.decora.persistence.model.AbstractEntity;

import javax.annotation.security.RolesAllowed;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries({
        @NamedQuery(
                name = SystemUser.FIND_ALL,
                query = "from SystemUser"
        ),
        @NamedQuery(
                name = SystemUser.FIND_BY_EMAIL,
                query = "from SystemUser where email = :email"
        )
})

@RolesAllowed(Permissions.ADMIN)
@Entity
public class SystemUser extends AbstractEntity {

    public static final String FIND_ALL = "SystemUser.findAll";
    public static final String FIND_BY_EMAIL = "SystemUser.findByEmail";

    private String name;

    private String email;

    private String password;

    private String phone;

    private String address;

    protected SystemUser() {

    }

    public SystemUser(final String theName, final String theEmail, final String thePassword, final String thePhone, final String theAddress) {
        setName(theName);
        setEmail(theEmail);
        setPassword(thePassword);
        setPhone(thePhone);
        setAddress(theAddress);
    }

    public final String getName() {
        return name;
    }

    public final void setName(final String theName) {
        name = theName;
    }

    public final String getEmail() {
        return email;
    }

    public final void setEmail(final String theEmail) {
        email = theEmail;
    }

    public final String getPassword() {
        return password;
    }

    public final void setPassword(final String thePassword) {
        password = thePassword;
    }

    public final String getPhone() {
        return phone;
    }

    public final void setPhone(final String thePhone) {
        phone = thePhone;
    }

    public final String getAddress() {
        return address;
    }

    public final void setAddress(final String theAddress) {
        address = theAddress;
    }
}

