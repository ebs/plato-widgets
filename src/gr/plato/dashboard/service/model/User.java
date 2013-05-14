package gr.plato.dashboard.service.model;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.ejb.EJBException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.codec.binary.Base64;

@Entity
@XmlRootElement
@Table(name = "Users")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private int version;

	@NotNull
	@Column(unique = true)
	private String username;

	private String password;

	private String passwordSalt;

	@Column(unique = true)
	private String authToken;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	/**
	 * Static Functions
	 */

	private static final String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@";

	private static final int PASSWORD_LENGTH = 8;

	public static String generatePassword() {
		try {
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			String pw = "";
			for (int i = 0; i < PASSWORD_LENGTH; i++) {
				int index = (int) (sr.nextDouble() * letters.length());
				pw += letters.substring(index, index + 1);
			}
			return pw;
		} catch (NoSuchAlgorithmException nsae) {
			throw new EJBException(nsae);
		}
	}

	public static String generatePasswordSalt() {
		try {
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			byte[] salt = new byte[10];
			sr.nextBytes(salt);
			return new String(Base64.encodeBase64(salt), "ISO-8859-1");
		} catch (NoSuchAlgorithmException nsae) {
			throw new EJBException(nsae);
		} catch (UnsupportedEncodingException uee) {
			throw new EJBException(uee);
		}
	}

	public static String encodePassword(String password, String salt) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] hash = md.digest(password.concat(salt).getBytes("ISO-8859-1"));
			return new String(Base64.encodeBase64(hash), "ISO-8859-1");
		} catch (NoSuchAlgorithmException nsae) {
			throw new EJBException(nsae);
		} catch (UnsupportedEncodingException uee) {
			throw new EJBException(uee);
		}
	}

	public String generateAuthenticationToken() {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] hash = md.digest((this.getUsername() + System.currentTimeMillis()).getBytes("ISO-8859-1"));
			return new String(Base64.encodeBase64(hash), "ISO-8859-1");
		} catch (NoSuchAlgorithmException nsae) {
			throw new EJBException(nsae);
		} catch (UnsupportedEncodingException uee) {
			throw new EJBException(uee);
		}
	}
}
