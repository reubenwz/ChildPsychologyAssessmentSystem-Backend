/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.security.CryptographicHelper;

/**
 *
 * @author Ong Bik Jeun
 * @author Ooi Jun Hao
 */
@Entity
public class AdminUserEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;
    @Column(nullable = false, unique = true, length = 64)
    @NotNull
    @Size(max = 64)
    @Email
    private String email;
    @Column(columnDefinition = "CHAR(32) NOT NULL")
    @NotNull
    private String password;
    @Column(columnDefinition = "CHAR(32) NOT NULL")
    private String salt;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date dob;
    @Column(nullable = false, length = 6)
    @NotNull
    private String gender;
    @Column(nullable = false)
    @NotNull
    private boolean root;
    @Column(nullable = false)
    @NotNull
    private boolean active;

    @OneToMany(mappedBy = "admin", fetch = FetchType.EAGER)
    private List<UploadEntity> doc;

    public AdminUserEntity() {
        this.doc = new ArrayList<>();
        this.salt = CryptographicHelper.getInstance().generateRandomString(32);
        this.active = true;
    }
    
    // for creation of new admin in admin system -> set random initial password
    public AdminUserEntity(String email, String name, Date dob, String gender, boolean root) {
        this();
        this.email = email;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.root = root;

        setPassword(CryptographicHelper.getInstance().generateRandomString(12));
    }

    public AdminUserEntity(String email, String name, Date dob, String gender, boolean root, String password) {
        this();
        this.email = email;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.root = root;

        setPassword(password);
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null) {
            this.password = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + this.salt));
        } else {
            this.password = null;
        }
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getname() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(dob);
        return nowAsISO;
    }
    
    public Date getRawDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public List<UploadEntity> getDoc() {
        return doc;
    }

    public void setDoc(List<UploadEntity> doc) {
        this.doc = doc;
    }

}
